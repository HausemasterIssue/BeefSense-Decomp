



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;
import com.gamesense.api.event.events.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import net.minecraft.util.math.*;
import com.gamesense.api.util.render.*;
import net.minecraft.client.renderer.*;

public class BreakESP extends Module
{
    Setting.Mode renderType;
    Setting.ColorSetting color;
    Setting.Integer range;
    Setting.Integer lineWidth;
    public static Setting.Boolean cancelAnimation;
    
    public BreakESP() {
        super("BreakESP", Module.Category.Render);
    }
    
    public void setup() {
        final ArrayList<String> renderTypes = new ArrayList<String>();
        renderTypes.add("Outline");
        renderTypes.add("Fill");
        renderTypes.add("Both");
        this.renderType = this.registerMode("Render", (List)renderTypes, "Both");
        this.lineWidth = this.registerInteger("Width", 1, 0, 5);
        this.range = this.registerInteger("Range", 100, 1, 200);
        BreakESP.cancelAnimation = this.registerBoolean("No Animation", true);
        this.color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
    }
    
    public void onWorldRender(final RenderEvent event) {
        if (BreakESP.mc.player == null || BreakESP.mc.world == null) {
            return;
        }
        BlockPos blockPos;
        int progress;
        AxisAlignedBB axisAlignedBB;
        BreakESP.mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress != null) {
                blockPos = destroyBlockProgress.getPosition();
                if (BreakESP.mc.world.getBlockState(blockPos).getBlock() != Blocks.AIR) {
                    if (blockPos.getDistance((int)BreakESP.mc.player.posX, (int)BreakESP.mc.player.posY, (int)BreakESP.mc.player.posZ) <= this.range.getValue()) {
                        progress = destroyBlockProgress.getPartialBlockDamage();
                        axisAlignedBB = BreakESP.mc.world.getBlockState(blockPos).getSelectedBoundingBox((World)BreakESP.mc.world, blockPos);
                        this.renderESP(axisAlignedBB, progress, this.color.getValue());
                    }
                }
            }
        });
    }
    
    private void renderESP(final AxisAlignedBB axisAlignedBB, final int progress, final GSColor color) {
        final GSColor fillColor = new GSColor(color, 50);
        final GSColor outlineColor = new GSColor(color, 255);
        final double centerX = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) / 2.0;
        final double centerY = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) / 2.0;
        final double centerZ = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2.0;
        final double progressValX = progress * ((axisAlignedBB.maxX - centerX) / 10.0);
        final double progressValY = progress * ((axisAlignedBB.maxY - centerY) / 10.0);
        final double progressValZ = progress * ((axisAlignedBB.maxZ - centerZ) / 10.0);
        final AxisAlignedBB axisAlignedBB2 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);
        final String value = this.renderType.getValue();
        switch (value) {
            case "Fill": {
                RenderUtil.drawBox(axisAlignedBB2, true, 0.0, fillColor, 63);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(axisAlignedBB2, (double)this.lineWidth.getValue(), outlineColor);
                break;
            }
            case "Both": {
                RenderUtil.drawBox(axisAlignedBB2, true, 0.0, fillColor, 63);
                RenderUtil.drawBoundingBox(axisAlignedBB2, (double)this.lineWidth.getValue(), outlineColor);
                break;
            }
        }
    }
}
