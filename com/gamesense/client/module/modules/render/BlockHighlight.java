



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;
import com.gamesense.api.event.events.*;
import net.minecraft.world.*;
import net.minecraft.block.material.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import com.gamesense.api.util.render.*;

public class BlockHighlight extends Module
{
    Setting.Integer lineWidth;
    Setting.Mode renderType;
    Setting.Mode renderLook;
    Setting.ColorSetting renderColor;
    private int lookInt;
    
    public BlockHighlight() {
        super("BlockHighlight", Module.Category.Render);
    }
    
    public void setup() {
        final ArrayList<String> renderLooks = new ArrayList<String>();
        renderLooks.add("Block");
        renderLooks.add("Side");
        final ArrayList<String> renderTypes = new ArrayList<String>();
        renderTypes.add("Outline");
        renderTypes.add("Fill");
        renderTypes.add("Both");
        this.renderLook = this.registerMode("Render", (List)renderLooks, "Block");
        this.renderType = this.registerMode("Type", (List)renderTypes, "Outline");
        this.lineWidth = this.registerInteger("Width", 1, 1, 5);
        this.renderColor = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    }
    
    public void onWorldRender(final RenderEvent event) {
        final RayTraceResult rayTraceResult = BlockHighlight.mc.objectMouseOver;
        if (rayTraceResult == null) {
            return;
        }
        final EnumFacing enumFacing = BlockHighlight.mc.objectMouseOver.sideHit;
        if (enumFacing == null) {
            return;
        }
        final GSColor colorWithOpacity = new GSColor(this.renderColor.getValue(), 50);
        final String value = this.renderLook.getValue();
        switch (value) {
            case "Block": {
                this.lookInt = 0;
                break;
            }
            case "Side": {
                this.lookInt = 1;
                break;
            }
        }
        if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            final BlockPos blockPos = rayTraceResult.getBlockPos();
            final AxisAlignedBB axisAlignedBB = BlockHighlight.mc.world.getBlockState(blockPos).getSelectedBoundingBox((World)BlockHighlight.mc.world, blockPos);
            if (axisAlignedBB != null && blockPos != null && BlockHighlight.mc.world.getBlockState(blockPos).getMaterial() != Material.AIR) {
                final String value2 = this.renderType.getValue();
                switch (value2) {
                    case "Outline": {
                        this.renderOutline(axisAlignedBB, this.lineWidth.getValue(), this.renderColor.getValue(), enumFacing, this.lookInt);
                        break;
                    }
                    case "Fill": {
                        this.renderFill(axisAlignedBB, colorWithOpacity, enumFacing, this.lookInt);
                        break;
                    }
                    case "Both": {
                        this.renderOutline(axisAlignedBB, this.lineWidth.getValue(), this.renderColor.getValue(), enumFacing, this.lookInt);
                        this.renderFill(axisAlignedBB, colorWithOpacity, enumFacing, this.lookInt);
                        break;
                    }
                }
            }
        }
    }
    
    public void renderOutline(final AxisAlignedBB axisAlignedBB, final int width, final GSColor color, final EnumFacing enumFacing, final int lookInt) {
        if (lookInt == 0) {
            RenderUtil.drawBoundingBox(axisAlignedBB, (double)width, color);
        }
        else if (lookInt == 1) {
            RenderUtil.drawBoundingBoxWithSides(axisAlignedBB, width, color, this.findRenderingSide(enumFacing));
        }
    }
    
    public void renderFill(final AxisAlignedBB axisAlignedBB, final GSColor color, final EnumFacing enumFacing, final int lookInt) {
        int facing = 0;
        if (lookInt == 0) {
            facing = 63;
        }
        else if (lookInt == 1) {
            facing = this.findRenderingSide(enumFacing);
        }
        RenderUtil.drawBox(axisAlignedBB, true, 1.0, color, facing);
    }
    
    private int findRenderingSide(final EnumFacing enumFacing) {
        int facing = 0;
        if (enumFacing == EnumFacing.EAST) {
            facing = 32;
        }
        else if (enumFacing == EnumFacing.WEST) {
            facing = 16;
        }
        else if (enumFacing == EnumFacing.NORTH) {
            facing = 4;
        }
        else if (enumFacing == EnumFacing.SOUTH) {
            facing = 8;
        }
        else if (enumFacing == EnumFacing.UP) {
            facing = 2;
        }
        else if (enumFacing == EnumFacing.DOWN) {
            facing = 1;
        }
        return facing;
    }
}
