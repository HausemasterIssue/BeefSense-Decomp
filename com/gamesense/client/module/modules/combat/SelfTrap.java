



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;
import com.gamesense.api.util.world.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import com.gamesense.api.util.player.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class SelfTrap extends Module
{
    Setting.Mode trapType;
    Setting.Boolean shiftOnly;
    Setting.Boolean chatMsg;
    Setting.Boolean rotate;
    Setting.Boolean disableNone;
    Setting.Boolean offHandObby;
    Setting.Boolean centerPlayer;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;
    private boolean noObby;
    private boolean isSneaking;
    private boolean firstRun;
    private boolean activedOff;
    private int delayTimeTicks;
    private final int playerYLevel = 0;
    private int offsetSteps;
    private int oldSlot;
    private Vec3d centeredBlock;
    
    public SelfTrap() {
        super("SelfTrap", Module.Category.Combat);
        this.noObby = false;
        this.isSneaking = false;
        this.firstRun = false;
        this.delayTimeTicks = 0;
        this.offsetSteps = 0;
        this.oldSlot = -1;
        this.centeredBlock = Vec3d.ZERO;
    }
    
    public void setup() {
        final ArrayList<String> trapTypes = new ArrayList<String>();
        trapTypes.add("Normal");
        trapTypes.add("No Step");
        trapTypes.add("Simple");
        this.trapType = this.registerMode("Mode", (List)trapTypes, "Normal");
        this.shiftOnly = this.registerBoolean("Shift Only", false);
        this.disableNone = this.registerBoolean("Disable No Obby", true);
        this.rotate = this.registerBoolean("Rotate", true);
        this.offHandObby = this.registerBoolean("Off Hand Obby", false);
        this.centerPlayer = this.registerBoolean("Center Player", false);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
    }
    
    public void onEnable() {
        PlacementUtil.onEnable();
        if (SelfTrap.mc.player == null) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "SelfTrap turned ON!");
        }
        if (this.centerPlayer.getValue() && SelfTrap.mc.player.onGround) {
            SelfTrap.mc.player.motionX = 0.0;
            SelfTrap.mc.player.motionZ = 0.0;
        }
        this.centeredBlock = BlockUtil.getCenterOfBlock(SelfTrap.mc.player.posX, SelfTrap.mc.player.posY, SelfTrap.mc.player.posY);
        this.oldSlot = SelfTrap.mc.player.inventory.currentItem;
    }
    
    public void onDisable() {
        PlacementUtil.onDisable();
        if (SelfTrap.mc.player == null) {
            return;
        }
        if (this.chatMsg.getValue()) {
            if (this.noObby) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No obsidian detected... SelfTrap turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "SelfTrap turned OFF!");
            }
        }
        if (this.isSneaking) {
            SelfTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)SelfTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != SelfTrap.mc.player.inventory.currentItem && this.oldSlot != -1 && this.oldSlot != 9) {
            SelfTrap.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
        this.centeredBlock = Vec3d.ZERO;
        this.noObby = false;
        this.firstRun = true;
        AutoCrystalGS.stopAC = false;
        if (this.offHandObby.getValue() && OffHand.isActive() && this.activedOff) {
            OffHand.removeObsidian();
            this.activedOff = false;
        }
    }
    
    public void onUpdate() {
        if (SelfTrap.mc.player == null) {
            this.disable();
            return;
        }
        if (this.disableNone.getValue() && this.noObby) {
            this.disable();
            return;
        }
        if (SelfTrap.mc.player.posY <= 0.0) {
            return;
        }
        if (this.firstRun || this.noObby) {
            this.firstRun = false;
            if (InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedOff) == -1) {
                this.noObby = true;
                return;
            }
            this.noObby = false;
            this.activedOff = true;
        }
        else {
            if (this.delayTimeTicks < this.tickDelay.getValue()) {
                ++this.delayTimeTicks;
                return;
            }
            this.delayTimeTicks = 0;
        }
        if (this.shiftOnly.getValue() && !SelfTrap.mc.player.isSneaking()) {
            return;
        }
        if (this.centerPlayer.getValue() && this.centeredBlock != Vec3d.ZERO && SelfTrap.mc.player.onGround) {
            final double xDeviation = Math.abs(this.centeredBlock.xCoord - SelfTrap.mc.player.posX);
            final double zDeviation = Math.abs(this.centeredBlock.zCoord - SelfTrap.mc.player.posZ);
            if (xDeviation <= 0.1 && zDeviation <= 0.1) {
                this.centeredBlock = Vec3d.ZERO;
            }
            else {
                double newX;
                if (SelfTrap.mc.player.posX > Math.round(SelfTrap.mc.player.posX)) {
                    newX = Math.round(SelfTrap.mc.player.posX) + 0.5;
                }
                else if (SelfTrap.mc.player.posX < Math.round(SelfTrap.mc.player.posX)) {
                    newX = Math.round(SelfTrap.mc.player.posX) - 0.5;
                }
                else {
                    newX = SelfTrap.mc.player.posX;
                }
                double newZ;
                if (SelfTrap.mc.player.posZ > Math.round(SelfTrap.mc.player.posZ)) {
                    newZ = Math.round(SelfTrap.mc.player.posZ) + 0.5;
                }
                else if (SelfTrap.mc.player.posZ < Math.round(SelfTrap.mc.player.posZ)) {
                    newZ = Math.round(SelfTrap.mc.player.posZ) - 0.5;
                }
                else {
                    newZ = SelfTrap.mc.player.posZ;
                }
                SelfTrap.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(newX, SelfTrap.mc.player.posY, newZ, true));
                SelfTrap.mc.player.setPosition(newX, SelfTrap.mc.player.posY, newZ);
            }
        }
        int blocksPlaced = 0;
        while (blocksPlaced <= this.blocksPerTick.getValue()) {
            Vec3d[] offsetPattern;
            int maxSteps;
            if (this.trapType.getValue().equalsIgnoreCase("Normal")) {
                offsetPattern = Offsets.TRAP;
                maxSteps = Offsets.TRAP.length;
            }
            else if (this.trapType.getValue().equalsIgnoreCase("No Step")) {
                offsetPattern = Offsets.TRAPFULLROOF;
                maxSteps = Offsets.TRAPFULLROOF.length;
            }
            else {
                offsetPattern = Offsets.TRAPSIMPLE;
                maxSteps = Offsets.TRAPSIMPLE.length;
            }
            if (this.offsetSteps >= maxSteps) {
                this.offsetSteps = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(offsetPattern[this.offsetSteps]);
            BlockPos targetPos = new BlockPos(SelfTrap.mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            if (SelfTrap.mc.player.posY % 1.0 > 0.2) {
                targetPos = new BlockPos(targetPos.getX(), targetPos.getY() + 1, targetPos.getZ());
            }
            boolean tryPlacing = true;
            if (!SelfTrap.mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                tryPlacing = false;
            }
            for (final Entity entity : SelfTrap.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(targetPos))) {
                if (entity instanceof EntityPlayer) {
                    tryPlacing = false;
                    break;
                }
            }
            if (tryPlacing && this.placeBlock(targetPos)) {
                ++blocksPlaced;
            }
            ++this.offsetSteps;
            if (!this.isSneaking) {
                continue;
            }
            SelfTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)SelfTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
    }
    
    private boolean placeBlock(final BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        final int obsidianSlot = InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedOff);
        if (obsidianSlot == -1) {
            this.noObby = true;
            return false;
        }
        if (obsidianSlot == 9) {
            this.activedOff = true;
            if (!(SelfTrap.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) || !(((ItemBlock)SelfTrap.mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian)) {
                return false;
            }
            handSwing = EnumHand.OFF_HAND;
        }
        if (SelfTrap.mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != 9) {
            SelfTrap.mc.player.inventory.currentItem = obsidianSlot;
        }
        return PlacementUtil.place(pos, handSwing, this.rotate.getValue());
    }
    
    private static class Offsets
    {
        private static final Vec3d[] TRAP;
        private static final Vec3d[] TRAPFULLROOF;
        private static final Vec3d[] TRAPSIMPLE;
        
        static {
            TRAP = new Vec3d[] { new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 2.0, 0.0) };
            TRAPFULLROOF = new Vec3d[] { new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 2.0, 0.0), new Vec3d(0.0, 3.0, 0.0) };
            TRAPSIMPLE = new Vec3d[] { new Vec3d(-1.0, -1.0, 0.0), new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 0.0) };
        }
    }
}
