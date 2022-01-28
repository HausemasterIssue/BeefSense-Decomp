



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

public class Surround extends Module
{
    Setting.Boolean chatMsg;
    Setting.Boolean triggerSurround;
    Setting.Boolean shiftOnly;
    Setting.Boolean rotate;
    Setting.Boolean disableNone;
    Setting.Boolean disableOnJump;
    Setting.Boolean offHandObby;
    Setting.Boolean cityBlocker;
    Setting.Boolean centerPlayer;
    Setting.Integer tickDelay;
    Setting.Integer timeOutTicks;
    Setting.Integer blocksPerTick;
    private boolean noObby;
    private boolean isSneaking;
    private boolean firstRun;
    private boolean activedOff;
    private int oldSlot;
    private int runTimeTicks;
    private int delayTimeTicks;
    private int offsetSteps;
    private Vec3d centeredBlock;
    
    public Surround() {
        super("Surround", Module.Category.Combat);
        this.noObby = false;
        this.isSneaking = false;
        this.firstRun = false;
        this.oldSlot = -1;
        this.runTimeTicks = 0;
        this.delayTimeTicks = 0;
        this.offsetSteps = 0;
        this.centeredBlock = Vec3d.ZERO;
    }
    
    public void setup() {
        this.triggerSurround = this.registerBoolean("Triggerable", false);
        this.shiftOnly = this.registerBoolean("Shift Only", false);
        this.cityBlocker = this.registerBoolean("City Blocker", false);
        this.disableNone = this.registerBoolean("Disable No Obby", true);
        this.disableOnJump = this.registerBoolean("Disable On Jump", false);
        this.rotate = this.registerBoolean("Rotate", true);
        this.offHandObby = this.registerBoolean("Off Hand Obby", false);
        this.centerPlayer = this.registerBoolean("Center Player", false);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.timeOutTicks = this.registerInteger("Timeout Ticks", 40, 1, 100);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
    }
    
    public void onEnable() {
        PlacementUtil.onEnable();
        if (Surround.mc.player == null) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "Surround turned ON!");
        }
        if (this.centerPlayer.getValue() && Surround.mc.player.onGround) {
            Surround.mc.player.motionX = 0.0;
            Surround.mc.player.motionZ = 0.0;
        }
        this.centeredBlock = BlockUtil.getCenterOfBlock(Surround.mc.player.posX, Surround.mc.player.posY, Surround.mc.player.posY);
        this.oldSlot = Surround.mc.player.inventory.currentItem;
    }
    
    public void onDisable() {
        PlacementUtil.onDisable();
        if (Surround.mc.player == null) {
            return;
        }
        if (this.chatMsg.getValue()) {
            if (this.noObby) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No obsidian detected... Surround turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Surround turned OFF!");
            }
        }
        if (this.isSneaking) {
            Surround.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Surround.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != Surround.mc.player.inventory.currentItem && this.oldSlot != -1 && this.oldSlot != 9) {
            Surround.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
        this.centeredBlock = Vec3d.ZERO;
        this.noObby = false;
        this.firstRun = true;
        AutoCrystalGS.stopAC = false;
        if (this.offHandObby.getValue() && OffHand.isActive()) {
            OffHand.removeObsidian();
            this.activedOff = false;
        }
    }
    
    public void onUpdate() {
        if (Surround.mc.player == null) {
            this.disable();
            return;
        }
        if (this.disableNone.getValue() && this.noObby) {
            this.disable();
            return;
        }
        if (Surround.mc.player.posY <= 0.0) {
            return;
        }
        if (this.firstRun) {
            this.firstRun = false;
            if (InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedOff) == -1) {
                this.noObby = true;
                this.disable();
            }
            else {
                this.activedOff = true;
            }
        }
        else {
            if (this.delayTimeTicks < this.tickDelay.getValue()) {
                ++this.delayTimeTicks;
                return;
            }
            this.delayTimeTicks = 0;
        }
        if (this.shiftOnly.getValue() && !Surround.mc.player.isSneaking()) {
            return;
        }
        if (this.disableOnJump.getValue() && !Surround.mc.player.onGround && !Surround.mc.player.isInWeb) {
            return;
        }
        if (this.centerPlayer.getValue() && this.centeredBlock != Vec3d.ZERO && Surround.mc.player.onGround) {
            final double xDeviation = Math.abs(this.centeredBlock.xCoord - Surround.mc.player.posX);
            final double zDeviation = Math.abs(this.centeredBlock.zCoord - Surround.mc.player.posZ);
            if (xDeviation <= 0.1 && zDeviation <= 0.1) {
                this.centeredBlock = Vec3d.ZERO;
            }
            else {
                double newX;
                if (Surround.mc.player.posX > Math.round(Surround.mc.player.posX)) {
                    newX = Math.round(Surround.mc.player.posX) + 0.5;
                }
                else if (Surround.mc.player.posX < Math.round(Surround.mc.player.posX)) {
                    newX = Math.round(Surround.mc.player.posX) - 0.5;
                }
                else {
                    newX = Surround.mc.player.posX;
                }
                double newZ;
                if (Surround.mc.player.posZ > Math.round(Surround.mc.player.posZ)) {
                    newZ = Math.round(Surround.mc.player.posZ) + 0.5;
                }
                else if (Surround.mc.player.posZ < Math.round(Surround.mc.player.posZ)) {
                    newZ = Math.round(Surround.mc.player.posZ) - 0.5;
                }
                else {
                    newZ = Surround.mc.player.posZ;
                }
                Surround.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(newX, Surround.mc.player.posY, newZ, true));
                Surround.mc.player.setPosition(newX, Surround.mc.player.posY, newZ);
            }
        }
        if (this.triggerSurround.getValue() && this.runTimeTicks >= this.timeOutTicks.getValue()) {
            this.runTimeTicks = 0;
            this.disable();
            return;
        }
        int blocksPlaced = 0;
        while (blocksPlaced <= this.blocksPerTick.getValue()) {
            Vec3d[] offsetPattern;
            int maxSteps;
            if (this.cityBlocker.getValue()) {
                offsetPattern = Offsets.CITY;
                maxSteps = Offsets.CITY.length;
            }
            else {
                offsetPattern = Offsets.SURROUND;
                maxSteps = Offsets.SURROUND.length;
            }
            if (this.offsetSteps >= maxSteps) {
                this.offsetSteps = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(offsetPattern[this.offsetSteps]);
            BlockPos targetPos = new BlockPos(Surround.mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean tryPlacing = true;
            if (Surround.mc.player.posY % 1.0 > 0.2) {
                targetPos = new BlockPos(targetPos.getX(), targetPos.getY() + 1, targetPos.getZ());
            }
            if (!Surround.mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                tryPlacing = false;
            }
            for (final Entity entity : Surround.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(targetPos))) {
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
            Surround.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Surround.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        ++this.runTimeTicks;
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
            if (!(Surround.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) || !(((ItemBlock)Surround.mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian)) {
                return false;
            }
            handSwing = EnumHand.OFF_HAND;
        }
        if (Surround.mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != 9) {
            Surround.mc.player.inventory.currentItem = obsidianSlot;
        }
        return PlacementUtil.place(pos, handSwing, this.rotate.getValue());
    }
    
    private static class Offsets
    {
        private static final Vec3d[] SURROUND;
        private static final Vec3d[] CITY;
        
        static {
            SURROUND = new Vec3d[] { new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0) };
            CITY = new Vec3d[] { new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(2.0, 0.0, 0.0), new Vec3d(-2.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 2.0), new Vec3d(0.0, 0.0, -2.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0) };
        }
    }
}
