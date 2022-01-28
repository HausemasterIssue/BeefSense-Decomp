



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;
import net.minecraft.block.*;
import com.gamesense.api.util.player.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

public class SelfWeb extends Module
{
    Setting.Boolean chatMsg;
    Setting.Boolean shiftOnly;
    Setting.Boolean singleWeb;
    Setting.Boolean rotate;
    Setting.Boolean disableNone;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;
    Setting.Mode placeType;
    private boolean noWeb;
    private boolean isSneaking;
    private boolean firstRun;
    private int blocksPlaced;
    private int delayTimeTicks;
    private int offsetSteps;
    private int oldSlot;
    
    public SelfWeb() {
        super("SelfWeb", Module.Category.Combat);
        this.noWeb = false;
        this.isSneaking = false;
        this.firstRun = false;
        this.delayTimeTicks = 0;
        this.offsetSteps = 0;
        this.oldSlot = -1;
    }
    
    public void setup() {
        final ArrayList<String> placeModes = new ArrayList<String>();
        placeModes.add("Single");
        placeModes.add("Double");
        this.placeType = this.registerMode("Place", (List)placeModes, "Single");
        this.shiftOnly = this.registerBoolean("Shift Only", false);
        this.singleWeb = this.registerBoolean("One Place", false);
        this.disableNone = this.registerBoolean("Disable No Web", true);
        this.rotate = this.registerBoolean("Rotate", true);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
    }
    
    public void onEnable() {
        PlacementUtil.onEnable();
        if (SelfWeb.mc.player == null) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "SelfWeb turned ON!");
        }
        this.oldSlot = SelfWeb.mc.player.inventory.currentItem;
        final int newSlot = InventoryUtil.findFirstBlockSlot((Class)BlockWeb.class, 0, 8);
        if (newSlot != -1) {
            SelfWeb.mc.player.inventory.currentItem = newSlot;
        }
    }
    
    public void onDisable() {
        PlacementUtil.onDisable();
        if (SelfWeb.mc.player == null) {
            return;
        }
        if (this.chatMsg.getValue()) {
            if (this.noWeb) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No web detected... SelfWeb turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "SelfWeb turned OFF!");
            }
        }
        if (this.isSneaking) {
            SelfWeb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)SelfWeb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != SelfWeb.mc.player.inventory.currentItem && this.oldSlot != -1) {
            SelfWeb.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
        this.noWeb = false;
        this.firstRun = true;
        AutoCrystalGS.stopAC = false;
    }
    
    public void onUpdate() {
        if (SelfWeb.mc.player == null) {
            this.disable();
            return;
        }
        if (this.disableNone.getValue() && this.noWeb) {
            this.disable();
            return;
        }
        if (SelfWeb.mc.player.posY <= 0.0) {
            return;
        }
        if (this.singleWeb.getValue() && this.blocksPlaced >= 1) {
            this.blocksPlaced = 0;
            this.disable();
            return;
        }
        if (this.firstRun) {
            this.firstRun = false;
            if (InventoryUtil.findFirstBlockSlot((Class)BlockWeb.class, 0, 8) == -1) {
                this.noWeb = true;
                this.disable();
            }
        }
        else {
            if (this.delayTimeTicks < this.tickDelay.getValue()) {
                ++this.delayTimeTicks;
                return;
            }
            this.delayTimeTicks = 0;
        }
        if (this.shiftOnly.getValue() && !SelfWeb.mc.player.isSneaking()) {
            return;
        }
        this.blocksPlaced = 0;
        while (this.blocksPlaced <= this.blocksPerTick.getValue()) {
            Vec3d[] offsetPattern;
            int maxSteps;
            if (this.placeType.getValue().equalsIgnoreCase("Double")) {
                offsetPattern = Offsets.DOUBLE;
                maxSteps = Offsets.DOUBLE.length;
            }
            else {
                offsetPattern = Offsets.SINGLE;
                maxSteps = Offsets.SINGLE.length;
            }
            if (this.offsetSteps >= maxSteps) {
                this.offsetSteps = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(offsetPattern[this.offsetSteps]);
            final BlockPos targetPos = new BlockPos(SelfWeb.mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean tryPlacing = true;
            if (!SelfWeb.mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                tryPlacing = false;
            }
            if (tryPlacing && PlacementUtil.placeBlock(targetPos, EnumHand.MAIN_HAND, this.rotate.getValue(), (Class)BlockWeb.class)) {
                ++this.blocksPlaced;
            }
            else if (InventoryUtil.findFirstBlockSlot((Class)BlockWeb.class, 0, 8) == -1) {
                this.noWeb = true;
                this.disable();
            }
            ++this.offsetSteps;
            if (!this.isSneaking) {
                continue;
            }
            SelfWeb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)SelfWeb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
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
