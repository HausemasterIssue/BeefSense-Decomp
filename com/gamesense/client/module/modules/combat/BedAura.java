



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.item.*;
import com.gamesense.api.util.player.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.network.*;
import net.minecraft.init.*;
import com.gamesense.api.util.combat.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import java.util.function.*;
import com.gamesense.api.util.world.*;
import java.util.*;
import java.util.stream.*;
import net.minecraft.util.math.*;
import net.minecraft.network.play.client.*;

public class BedAura extends Module
{
    Setting.Mode attackMode;
    Setting.Double attackRange;
    Setting.Integer breakDelay;
    Setting.Integer placeDelay;
    Setting.Double targetRange;
    Setting.Boolean antiSuicide;
    Setting.Integer antiSuicideHealth;
    Setting.Integer minDamage;
    Setting.Boolean rotate;
    Setting.Boolean chatMsgs;
    Setting.Boolean disableNone;
    Setting.Boolean autoSwitch;
    private boolean hasNone;
    private int oldSlot;
    private final ArrayList<BlockPos> placedPos;
    private final Timer breakTimer;
    private final Timer placeTimer;
    
    public BedAura() {
        super("BedAura", Module.Category.Combat);
        this.hasNone = false;
        this.oldSlot = -1;
        this.placedPos = new ArrayList<BlockPos>();
        this.breakTimer = new Timer();
        this.placeTimer = new Timer();
    }
    
    public void setup() {
        final ArrayList<String> attackModes = new ArrayList<String>();
        attackModes.add("Normal");
        attackModes.add("Own");
        this.attackMode = this.registerMode("Mode", (List)attackModes, "Own");
        this.attackRange = this.registerDouble("Attack Range", 4.0, 0.0, 10.0);
        this.breakDelay = this.registerInteger("Break Delay", 1, 0, 20);
        this.placeDelay = this.registerInteger("Place Delay", 1, 0, 20);
        this.targetRange = this.registerDouble("Target Range", 7.0, 0.0, 16.0);
        this.rotate = this.registerBoolean("Rotate", true);
        this.disableNone = this.registerBoolean("Disable No Bed", false);
        this.autoSwitch = this.registerBoolean("Switch", true);
        this.antiSuicide = this.registerBoolean("Anti Suicide", false);
        this.antiSuicideHealth = this.registerInteger("Suicide Health", 14, 1, 36);
        this.minDamage = this.registerInteger("Min Damage", 5, 1, 36);
        this.chatMsgs = this.registerBoolean("Chat Msgs", true);
    }
    
