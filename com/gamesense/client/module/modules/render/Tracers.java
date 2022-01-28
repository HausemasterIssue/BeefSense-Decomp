



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;
import com.gamesense.api.event.events.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.player.enemy.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import com.gamesense.api.util.render.*;
import net.minecraft.util.math.*;

public class Tracers extends Module
{
    Setting.Boolean colorType;
    Setting.Integer renderDistance;
    Setting.Mode pointsTo;
    Setting.ColorSetting nearColor;
    Setting.ColorSetting midColor;
    Setting.ColorSetting farColor;
    GSColor tracerColor;
    
    public Tracers() {
        super("Tracers", Module.Category.Render);
    }
    
    public void setup() {
        this.renderDistance = this.registerInteger("Distance", 100, 10, 260);
        final ArrayList<String> link = new ArrayList<String>();
        link.add("Head");
        link.add("Feet");
        this.pointsTo = this.registerMode("Draw To", (List)link, "Feet");
        this.colorType = this.registerBoolean("Color Sync", true);
        this.nearColor = this.registerColor("Near Color", new GSColor(255, 0, 0, 255));
        this.midColor = this.registerColor("Middle Color", new GSColor(255, 255, 0, 255));
        this.farColor = this.registerColor("Far Color", new GSColor(0, 255, 0, 255));
    }
    
    public void onWorldRender(final RenderEvent event) {
        Tracers.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).filter(e -> e != Tracers.mc.player).forEach(e -> {
            if (Tracers.mc.player.getDistanceToEntity(e) <= this.renderDistance.getValue()) {
                if (Friends.isFriend(e.getName())) {
                    this.tracerColor = ColorMain.getFriendGSColor();
                }
                else if (Enemies.isEnemy(e.getName())) {
                    this.tracerColor = ColorMain.getEnemyGSColor();
                }
                else {
                    if (Tracers.mc.player.getDistanceToEntity(e) < 20.0f) {
                        this.tracerColor = this.nearColor.getValue();
                    }
                    if (Tracers.mc.player.getDistanceToEntity(e) >= 20.0f && Tracers.mc.player.getDistanceToEntity(e) < 50.0f) {
                        this.tracerColor = this.midColor.getValue();
                    }
                    if (Tracers.mc.player.getDistanceToEntity(e) >= 50.0f) {
                        this.tracerColor = this.farColor.getValue();
                    }
                    if (this.colorType.getValue()) {
                        this.tracerColor = this.getDistanceColor((int)Tracers.mc.player.getDistanceToEntity(e));
                    }
                }
                this.drawLineToEntityPlayer(e, this.tracerColor);
            }
        });
    }
    
    public void drawLineToEntityPlayer(final Entity e, final GSColor color) {
        final double[] xyz = interpolate(e);
        this.drawLine1(xyz[0], xyz[1], xyz[2], e.height, color);
    }
    
    public static double[] interpolate(final Entity entity) {
        final double posX = interpolate(entity.posX, entity.lastTickPosX);
        final double posY = interpolate(entity.posY, entity.lastTickPosY);
        final double posZ = interpolate(entity.posZ, entity.lastTickPosZ);
        return new double[] { posX, posY, posZ };
    }
    
    public static double interpolate(final double now, final double then) {
        return then + (now - then) * Tracers.mc.getRenderPartialTicks();
    }
    
    public void drawLine1(final double posx, final double posy, final double posz, final double up, final GSColor color) {
        final Vec3d eyes = ActiveRenderInfo.getCameraPosition().addVector(Tracers.mc.getRenderManager().viewerPosX, Tracers.mc.getRenderManager().viewerPosY, Tracers.mc.getRenderManager().viewerPosZ);
        RenderUtil.prepare();
        if (this.pointsTo.getValue().equalsIgnoreCase("Head")) {
            RenderUtil.drawLine(eyes.xCoord, eyes.yCoord, eyes.zCoord, posx, posy + up, posz, color);
        }
        else {
            RenderUtil.drawLine(eyes.xCoord, eyes.yCoord, eyes.zCoord, posx, posy, posz, color);
        }
        RenderUtil.release();
    }
    
    private GSColor getDistanceColor(int distance) {
        if (distance > 50) {
            distance = 50;
        }
        final int red = (int)(255.0 - distance * 5.1);
        final int green = 255 - red;
        return new GSColor(red, green, 0, 255);
    }
}
