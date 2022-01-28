



package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.item.*;
import com.gamesense.api.util.combat.*;
import com.gamesense.api.util.world.*;
import java.util.*;
import net.minecraft.util.math.*;
import com.gamesense.client.module.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class AntiCrystal extends Module
{
    Setting.Double rangePlace;
    Setting.Double damageMin;
    Setting.Double enemyRange;
    Setting.Double biasDamage;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;
    Setting.Boolean rotate;
    Setting.Boolean offHandMode;
    Setting.Boolean onlyIfEnemy;
    Setting.Boolean nonAbusive;
    Setting.Boolean checkDamage;
    Setting.Boolean switchBack;
    Setting.Boolean notOurCrystals;
    Setting.Boolean chatMsg;
    private int delayTimeTicks;
    private boolean isSneaking;
    
    public AntiCrystal() {
        super("AntiCrystal", Module.Category.Combat);
        this.isSneaking = false;
    }
    
    public void setup() {
        this.rangePlace = this.registerDouble("Range Place", 5.9, 0.0, 6.0);
        this.enemyRange = this.registerDouble("Enemy Range", 12.0, 0.0, 20.0);
        this.damageMin = this.registerDouble("Damage Min", 4.0, 0.0, 15.0);
        this.biasDamage = this.registerDouble("Bias Damage", 1.0, 0.0, 3.0);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
        this.offHandMode = this.registerBoolean("OffHand Mode", true);
        this.rotate = this.registerBoolean("Rotate", false);
        this.onlyIfEnemy = this.registerBoolean("Only If Enemy", true);
        this.nonAbusive = this.registerBoolean("Non Abusive", true);
        this.checkDamage = this.registerBoolean("Damage Check", true);
        this.switchBack = this.registerBoolean("Switch Back", true);
        this.notOurCrystals = this.registerBoolean("Ignore AutoCrystal", true);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
    }
    
    public void onEnable() {
        this.delayTimeTicks = 0;
        if (this.chatMsg.getValue()) {
            PistonCrystal.printChat("AntiCrystal turned ON!", false);
        }
    }
    
    public void onDisable() {
        if (this.chatMsg.getValue()) {
            PistonCrystal.printChat("AntiCrystal turned Off!", true);
        }
        if (this.isSneaking) {
            AntiCrystal.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AntiCrystal.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
    }
    
    public void onUpdate() {
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (this.onlyIfEnemy.getValue()) {
            if (AntiCrystal.mc.world.playerEntities.size() <= 1) {
                return;
            }
            boolean found = false;
            for (final EntityPlayer check : AntiCrystal.mc.world.playerEntities) {
                if (check != AntiCrystal.mc.player && AntiCrystal.mc.player.getDistanceToEntity((Entity)check) <= this.enemyRange.getValue()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return;
            }
        }
        int blocksPlaced = 0;
        boolean pressureSwitch = true;
        int slotPressure = -1;
        for (final Entity t : AntiCrystal.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && AntiCrystal.mc.player.getDistanceToEntity(t) <= this.rangePlace.getValue()) {
                if (pressureSwitch) {
                    if (this.offHandMode.getValue() && isOffHandPressure()) {
                        slotPressure = 9;
                    }
                    else if ((slotPressure = this.getHotBarPressure()) == -1) {
                        return;
                    }
                    pressureSwitch = false;
                }
                if (!this.notOurCrystals.getValue() && this.usCrystal(t)) {
                    return;
                }
                if (this.checkDamage.getValue()) {
                    final float damage = (float)(DamageUtil.calculateDamage(t.posX, t.posY, t.posZ, (Entity)AntiCrystal.mc.player) * this.biasDamage.getValue());
                    if (damage < this.damageMin.getValue() && damage < AntiCrystal.mc.player.getHealth()) {
                        return;
                    }
                }
                if (BlockUtil.getBlock(t.posX, t.posY, t.posZ) instanceof BlockAir) {
                    this.placeBlock(new BlockPos(t.posX, t.posY, t.posZ), slotPressure);
                    if (++blocksPlaced == this.blocksPerTick.getValue()) {
                        return;
                    }
                }
                if (!this.isSneaking) {
                    continue;
                }
                AntiCrystal.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AntiCrystal.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
    }
    
    public boolean usCrystal(final Entity crystal) {
        return AutoCrystalGS.PlacedCrystals.contains(new BlockPos((double)(int)crystal.posX, crystal.posY - 1.0, (double)(int)crystal.posZ));
    }
    
    public static boolean isOffHandPressure() {
        return OffHand.nonDefaultItem.getValue().equals("Plates") || OffHand.defaultItem.getValue().equals("Plates");
    }
    
    private void placeBlock(final BlockPos pos, final int slotPressure) {
        int oldSlot = -1;
        final EnumFacing side = EnumFacing.DOWN;
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = AntiCrystal.mc.world.getBlockState(neighbour).getBlock();
        if (slotPressure != 9 && AntiCrystal.mc.player.inventory.currentItem != slotPressure) {
            if (this.nonAbusive.getValue()) {
                return;
            }
            if (this.switchBack.getValue()) {
                oldSlot = AntiCrystal.mc.player.inventory.currentItem;
            }
            AntiCrystal.mc.player.inventory.currentItem = slotPressure;
        }
        if ((!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            AntiCrystal.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AntiCrystal.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled((Class)AutoCrystalGS.class)) {
            AutoCrystalGS.stopAC = true;
            stoppedAC = true;
        }
        if (this.rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec, Boolean.valueOf(true));
        }
        EnumHand swingHand = EnumHand.MAIN_HAND;
        if (slotPressure == 9) {
            swingHand = EnumHand.OFF_HAND;
            if (!this.isPressure(AntiCrystal.mc.player.getHeldItemOffhand())) {
                return;
            }
        }
        else if (!this.isPressure(AntiCrystal.mc.player.getHeldItemMainhand())) {
            return;
        }
        AntiCrystal.mc.playerController.processRightClickBlock(AntiCrystal.mc.player, AntiCrystal.mc.world, neighbour, opposite, hitVec, swingHand);
        AntiCrystal.mc.player.swingArm(swingHand);
        if (this.switchBack.getValue() && oldSlot != -1) {
            AntiCrystal.mc.player.inventory.currentItem = oldSlot;
        }
        if (stoppedAC) {
            AutoCrystalGS.stopAC = false;
            stoppedAC = false;
        }
    }
    
    private boolean isPressure(final ItemStack stack) {
        return stack != ItemStack.field_190927_a && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() instanceof BlockPressurePlate;
    }
    
    private int getHotBarPressure() {
        for (int i = 0; i < 9; ++i) {
            if (this.isPressure(AntiCrystal.mc.player.inventory.getStackInSlot(i))) {
                return i;
            }
        }
        return -1;
    }
}
