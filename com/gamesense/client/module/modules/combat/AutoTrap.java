



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.entity.player.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import com.gamesense.api.util.player.*;
import net.minecraft.util.math.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class AutoTrap extends Module
{
    Setting.Mode trapType;
    Setting.Mode target;
    Setting.Boolean chatMsg;
    Setting.Boolean rotate;
    Setting.Boolean offHandObby;
    Setting.Boolean disableNone;
    Setting.Integer enemyRange;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;
    private boolean noObby;
    private boolean isSneaking;
    private boolean firstRun;
    private boolean activedOff;
    private int oldSlot;
    private int delayTimeTicks;
    private int offsetSteps;
    private EntityPlayer aimTarget;
    
    public AutoTrap() {
        super("AutoTrap", Module.Category.Combat);
        this.noObby = false;
        this.isSneaking = false;
        this.firstRun = false;
        this.oldSlot = -1;
        this.delayTimeTicks = 0;
        this.offsetSteps = 0;
    }
    
    public void setup() {
        final ArrayList<String> trapTypes = new ArrayList<String>();
        trapTypes.add("Normal");
        trapTypes.add("No Step");
        trapTypes.add("Air");
        final ArrayList<String> targetChoose = new ArrayList<String>();
        targetChoose.add("Nearest");
        targetChoose.add("Looking");
        this.trapType = this.registerMode("Mode", (List)trapTypes, "Normal");
        this.target = this.registerMode("Target", (List)targetChoose, "Nearest");
        this.disableNone = this.registerBoolean("Disable No Obby", true);
        this.rotate = this.registerBoolean("Rotate", true);
        this.offHandObby = this.registerBoolean("Off Hand Obby", false);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
        this.enemyRange = this.registerInteger("Range", 4, 0, 6);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
    }
    
    public void onEnable() {
        PlacementUtil.onEnable();
        this.activedOff = false;
        if (AutoTrap.mc.player == null) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoTrap turned ON!");
        }
    }
    
    public void onDisable() {
        PlacementUtil.onDisable();
        if (AutoTrap.mc.player == null) {
            return;
        }
        if (this.chatMsg.getValue()) {
            if (this.noObby) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No obsidian detected... AutoTrap turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "AutoTrap turned OFF!");
            }
        }
        if (this.oldSlot != AutoTrap.mc.player.inventory.currentItem && this.oldSlot != -1) {
            AutoTrap.mc.player.inventory.currentItem = this.oldSlot;
        }
        if (this.isSneaking) {
            AutoTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        this.noObby = false;
        this.firstRun = true;
        AutoCrystalGS.stopAC = false;
        if (this.offHandObby.getValue() && OffHand.isActive()) {
            OffHand.removeObsidian();
            this.activedOff = false;
        }
    }
    
    public void onUpdate() {
        if (AutoTrap.mc.player == null) {
            this.disable();
            return;
        }
        if (this.target.getValue().equals("Nearest")) {
            this.aimTarget = PlayerUtil.findClosestTarget((double)this.enemyRange.getValue(), this.aimTarget);
        }
        else if (this.target.getValue().equals("Looking")) {
            this.aimTarget = PlayerUtil.findLookingPlayer((double)this.enemyRange.getValue());
        }
        if (this.aimTarget == null) {
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
        if (this.disableNone.getValue() && this.noObby) {
            this.disable();
            return;
        }
        int blocksPlaced = 0;
        if (!this.noObby) {
            while (blocksPlaced <= this.blocksPerTick.getValue()) {
                final List<Vec3d> placeTargets = new ArrayList<Vec3d>();
                int maxSteps;
                if (this.trapType.getValue().equalsIgnoreCase("Normal")) {
                    Collections.addAll(placeTargets, Offsets.TRAP);
                    maxSteps = Offsets.TRAP.length;
                }
                else if (this.trapType.getValue().equalsIgnoreCase("Air")) {
                    Collections.addAll(placeTargets, Offsets.AIR);
                    maxSteps = Offsets.AIR.length;
                }
                else {
                    Collections.addAll(placeTargets, Offsets.TRAPFULLROOF);
                    maxSteps = Offsets.TRAPFULLROOF.length;
                }
                if (this.offsetSteps >= maxSteps) {
                    this.offsetSteps = 0;
                    break;
                }
                final BlockPos offsetPos = new BlockPos((Vec3d)placeTargets.get(this.offsetSteps));
                BlockPos targetPos = new BlockPos(this.aimTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
                if (this.aimTarget.posY % 1.0 > 0.2) {
                    targetPos = new BlockPos(targetPos.getX(), targetPos.getY() + 1, targetPos.getZ());
                }
                boolean tryPlacing = true;
                if (!AutoTrap.mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                    tryPlacing = false;
                }
                for (final Entity entity : AutoTrap.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(targetPos))) {
                    if (entity instanceof EntityPlayer) {
                        tryPlacing = false;
                        break;
                    }
                }
                if (tryPlacing && this.placeBlock(targetPos, this.enemyRange.getValue())) {
                    ++blocksPlaced;
                }
                ++this.offsetSteps;
                if (!this.isSneaking) {
                    continue;
                }
                AutoTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
    }
    
    private boolean placeBlock(final BlockPos pos, final int range) {
        if (AutoTrap.mc.player.getDistanceSq(pos) > range * range) {
            return false;
        }
        EnumHand handSwing = EnumHand.MAIN_HAND;
        final int obsidianSlot = InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedOff);
        if (obsidianSlot == -1) {
            this.noObby = true;
            return false;
        }
        if (obsidianSlot == 9) {
            this.activedOff = true;
            if (!(AutoTrap.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) || !(((ItemBlock)AutoTrap.mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian)) {
                return false;
            }
            handSwing = EnumHand.OFF_HAND;
        }
        if (AutoTrap.mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != 9) {
            AutoTrap.mc.player.inventory.currentItem = obsidianSlot;
        }
        return PlacementUtil.place(pos, handSwing, this.rotate.getValue());
    }
    
    private static class Offsets
    {
        private static final Vec3d[] TRAP;
        private static final Vec3d[] TRAPFULLROOF;
        private static final Vec3d[] AIR;
        
        static {
            TRAP = new Vec3d[] { new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 2.0, 0.0) };
            TRAPFULLROOF = new Vec3d[] { new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 2.0, 0.0), new Vec3d(0.0, 3.0, 0.0) };
            AIR = new Vec3d[] { new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 1.0, 1.0) };
        }
    }
}
