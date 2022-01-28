



package com.gamesense.api.util.player;

import net.minecraft.client.*;

public final class HandSwapContext
{
    private final int oldSlot;
    private final int newSlot;
    
    public HandSwapContext(final int oldSlot, final int newSlot) {
        this.oldSlot = oldSlot;
        this.newSlot = newSlot;
    }
    
    public int getOldSlot() {
        return this.oldSlot;
    }
    
    public int getNewSlot() {
        return this.newSlot;
    }
    
    public void handleHandSwap(final boolean restore, final Minecraft minecraft) {
        minecraft.player.inventory.currentItem = (restore ? this.getOldSlot() : this.getNewSlot());
        minecraft.playerController.updateController();
    }
}
