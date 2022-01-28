



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.concurrent.*;
import com.google.common.collect.*;
import com.gamesense.api.util.player.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import com.gamesense.api.util.world.*;
import java.util.*;
import com.gamesense.api.event.events.*;
import java.util.function.*;
import com.gamesense.api.util.render.*;

public class HoleESP extends Module
{
    public static Setting.Integer rangeS;
    Setting.Boolean hideOwn;
    Setting.Boolean flatOwn;
    Setting.Mode customHoles;
    Setting.Mode mode;
    Setting.Mode type;
    Setting.Double slabHeight;
    Setting.Integer width;
    Setting.ColorSetting bedrockColor;
    Setting.ColorSetting obsidianColor;
    Setting.ColorSetting customColor;
    Setting.Integer ufoAlpha;
    private ConcurrentHashMap<AxisAlignedBB, GSColor> holes;
    
    public HoleESP() {
        super("HoleESP", Module.Category.Render);
    }
    
    public void setup() {
        final ArrayList<String> holes = new ArrayList<String>();
        holes.add("Single");
        holes.add("Double");
        holes.add("Custom");
        final ArrayList<String> render = new ArrayList<String>();
        render.add("Outline");
        render.add("Fill");
        render.add("Both");
        final ArrayList<String> modes = new ArrayList<String>();
        modes.add("Air");
        modes.add("Ground");
        modes.add("Flat");
        modes.add("Slab");
        modes.add("Double");
        HoleESP.rangeS = this.registerInteger("Range", 5, 1, 20);
        this.customHoles = this.registerMode("Show", (List)holes, "Single");
        this.type = this.registerMode("Render", (List)render, "Both");
        this.mode = this.registerMode("Mode", (List)modes, "Air");
        this.hideOwn = this.registerBoolean("Hide Own", false);
        this.flatOwn = this.registerBoolean("Flat Own", false);
        this.slabHeight = this.registerDouble("Slab Height", 0.5, 0.1, 1.5);
        this.width = this.registerInteger("Width", 1, 1, 10);
        this.bedrockColor = this.registerColor("Bedrock Color", new GSColor(0, 255, 0));
        this.obsidianColor = this.registerColor("Obsidian Color", new GSColor(255, 0, 0));
        this.customColor = this.registerColor("Custom Color", new GSColor(0, 0, 255));
        this.ufoAlpha = this.registerInteger("UFOAlpha", 255, 0, 255);
    }
    