    public void onEnable() {
        this.hasNone = false;
        this.placedPos.clear();
        if (BedAura.mc.player == null || BedAura.mc.world == null) {
            this.disable();
            return;
        }
        final int bedSlot = InventoryUtil.findFirstItemSlot((Class)ItemBed.class, 0, 8);
        if (BedAura.mc.player.inventory.currentItem != bedSlot && bedSlot != -1 && this.autoSwitch.getValue()) {
            this.oldSlot = BedAura.mc.player.inventory.currentItem;
            BedAura.mc.player.inventory.currentItem = bedSlot;
        }
        else if (bedSlot == -1) {
            this.hasNone = true;
        }
        if (this.chatMsgs.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "BedAura turned ON!");
        }
    }
    
    public void onDisable() {
        this.placedPos.clear();
        if (BedAura.mc.player == null || BedAura.mc.world == null) {
            return;
        }
        if (this.autoSwitch.getValue() && BedAura.mc.player.inventory.currentItem != this.oldSlot && this.oldSlot != -1) {
            BedAura.mc.player.inventory.currentItem = this.oldSlot;
        }
        if (this.chatMsgs.getValue()) {
            if (this.hasNone && this.disableNone.getValue()) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No beds detected... BedAura turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "BedAura turned OFF!");
            }
        }
        this.hasNone = false;
        this.oldSlot = -1;
    }
    
    public void onUpdate() {
        if (BedAura.mc.player == null || BedAura.mc.world == null || BedAura.mc.player.dimension == 0) {
            this.disable();
            return;
        }
        final int bedSlot = InventoryUtil.findFirstItemSlot((Class)ItemBed.class, 0, 8);
        if (BedAura.mc.player.inventory.currentItem != bedSlot && bedSlot != -1 && this.autoSwitch.getValue()) {
            this.oldSlot = BedAura.mc.player.inventory.currentItem;
            BedAura.mc.player.inventory.currentItem = bedSlot;
        }
        else if (bedSlot == -1) {
            this.hasNone = true;
        }
        if (this.antiSuicide.getValue() && BedAura.mc.player.getHealth() + BedAura.mc.player.getAbsorptionAmount() < this.antiSuicideHealth.getValue()) {
            return;
        }
        if (this.breakTimer.getTimePassed() / 50L >= this.breakDelay.getValue()) {
            this.breakTimer.reset();
            this.breakBed();
        }
        if (this.hasNone) {
            if (this.disableNone.getValue()) {
                this.disable();
            }
        }
        else {
            if (BedAura.mc.player.inventory.getStackInSlot(BedAura.mc.player.inventory.currentItem).getItem() != Items.BED) {
                return;
            }
            if (this.placeTimer.getTimePassed() / 50L >= this.placeDelay.getValue()) {
                this.placeTimer.reset();
                this.placeBed();
            }
        }
    }
    
    private void breakBed() {
        for (final TileEntity tileEntity : this.findBedEntities((EntityPlayer)BedAura.mc.player)) {
            if (!(tileEntity instanceof TileEntityBed)) {
                continue;
            }
            if (this.rotate.getValue()) {
                BlockUtil.faceVectorPacketInstant(new Vec3d((double)tileEntity.getPos().getX(), (double)tileEntity.getPos().getY(), (double)tileEntity.getPos().getZ()), Boolean.valueOf(true));
            }
            BedAura.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(tileEntity.getPos(), EnumFacing.UP, EnumHand.OFF_HAND, 0.0f, 0.0f, 0.0f));
        }
    }
    
    private void placeBed() {
        for (final EntityPlayer entityPlayer : this.findTargetEntities((EntityPlayer)BedAura.mc.player)) {
            if (entityPlayer.isDead) {
                continue;
            }
            final NonNullList<BlockPos> targetPos = this.findTargetPlacePos(entityPlayer);
            if (targetPos.size() < 1) {
                continue;
            }
            for (final BlockPos blockPos : targetPos) {
                final BlockPos targetPos2 = blockPos.up();
                if (targetPos2.getDistance((int)BedAura.mc.player.posX, (int)BedAura.mc.player.posY, (int)BedAura.mc.player.posZ) > this.attackRange.getValue()) {
                    continue;
                }
                if (BedAura.mc.world.getBlockState(targetPos2).getBlock() != Blocks.AIR) {
                    continue;
                }
                if (entityPlayer.getPosition() == targetPos2) {
                    continue;
                }
                if (DamageUtil.calculateDamage((double)targetPos2.getX(), (double)targetPos2.getY(), (double)targetPos2.getZ(), (Entity)entityPlayer) < this.minDamage.getValue()) {
                    continue;
                }
                if (BedAura.mc.world.getBlockState(targetPos2.east()).getBlock() == Blocks.AIR) {
                    this.placeBedFinal(targetPos2, 90, EnumFacing.DOWN);
                    return;
                }
                if (BedAura.mc.world.getBlockState(targetPos2.west()).getBlock() == Blocks.AIR) {
                    this.placeBedFinal(targetPos2, -90, EnumFacing.DOWN);
                    return;
                }
                if (BedAura.mc.world.getBlockState(targetPos2.north()).getBlock() == Blocks.AIR) {
                    this.placeBedFinal(targetPos2, 0, EnumFacing.DOWN);
                    return;
                }
                if (BedAura.mc.world.getBlockState(targetPos2.south()).getBlock() == Blocks.AIR) {
                    this.placeBedFinal(targetPos2, 180, EnumFacing.SOUTH);
                }
            }
        }
    }
    
    private NonNullList<TileEntity> findBedEntities(final EntityPlayer entityPlayer) {
        final NonNullList<TileEntity> bedEntities = (NonNullList<TileEntity>)NonNullList.func_191196_a();
        BedAura.mc.world.loadedTileEntityList.stream().filter(tileEntity -> tileEntity instanceof TileEntityBed).filter(tileEntity -> tileEntity.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ) <= this.attackRange.getValue() * this.attackRange.getValue()).filter(this::isOwn).forEach(bedEntities::add);
        bedEntities.sort((Comparator)Comparator.comparing(tileEntity -> tileEntity.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ)));
        return bedEntities;
    }
    
    private boolean isOwn(final TileEntity tileEntity) {
        if (this.attackMode.getValue().equalsIgnoreCase("Normal")) {
            return true;
        }
        if (this.attackMode.getValue().equalsIgnoreCase("Own")) {
            for (final BlockPos blockPos : this.placedPos) {
                if (blockPos.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= 3.0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private NonNullList<EntityPlayer> findTargetEntities(final EntityPlayer entityPlayer) {
        final NonNullList<EntityPlayer> targetEntities = (NonNullList<EntityPlayer>)NonNullList.func_191196_a();
        BedAura.mc.world.playerEntities.stream().filter(entityPlayer1 -> !EntityUtil.basicChecksEntity(entityPlayer1)).filter(entityPlayer1 -> entityPlayer1.getDistanceToEntity((Entity)entityPlayer) <= this.targetRange.getValue()).sorted(Comparator.comparing(entityPlayer1 -> entityPlayer1.getDistanceToEntity((Entity)entityPlayer))).forEach(targetEntities::add);
        return targetEntities;
    }
    
    private NonNullList<BlockPos> findTargetPlacePos(final EntityPlayer entityPlayer) {
        final NonNullList<BlockPos> targetPlacePos = (NonNullList<BlockPos>)NonNullList.func_191196_a();
        targetPlacePos.addAll((Collection)EntityUtil.getSphere(BedAura.mc.player.getPosition(), (float)this.attackRange.getValue(), (int)this.attackRange.getValue(), false, true, 0).stream().filter(this::canPlaceBed).sorted(Comparator.comparing(blockPos -> 1.0f - DamageUtil.calculateDamage((double)blockPos.up().getX(), (double)blockPos.up().getY(), (double)blockPos.up().getZ(), (Entity)entityPlayer))).collect(Collectors.toList()));
        return targetPlacePos;
    }
    
    private boolean canPlaceBed(final BlockPos blockPos) {
        return BedAura.mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR && BedAura.mc.world.getBlockState(blockPos).getBlock() != Blocks.AIR && BedAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(blockPos)).isEmpty();
    }
    
    private void placeBedFinal(final BlockPos blockPos, final int direction, final EnumFacing enumFacing) {
        BedAura.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation((float)direction, 0.0f, BedAura.mc.player.onGround));
        if (BedAura.mc.world.getBlockState(blockPos).getBlock() != Blocks.AIR) {
            return;
        }
        final BlockPos neighbourPos = blockPos.offset(enumFacing);
        final EnumFacing oppositeFacing = enumFacing.getOpposite();
        final Vec3d vec3d = new Vec3d((Vec3i)neighbourPos).addVector(0.5, 0.5, 0.5).add(new Vec3d(oppositeFacing.getDirectionVec()).scale(0.5));
        if (this.rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(vec3d, Boolean.valueOf(true));
        }
        BedAura.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BedAura.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        BedAura.mc.playerController.processRightClickBlock(BedAura.mc.player, BedAura.mc.world, neighbourPos, oppositeFacing, vec3d, EnumHand.MAIN_HAND);
        BedAura.mc.player.swingArm(EnumHand.MAIN_HAND);
        BedAura.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BedAura.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.placedPos.add(blockPos);
    }
}
