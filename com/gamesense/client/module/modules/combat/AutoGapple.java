



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.client.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class AutoGapple extends Module
{
    public static Setting.Double health;
    public static Setting.Integer forcedSlot;
    private int previousHeldItem;
    private int notchAppleSlot;
    
    public AutoGapple() {
        super("AutoGapple", Module.Category.Combat);
        this.previousHeldItem = -1;
        this.notchAppleSlot = -1;
    }
    
    public String getMetaData() {
        return "" + this.getNotchAppleCount();
    }
    
    public void onUpdate() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) {
            return;
        }
        if (mc.player.getHealth() < AutoGapple.health.getValue() && mc.player.getAbsorptionAmount() == 0.0f) {
            this.notchAppleSlot = this.findNotchApple();
        }
        if (this.notchAppleSlot != -1) {
            if (this.previousHeldItem == -1) {
                this.previousHeldItem = mc.player.inventory.currentItem;
            }
            if (this.notchAppleSlot < 36) {
                mc.playerController.windowClick(0, AutoGapple.forcedSlot.getValue(), 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
                mc.playerController.windowClick(0, this.notchAppleSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
                mc.playerController.windowClick(0, AutoGapple.forcedSlot.getValue(), 0, ClickType.PICKUP, (EntityPlayer)mc.player);
                mc.player.inventory.currentItem = AutoGapple.forcedSlot.getValue() - 36;
            }
            else {
                mc.player.inventory.currentItem = this.notchAppleSlot - 36;
            }
        }
        else if (mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE) {
            mc.playerController.windowClick(0, 45, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
            mc.playerController.windowClick(0, this.notchAppleSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        }
        if (mc.player.getHealth() >= AutoGapple.health.getValue() && mc.player.getAbsorptionAmount() > 0.0f) {
            if (this.previousHeldItem != -1) {
                mc.player.inventory.currentItem = this.previousHeldItem;
            }
            this.notchAppleSlot = -1;
            this.previousHeldItem = -1;
        }
    }
    
    public void setup() {
        AutoGapple.health = this.registerDouble("Health", 15.0, 1.0, 20.0);
        AutoGapple.forcedSlot = this.registerInteger("Slot", 3, 0, 0);
    }
    
    private int findNotchApple() {
        for (int slot = 44; slot > 8; --slot) {
            final ItemStack itemStack = Minecraft.getMinecraft().player.inventoryContainer.getSlot(slot).getStack();
            if (!itemStack.func_190926_b()) {
                if (itemStack.getItemDamage() != 0) {
                    if (itemStack.getItem() == Items.GOLDEN_APPLE) {
                        return slot;
                    }
                }
            }
        }
        return -1;
    }
    
    private int getNotchAppleCount() {
        int gapples = 0;
        if (Minecraft.getMinecraft().player == null) {
            return gapples;
        }
        for (int i = 0; i < 45; ++i) {
            final ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.GOLDEN_APPLE && stack.getItemDamage() != 0) {
                gapples += stack.func_190916_E();
            }
        }
        return gapples;
    }
}