    public void onUpdate() {
        if (HoleESP.mc.player == null || HoleESP.mc.world == null) {
            return;
        }
        if (this.holes == null) {
            this.holes = new ConcurrentHashMap<AxisAlignedBB, GSColor>();
        }
        else {
            this.holes.clear();
        }
        final int range = (int)Math.ceil(HoleESP.rangeS.getValue());
        final HashSet<BlockPos> possibleHoles = (HashSet<BlockPos>)Sets.newHashSet();
        final List<BlockPos> blockPosList = (List<BlockPos>)EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (float)range, range, false, true, 0);
        for (final BlockPos pos2 : blockPosList) {
            if (!HoleESP.mc.world.getBlockState(pos2).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (HoleESP.mc.world.getBlockState(pos2.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!HoleESP.mc.world.getBlockState(pos2.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!HoleESP.mc.world.getBlockState(pos2.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            possibleHoles.add(pos2);
        }
        final HoleUtil.HoleInfo holeInfo;
        final HoleUtil.HoleType holeType;
        HoleUtil.BlockSafety holeSafety;
        AxisAlignedBB centreBlocks;
        GSColor colour;
        String mode;
        possibleHoles.forEach(pos -> {
            holeInfo = HoleUtil.isHole(pos, false, false);
            holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                holeSafety = holeInfo.getSafety();
                centreBlocks = holeInfo.getCentre();
                if (centreBlocks != null) {
                    if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                        colour = new GSColor(this.bedrockColor.getValue(), 255);
                    }
                    else {
                        colour = new GSColor(this.obsidianColor.getValue(), 255);
                    }
                    if (holeType == HoleUtil.HoleType.CUSTOM) {
                        colour = new GSColor(this.customColor.getValue(), 255);
                    }
                    mode = this.customHoles.getValue();
                    if (mode.equalsIgnoreCase("Custom") && (holeType == HoleUtil.HoleType.CUSTOM || holeType == HoleUtil.HoleType.DOUBLE)) {
                        this.holes.put(centreBlocks, colour);
                    }
                    else if (mode.equalsIgnoreCase("Double") && holeType == HoleUtil.HoleType.DOUBLE) {
                        this.holes.put(centreBlocks, colour);
                    }
                    else if (holeType == HoleUtil.HoleType.SINGLE) {
                        this.holes.put(centreBlocks, colour);
                    }
                }
            }
        });
    }
    
    public void onWorldRender(final RenderEvent event) {
        if (HoleESP.mc.player == null || HoleESP.mc.world == null || this.holes == null || this.holes.isEmpty()) {
            return;
        }
        this.holes.forEach(this::renderHoles);
    }
    
    private void renderHoles(final AxisAlignedBB hole, final GSColor color) {
        final String value = this.type.getValue();
        switch (value) {
            case "Outline": {
                this.renderOutline(hole, color);
                break;
            }
            case "Fill": {
                this.renderFill(hole, color);
                break;
            }
            case "Both": {
                this.renderOutline(hole, color);
                this.renderFill(hole, color);
                break;
            }
        }
    }
    
    private void renderFill(final AxisAlignedBB hole, final GSColor color) {
        final GSColor fillColor = new GSColor(color, 50);
        final int ufoAlpha = this.ufoAlpha.getValue() * 50 / 255;
        if (this.hideOwn.getValue() && hole.intersectsWith(HoleESP.mc.player.getEntityBoundingBox())) {
            return;
        }
        final String value = this.mode.getValue();
        switch (value) {
            case "Air": {
                if (this.flatOwn.getValue() && hole.intersectsWith(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 63);
                break;
            }
            case "Ground": {
                RenderUtil.drawBox(hole.offset(0.0, -1.0, 0.0), true, 1.0, new GSColor(fillColor, ufoAlpha), fillColor.getAlpha(), 63);
                break;
            }
            case "Flat": {
                RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                break;
            }
            case "Slab": {
                if (this.flatOwn.getValue() && hole.intersectsWith(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole, false, this.slabHeight.getValue(), fillColor, ufoAlpha, 63);
                break;
            }
            case "Double": {
                if (this.flatOwn.getValue() && hole.intersectsWith(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole.setMaxY(hole.maxY + 1.0), true, 2.0, fillColor, ufoAlpha, 63);
                break;
            }
        }
    }
    
    private void renderOutline(final AxisAlignedBB hole, final GSColor color) {
        final GSColor outlineColor = new GSColor(color, 255);
        if (this.hideOwn.getValue() && hole.intersectsWith(HoleESP.mc.player.getEntityBoundingBox())) {
            return;
        }
        final String value = this.mode.getValue();
        switch (value) {
            case "Air": {
                if (this.flatOwn.getValue() && hole.intersectsWith(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole, (double)this.width.getValue(), outlineColor, this.ufoAlpha.getValue());
                break;
            }
            case "Ground": {
                RenderUtil.drawBoundingBox(hole.offset(0.0, -1.0, 0.0), (double)this.width.getValue(), new GSColor(outlineColor, this.ufoAlpha.getValue()), outlineColor.getAlpha());
                break;
            }
            case "Flat": {
                RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                break;
            }
            case "Slab": {
                if (this.flatOwn.getValue() && hole.intersectsWith(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole.setMaxY(hole.minY + this.slabHeight.getValue()), (double)this.width.getValue(), outlineColor, this.ufoAlpha.getValue());
                break;
            }
            case "Double": {
                if (this.flatOwn.getValue() && hole.intersectsWith(HoleESP.mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole.setMaxY(hole.maxY + 1.0), (double)this.width.getValue(), outlineColor, this.ufoAlpha.getValue());
                break;
            }
        }
    }
}
