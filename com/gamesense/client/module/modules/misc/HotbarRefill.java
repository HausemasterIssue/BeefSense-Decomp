



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.misc.*;
import net.minecraft.init.*;
import com.gamesense.api.util.player.*;
import java.util.stream.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.block.*;

public class HotbarRefill extends Module
{
    Setting.Integer threshold;
    Setting.Integer tickDelay;
    private int delayStep;
    
    public HotbarRefill() {
        super("HotbarRefill", Module.Category.Misc);
        this.delayStep = 0;
    }
    
    public void setup() {
        this.threshold = this.registerInteger("Threshold", 32, 1, 63);
        this.tickDelay = this.registerInteger("Tick Delay", 2, 1, 10);
    }
    
    public void onUpdate() {
        if (HotbarRefill.mc.player == null) {
            return;
        }
        if (HotbarRefill.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.delayStep < this.tickDelay.getValue()) {
            ++this.delayStep;
            return;
        }
        this.delayStep = 0;
        final Pair<Integer, Integer> slots = this.findReplenishableHotbarSlot();
        if (slots == null) {
            return;
        }
        final int inventorySlot = (int)slots.getKey();
        final int hotbarSlot = (int)slots.getValue();
        HotbarRefill.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)HotbarRefill.mc.player);
        HotbarRefill.mc.playerController.windowClick(0, hotbarSlot + 36, 0, ClickType.PICKUP, (EntityPlayer)HotbarRefill.mc.player);
        HotbarRefill.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)HotbarRefill.mc.player);
    }
    
    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        final List<ItemStack> inventory = (List<ItemStack>)HotbarRefill.mc.player.inventory.mainInventory;
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            final ItemStack stack = inventory.get(hotbarSlot);
            if (stack.isStackable()) {
                if (!stack.field_190928_g) {
                    if (stack.getItem() != Items.field_190931_a) {
                        if (stack.stackSize < stack.getMaxStackSize()) {
                            if (stack.stackSize <= this.threshold.getValue()) {
                                final int inventorySlot = this.findCompatibleInventorySlot(stack);
                                if (inventorySlot != -1) {
                                    return (Pair<Integer, Integer>)new Pair((Object)inventorySlot, (Object)hotbarSlot);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private int findCompatibleInventorySlot(final ItemStack hotbarStack) {
        final Item item = hotbarStack.getItem();
        List<Integer> potentialSlots;
        if (item instanceof ItemBlock) {
            potentialSlots = (List<Integer>)InventoryUtil.findAllBlockSlots((Class)((ItemBlock)item).getBlock().getClass());
        }
        else {
            potentialSlots = (List<Integer>)InventoryUtil.findAllItemSlots((Class)item.getClass());
        }
        potentialSlots = potentialSlots.stream().filter(integer -> integer > 8 && integer < 36).sorted(Comparator.comparingInt(interger -> -interger)).collect((Collector<? super Object, ?, List<Integer>>)Collectors.toList());
        for (final int slot : potentialSlots) {
            if (this.isCompatibleStacks(hotbarStack, HotbarRefill.mc.player.inventory.getStackInSlot(slot))) {
                return slot;
            }
        }
        return -1;
    }
    
    private boolean isCompatibleStacks(final ItemStack stack1, final ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }
        if (stack1.getItem() instanceof ItemBlock && stack2.getItem() instanceof ItemBlock) {
            final Block block1 = ((ItemBlock)stack1.getItem()).getBlock();
            final Block block2 = ((ItemBlock)stack2.getItem()).getBlock();
            if (!block1.blockMaterial.equals(block2.blockMaterial)) {
                return false;
            }
        }
        return stack1.getDisplayName().equals(stack2.getDisplayName()) && stack1.getItemDamage() == stack2.getItemDamage();
    }
}
