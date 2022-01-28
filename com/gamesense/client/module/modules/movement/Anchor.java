



package com.gamesense.client.module.modules.movement;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import com.gamesense.api.util.world.*;
import java.util.*;

public class Anchor extends Module
{
    Setting.Boolean guarantee;
    Setting.Integer activateHeight;
    BlockPos playerPos;
    
    public Anchor() {
        super("Anchor", Module.Category.Movement);
    }
    
    public void setup() {
        this.guarantee = this.registerBoolean("Guarantee Hole", true);
        this.activateHeight = this.registerInteger("Activate Height", 2, 1, 5);
    }
    
    public void onUpdate() {
        if (Anchor.mc.player == null) {
            return;
        }
        if (Anchor.mc.player.posY < 0.0) {
            return;
        }
        final double blockX = Math.floor(Anchor.mc.player.posX);
        final double blockZ = Math.floor(Anchor.mc.player.posZ);
        final double offsetX = Math.abs(Anchor.mc.player.posX - blockX);
        final double offsetZ = Math.abs(Anchor.mc.player.posZ - blockZ);
        if (this.guarantee.getValue() && (offsetX < 0.30000001192092896 || offsetX > 0.699999988079071 || offsetZ < 0.30000001192092896 || offsetZ > 0.699999988079071)) {
            return;
        }
        this.playerPos = new BlockPos(blockX, Anchor.mc.player.posY, blockZ);
        if (Anchor.mc.world.getBlockState(this.playerPos).getBlock() != Blocks.AIR) {
            return;
        }
        BlockPos currentBlock = this.playerPos.down();
        for (int i = 0; i < this.activateHeight.getValue(); ++i) {
            currentBlock = currentBlock.down();
            if (Anchor.mc.world.getBlockState(currentBlock).getBlock() != Blocks.AIR) {
                final HashMap<HoleUtil.BlockOffset, HoleUtil.BlockSafety> sides = (HashMap<HoleUtil.BlockOffset, HoleUtil.BlockSafety>)HoleUtil.getUnsafeSides(currentBlock.up());
                sides.entrySet().removeIf(entry -> entry.getValue() == HoleUtil.BlockSafety.RESISTANT);
                if (sides.size() == 0) {
                    Anchor.mc.player.motionX = 0.0;
                    Anchor.mc.player.motionZ = 0.0;
                }
            }
        }
    }
}
