



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.util.*;
import com.gamesense.api.util.player.*;
import com.gamesense.api.util.world.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class HoleFill extends Module
{
    Setting.Boolean chatMsgs;
    Setting.Boolean autoSwitch;
    Setting.Boolean rotate;
    Setting.Boolean disableOnFinish;
    Setting.Boolean offHandObby;
    Setting.Boolean onlyPlayer;
    Setting.Integer placeDelay;
    Setting.Integer retryDelay;
    Setting.Integer bpc;
    Setting.Double playerRange;
    Setting.Double range;
    Setting.Mode mode;
    private int delayTicks;
    private int oldHandEnable;
    private boolean activedOff;
    private int obbySlot;
    private final HashMap<BlockPos, Integer> recentPlacements;
    
    public HoleFill() {
        super("HoleFill", Module.Category.Combat);
        this.delayTicks = 0;
        this.oldHandEnable = -1;
        this.recentPlacements = new HashMap<BlockPos, Integer>();
    }
    
    public void setup() {
        final ArrayList<String> modes = new ArrayList<String>();
        modes.add("Obby");
        modes.add("Echest");
        modes.add("Both");
        modes.add("Web");
        this.mode = this.registerMode("Type", (List)modes, "Obby");
        this.placeDelay = this.registerInteger("Delay", 2, 0, 10);
        this.retryDelay = this.registerInteger("Retry Delay", 10, 0, 50);
        this.bpc = this.registerInteger("Block pre Cycle", 2, 1, 5);
        this.range = this.registerDouble("Range", 4.0, 0.0, 10.0);
        this.playerRange = this.registerDouble("Player Range", 3.0, 1.0, 6.0);
        this.onlyPlayer = this.registerBoolean("Only Player", false);
        this.rotate = this.registerBoolean("Rotate", true);
        this.autoSwitch = this.registerBoolean("Switch", true);
        this.offHandObby = this.registerBoolean("Off Hand Obby", false);
        this.chatMsgs = this.registerBoolean("Chat Msgs", true);
        this.disableOnFinish = this.registerBoolean("Disable on Finish", true);
    }
    
    public void onEnable() {
        this.activedOff = false;
        PlacementUtil.onEnable();
        if (this.chatMsgs.getValue() && HoleFill.mc.player != null) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "HoleFill turned ON!");
        }
        if (this.autoSwitch.getValue() && HoleFill.mc.player != null) {
            this.oldHandEnable = HoleFill.mc.player.inventory.currentItem;
        }
        this.obbySlot = InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedOff);
        if (this.obbySlot == 9) {
            this.activedOff = true;
        }
    }
    
    public void onDisable() {
        PlacementUtil.onDisable();
        if (this.chatMsgs.getValue() && HoleFill.mc.player != null) {
            MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "HoleFill turned OFF!");
        }
        if (this.autoSwitch.getValue() && HoleFill.mc.player != null) {
            HoleFill.mc.player.inventory.currentItem = this.oldHandEnable;
        }
        this.recentPlacements.clear();
        if (this.offHandObby.getValue() && OffHand.isActive()) {
            OffHand.removeObsidian();
            this.activedOff = false;
        }
    }
    
    public void onUpdate() {
        if (HoleFill.mc.player == null || HoleFill.mc.world == null) {
            this.disable();
            return;
        }
        this.recentPlacements.replaceAll((blockPos, integer) -> integer + 1);
        this.recentPlacements.values().removeIf(integer -> integer > this.retryDelay.getValue() * 2);
        if (this.delayTicks <= this.placeDelay.getValue() * 2) {
            ++this.delayTicks;
            return;
        }
        if (this.obbySlot == 9 && (!(HoleFill.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) || !(((ItemBlock)HoleFill.mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian))) {
            return;
        }
        if (this.autoSwitch.getValue()) {
            final int oldHand = HoleFill.mc.player.inventory.currentItem;
            final int newHand = this.findRightBlock(oldHand);
            if (newHand == -1) {
                return;
            }
            HoleFill.mc.player.inventory.currentItem = newHand;
        }
        List<BlockPos> holePos = new ArrayList<BlockPos>(this.findHoles());
        holePos.removeAll(this.recentPlacements.keySet());
        final AtomicInteger placements = new AtomicInteger();
        holePos = holePos.stream().sorted(Comparator.comparing(blockPos -> blockPos.distanceSq((double)(int)HoleFill.mc.player.posX, (double)(int)HoleFill.mc.player.posY, (double)(int)HoleFill.mc.player.posZ))).collect((Collector<? super Object, ?, List<BlockPos>>)Collectors.toList());
        final List<EntityPlayer> listPlayer = (List<EntityPlayer>)HoleFill.mc.world.playerEntities;
        listPlayer.removeIf(player -> EntityUtil.basicChecksEntity(player) || !this.onlyPlayer.getValue() || HoleFill.mc.player.getDistanceToEntity(player) > 6.0 + this.playerRange.getValue());
        final AtomicInteger atomicInteger;
        boolean output;
        boolean found;
        final List<EntityPlayer> list;
        final Iterator<EntityPlayer> iterator;
        EntityPlayer player2;
        holePos.removeIf(placePos -> {
            if (atomicInteger.get() >= this.bpc.getValue()) {
                return false;
            }
            else if (HoleFill.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(placePos)).stream().anyMatch(entity -> entity instanceof EntityPlayer)) {
                return true;
            }
            else {
                output = false;
                if (this.isHoldingRightBlock(HoleFill.mc.player.inventory.currentItem, HoleFill.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem()) || this.offHandObby.getValue()) {
                    found = false;
                    if (this.onlyPlayer.getValue()) {
                        list.iterator();
                        while (iterator.hasNext()) {
                            player2 = iterator.next();
                            if (player2.getDistanceSqToCenter(placePos) < this.playerRange.getValue() * 2.0) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            return false;
                        }
                    }
                    if (this.placeBlock(placePos)) {
                        atomicInteger.getAndIncrement();
                        output = true;
                        this.delayTicks = 0;
                    }
                    this.recentPlacements.put(placePos, 0);
                }
                return output;
            }
        });
        if (this.disableOnFinish.getValue() && holePos.size() == 0) {
            this.disable();
        }
    }
    
    private boolean placeBlock(final BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        final int obsidianSlot = InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedOff);
        if (obsidianSlot == -1) {
            return false;
        }
        if (obsidianSlot == 9) {
            this.activedOff = true;
            if (!(HoleFill.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) || !(((ItemBlock)HoleFill.mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian)) {
                return false;
            }
            handSwing = EnumHand.OFF_HAND;
        }
        if (HoleFill.mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != 9) {
            HoleFill.mc.player.inventory.currentItem = obsidianSlot;
        }
        return PlacementUtil.place(pos, handSwing, this.rotate.getValue());
    }
    
    private List<BlockPos> findHoles() {
        final NonNullList<BlockPos> holes = (NonNullList<BlockPos>)NonNullList.func_191196_a();
        final List<BlockPos> blockPosList = (List<BlockPos>)EntityUtil.getSphere(PlayerUtil.getPlayerPos(), 5.0f, 5, false, true, 0);
        for (final BlockPos blockPos : blockPosList) {
            if (HoleUtil.isHole(blockPos, true, true).getType() == HoleUtil.HoleType.SINGLE) {
                holes.add((Object)blockPos);
            }
        }
        return (List<BlockPos>)holes;
    }
    
    private int findRightBlock(final int oldHand) {
        int newHand = -1;
        if (this.mode.getValue().equalsIgnoreCase("Both")) {
            newHand = InventoryUtil.findFirstBlockSlot((Class)BlockObsidian.class, 0, 8);
            if (newHand == -1) {
                newHand = InventoryUtil.findFirstBlockSlot((Class)BlockEnderChest.class, 0, 8);
            }
        }
        else if (this.mode.getValue().equalsIgnoreCase("Obby")) {
            newHand = InventoryUtil.findFirstBlockSlot((Class)BlockObsidian.class, 0, 8);
        }
        else if (this.mode.getValue().equalsIgnoreCase("Echest")) {
            newHand = InventoryUtil.findFirstBlockSlot((Class)BlockEnderChest.class, 0, 8);
        }
        else if (this.mode.getValue().equalsIgnoreCase("Web")) {
            newHand = InventoryUtil.findFirstBlockSlot((Class)BlockEnderChest.class, 0, 8);
        }
        if (newHand == -1) {
            newHand = oldHand;
        }
        return newHand;
    }
    
    private Boolean isHoldingRightBlock(final int hand, final Item item) {
        if (hand == -1) {
            return false;
        }
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        final Block block = ((ItemBlock)item).getBlock();
        if (this.mode.getValue().equalsIgnoreCase("Obby") && block instanceof BlockObsidian) {
            return true;
        }
        if (this.mode.getValue().equalsIgnoreCase("Echest") && block instanceof BlockEnderChest) {
            return true;
        }
        if (this.mode.getValue().equalsIgnoreCase("Both") && (block instanceof BlockObsidian || block instanceof BlockEnderChest)) {
            return true;
        }
        return this.mode.getValue().equalsIgnoreCase("Web") && block instanceof BlockWeb;
    }
}
