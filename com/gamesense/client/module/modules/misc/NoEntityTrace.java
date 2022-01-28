



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.block.*;
import net.minecraft.item.*;

public class NoEntityTrace extends Module
{
    Setting.Boolean pickaxe;
    Setting.Boolean obsidian;
    Setting.Boolean eChest;
    Setting.Boolean block;
    Setting.Boolean all;
    boolean isHoldingPickaxe;
    boolean isHoldingObsidian;
    boolean isHoldingEChest;
    boolean isHoldingBlock;
    
    public NoEntityTrace() {
        super("NoEntityTrace", Module.Category.Misc);
        this.isHoldingPickaxe = false;
        this.isHoldingObsidian = false;
        this.isHoldingEChest = false;
        this.isHoldingBlock = false;
    }
    
    public void setup() {
        this.pickaxe = this.registerBoolean("Pickaxe", true);
        this.obsidian = this.registerBoolean("Obsidian", false);
        this.eChest = this.registerBoolean("EnderChest", false);
        this.block = this.registerBoolean("Blocks", false);
        this.all = this.registerBoolean("All", false);
    }
    
    public void onUpdate() {
        final Item item = NoEntityTrace.mc.player.getHeldItemMainhand().getItem();
        this.isHoldingPickaxe = (item instanceof ItemPickaxe);
        this.isHoldingBlock = (item instanceof ItemBlock);
        if (this.isHoldingBlock) {
            this.isHoldingObsidian = (((ItemBlock)item).getBlock() instanceof BlockObsidian);
            this.isHoldingEChest = (((ItemBlock)item).getBlock() instanceof BlockEnderChest);
        }
        else {
            this.isHoldingObsidian = false;
            this.isHoldingEChest = false;
        }
    }
    
    public boolean noTrace() {
        if (this.pickaxe.getValue() && this.isHoldingPickaxe) {
            return this.isEnabled();
        }
        if (this.obsidian.getValue() && this.isHoldingObsidian) {
            return this.isEnabled();
        }
        if (this.eChest.getValue() && this.isHoldingEChest) {
            return this.isEnabled();
        }
        if (this.block.getValue() && this.isHoldingBlock) {
            return this.isEnabled();
        }
        return this.all.getValue() && this.isEnabled();
    }
}
