



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;
import net.minecraft.block.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import com.gamesense.api.util.player.*;
import java.util.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

public class AutoWeb extends Module
{
    Setting.Mode trapType;
    Setting.Boolean chatMsg;
    Setting.Boolean rotate;
    Setting.Boolean disableNone;
    Setting.Integer enemyRange;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;
    private boolean noWeb;
    private boolean isSneaking;
    private boolean firstRun;
    private int delayTimeTicks;
    private int offsetSteps;
    private int oldSlot;
    
    public AutoWeb() {
        super("AutoWeb", Module.Category.Combat);
        this.noWeb = false;
        this.isSneaking = false;
        this.firstRun = false;
        this.delayTimeTicks = 0;
        this.offsetSteps = 0;
        this.oldSlot = -1;
    }
    
    public void setup() {
        final ArrayList<String> trapTypes = new ArrayList<String>();
        trapTypes.add("Single");
        trapTypes.add("Double");
        this.trapType = this.registerMode("Mode", (List)trapTypes, "Double");
        this.disableNone = this.registerBoolean("Disable No Web", true);
        this.rotate = this.registerBoolean("Rotate", true);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
        this.enemyRange = this.registerInteger("Range", 4, 0, 6);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
    }
    
    public void onEnable() {
        PlacementUtil.onEnable();
        if (AutoWeb.mc.player == null) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoWeb turned ON!");
        }
        this.oldSlot = AutoWeb.mc.player.inventory.currentItem;
        final int newSlot = InventoryUtil.findFirstBlockSlot((Class)BlockWeb.class, 0, 8);
        if (newSlot != -1) {
            AutoWeb.mc.player.inventory.currentItem = newSlot;
        }
    }
    
    public void onDisable() {
        PlacementUtil.onDisable();
        if (AutoWeb.mc.player == null) {
            return;
        }
        if (this.chatMsg.getValue()) {
            if (this.noWeb) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No web detected... AutoWeb turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "AutoWeb turned OFF!");
            }
        }
        if (this.isSneaking) {
            AutoWeb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoWeb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != AutoWeb.mc.player.inventory.currentItem && this.oldSlot != -1) {
            AutoWeb.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
        this.noWeb = false;
        this.firstRun = true;
        AutoCrystalGS.stopAC = false;
    }
    
    public void onUpdate() {
        if (AutoWeb.mc.player == null) {
            this.disable();
            return;
        }
        if (this.disableNone.getValue() && this.noWeb) {
            this.disable();
            return;
        }
        final EntityPlayer closestTarget = PlayerUtil.findClosestTarget();
        if (closestTarget == null) {
            return;
        }
        if (this.firstRun) {
            this.firstRun = false;
            if (InventoryUtil.findFirstBlockSlot((Class)BlockWeb.class, 0, 8) == -1) {
                this.noWeb = true;
            }
        }
        else {
            if (this.delayTimeTicks < this.tickDelay.getValue()) {
                ++this.delayTimeTicks;
                return;
            }
            this.delayTimeTicks = 0;
        }
        int blocksPlaced = 0;
        while (blocksPlaced <= this.blocksPerTick.getValue()) {
            final List<Vec3d> placeTargets = new ArrayList<Vec3d>();
            int maxSteps;
            if (this.trapType.getValue().equalsIgnoreCase("Single")) {
                Collections.addAll(placeTargets, Offsets.SINGLE);
                maxSteps = Offsets.SINGLE.length;
            }
            else {
                Collections.addAll(placeTargets, Offsets.DOUBLE);
                maxSteps = Offsets.DOUBLE.length;
            }
            if (this.offsetSteps >= maxSteps) {
                this.offsetSteps = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos((Vec3d)placeTargets.get(this.offsetSteps));
            final BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean tryPlacing = true;
            if (!AutoWeb.mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                tryPlacing = false;
            }
            if (tryPlacing && this.placeBlock(targetPos, this.enemyRange.getValue())) {
                ++blocksPlaced;
            }
            else if (InventoryUtil.findFirstBlockSlot((Class)BlockWeb.class, 0, 8) == -1) {
                this.noWeb = true;
                this.disable();
            }
            ++this.offsetSteps;
            if (!this.isSneaking) {
                continue;
            }
            AutoWeb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoWeb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
    }
    
    private boolean placeBlock(final BlockPos pos, final int range) {
        return AutoWeb.mc.player.getDistanceSq(pos) <= range * range && PlacementUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), (Class)BlockWeb.class);
    }
    
    private static class Offsets
    {
        private static final Vec3d[] SINGLE;
        private static final Vec3d[] DOUBLE;
        
        static {
            SINGLE = new Vec3d[] { new Vec3d(0.0, 0.0, 0.0) };
            DOUBLE = new Vec3d[] { new Vec3d(0.0, 0.0, 0.0), new Vec3d(0.0, 1.0, 0.0) };
        }
    }
}
