



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import org.lwjgl.input.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import com.gamesense.client.*;

public class AutoTool extends Module
{
    Setting.Boolean switchBack;
    boolean shouldMoveBack;
    int lastSlot;
    long lastChange;
    @EventHandler
    private final Listener<DamageBlockEvent> leftClickListener;
    
    public AutoTool() {
        super("AutoTool", Module.Category.Misc);
        this.shouldMoveBack = false;
        this.lastSlot = 0;
        this.lastChange = 0L;
        this.leftClickListener = (Listener<DamageBlockEvent>)new Listener(event -> this.equipBestTool(AutoTool.mc.world.getBlockState(event.getBlockPos())), new Predicate[0]);
    }
    
    public void setup() {
        this.switchBack = this.registerBoolean("Switch Back", false);
    }
    
    public void onUpdate() {
        if (!this.switchBack.getValue()) {
            this.shouldMoveBack = false;
        }
        if (AutoTool.mc.currentScreen != null || !this.switchBack.getValue()) {
            return;
        }
        final boolean mouse = Mouse.isButtonDown(0);
        if (mouse && !this.shouldMoveBack) {
            this.lastChange = System.currentTimeMillis();
            this.shouldMoveBack = true;
            this.lastSlot = AutoTool.mc.player.inventory.currentItem;
            AutoTool.mc.playerController.syncCurrentPlayItem();
        }
        else if (!mouse && this.shouldMoveBack) {
            this.shouldMoveBack = false;
            AutoTool.mc.player.inventory.currentItem = this.lastSlot;
            AutoTool.mc.playerController.syncCurrentPlayItem();
        }
    }
    
    private void equipBestTool(final IBlockState blockState) {
        int bestSlot = -1;
        double max = 0.0;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoTool.mc.player.inventory.getStackInSlot(i);
            if (!stack.func_190926_b()) {
                float speed = stack.getStrVsBlock(blockState);
                if (speed > 1.0f) {
                    final int eff;
                    speed += (float)(((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0) ? (Math.pow(eff, 2.0) + 1.0) : 0.0);
                    if (speed > max) {
                        max = speed;
                        bestSlot = i;
                    }
                }
            }
        }
        if (bestSlot != -1) {
            equip(bestSlot);
        }
    }
    
    private static void equip(final int slot) {
        AutoTool.mc.player.inventory.currentItem = slot;
        AutoTool.mc.playerController.syncCurrentPlayItem();
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
}
