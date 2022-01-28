



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import io.netty.util.internal.*;
import com.gamesense.api.util.world.*;
import net.minecraft.init.*;
import java.util.*;
import com.gamesense.api.event.events.*;
import net.minecraft.util.math.*;
import com.gamesense.api.util.render.*;

public class VoidESP extends Module
{
    Setting.Integer renderDistance;
    Setting.Integer activeYValue;
    Setting.Mode renderType;
    Setting.Mode renderMode;
    Setting.Integer width;
    Setting.ColorSetting color;
    private ConcurrentSet<BlockPos> voidHoles;
    
    public VoidESP() {
        super("VoidESP", Module.Category.Render);
    }
    
    public void setup() {
        final ArrayList<String> render = new ArrayList<String>();
        render.add("Outline");
        render.add("Fill");
        render.add("Both");
        final ArrayList<String> modes = new ArrayList<String>();
        modes.add("Box");
        modes.add("Flat");
        this.renderDistance = this.registerInteger("Distance", 10, 1, 40);
        this.activeYValue = this.registerInteger("Activate Y", 20, 0, 256);
        this.renderType = this.registerMode("Render", (List)render, "Both");
        this.renderMode = this.registerMode("Mode", (List)modes, "Flat");
        this.width = this.registerInteger("Width", 1, 1, 10);
        this.color = this.registerColor("Color", new GSColor(255, 255, 0));
    }
    
    public void onUpdate() {
        if (VoidESP.mc.player.dimension == 1) {
            return;
        }
        if (VoidESP.mc.player.getPosition().getY() > this.activeYValue.getValue()) {
            return;
        }
        if (this.voidHoles == null) {
            this.voidHoles = (ConcurrentSet<BlockPos>)new ConcurrentSet();
        }
        else {
            this.voidHoles.clear();
        }
        final List<BlockPos> blockPosList = (List<BlockPos>)BlockUtil.getCircle(getPlayerPos(), 0, (float)this.renderDistance.getValue(), false);
        for (final BlockPos blockPos : blockPosList) {
            if (VoidESP.mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK)) {
                continue;
            }
            if (this.isAnyBedrock(blockPos, Offsets.center)) {
                continue;
            }
            this.voidHoles.add((Object)blockPos);
        }
    }
    
    public void onWorldRender(final RenderEvent event) {
        if (VoidESP.mc.player == null || this.voidHoles == null) {
            return;
        }
        if (VoidESP.mc.player.getPosition().getY() > this.activeYValue.getValue()) {
            return;
        }
        if (this.voidHoles.isEmpty()) {
            return;
        }
        this.voidHoles.forEach(blockPos -> {
            if (this.renderMode.getValue().equalsIgnoreCase("Box")) {
                this.drawBox(blockPos);
            }
            else {
                this.drawFlat(blockPos);
            }
            this.drawOutline(blockPos, this.width.getValue());
        });
    }
    
    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(VoidESP.mc.player.posX), Math.floor(VoidESP.mc.player.posY), Math.floor(VoidESP.mc.player.posZ));
    }
    
    private boolean isAnyBedrock(final BlockPos origin, final BlockPos[] offset) {
        for (final BlockPos pos : offset) {
            if (VoidESP.mc.world.getBlockState(origin.add((Vec3i)pos)).getBlock().equals(Blocks.BEDROCK)) {
                return true;
            }
        }
        return false;
    }
    
    private void drawFlat(final BlockPos blockPos) {
        if (this.renderType.getValue().equalsIgnoreCase("Fill") || this.renderType.getValue().equalsIgnoreCase("Both")) {
            final GSColor c = new GSColor(this.color.getValue(), 50);
            if (this.renderMode.getValue().equalsIgnoreCase("Flat")) {
                RenderUtil.drawBox(blockPos, 1.0, c, 1);
            }
        }
    }
    
    private void drawBox(final BlockPos blockPos) {
        if (this.renderType.getValue().equalsIgnoreCase("Fill") || this.renderType.getValue().equalsIgnoreCase("Both")) {
            final GSColor c = new GSColor(this.color.getValue(), 50);
            RenderUtil.drawBox(blockPos, 1.0, c, 63);
        }
    }
    
    private void drawOutline(final BlockPos blockPos, final int width) {
        if (this.renderType.getValue().equalsIgnoreCase("Outline") || this.renderType.getValue().equalsIgnoreCase("Both")) {
            if (this.renderMode.getValue().equalsIgnoreCase("Box")) {
                RenderUtil.drawBoundingBox(blockPos, 1.0, (float)width, this.color.getValue());
            }
            if (this.renderMode.getValue().equalsIgnoreCase("Flat")) {
                RenderUtil.drawBoundingBoxWithSides(blockPos, width, this.color.getValue(), 1);
            }
        }
    }
    
    private static class Offsets
    {
        static final BlockPos[] center;
        
        static {
            center = new BlockPos[] { new BlockPos(0, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, 2, 0) };
        }
    }
}
