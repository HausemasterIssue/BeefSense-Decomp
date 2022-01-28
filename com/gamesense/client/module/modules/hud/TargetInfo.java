



package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.render.*;
import com.lukflug.panelstudio.theme.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.player.enemy.*;
import net.minecraft.client.*;
import com.lukflug.panelstudio.hud.*;
import net.minecraft.entity.*;
import java.util.*;
import java.awt.*;
import com.gamesense.client.clickgui.*;
import com.lukflug.panelstudio.*;

public class TargetInfo extends HUDModule
{
    private Setting.Integer range;
    private Setting.ColorSetting backgroundColor;
    private Setting.ColorSetting outlineColor;
    public static EntityPlayer targetPlayer;
    
    public TargetInfo() {
        super("TargetInfo", new Point(0, 150));
    }
    
    public void setup() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.backgroundColor = this.registerColor("Background", new GSColor(0, 0, 0, 255));
        this.outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
    }
    
    public void populate(final Theme theme) {
        this.component = new TargetInfoComponent(theme);
    }
    
    private Color getNameColor(final EntityPlayer entityPlayer) {
        if (Friends.isFriend(entityPlayer.getName())) {
            return (Color)new GSColor(ColorMain.getFriendGSColor(), 255);
        }
        if (Enemies.isEnemy(entityPlayer.getName())) {
            return (Color)new GSColor(ColorMain.getEnemyGSColor(), 255);
        }
        return (Color)new GSColor(255, 255, 255, 255);
    }
    
    private Color getHealthColor(final EntityPlayer entityPlayer) {
        int health = (int)(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }
        final int red = (int)(255.0 - health * 7.0833);
        final int green = 255 - red;
        return new Color(red, green, 0, 100);
    }
    
    private static Color getDistanceColor(final EntityPlayer entityPlayer) {
        int distance = (int)entityPlayer.getDistanceToEntity((Entity)TargetInfo.mc.player);
        if (distance > 50) {
            distance = 50;
        }
        final int red = (int)(255.0 - distance * 5.1);
        final int green = 255 - red;
        return new Color(red, green, 0, 100);
    }
    
    public static boolean isRenderingEntity(final EntityPlayer entityPlayer) {
        return TargetInfo.targetPlayer == entityPlayer;
    }
    
    private class TargetInfoComponent extends HUDComponent
    {
        public TargetInfoComponent(final Theme theme) {
            super(TargetInfo.this.getName(), theme.getPanelRenderer(), TargetInfo.this.position);
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            if (TargetInfo.mc.player != null && TargetInfo.mc.player.ticksExisted >= 10) {
                final EntityPlayer entityPlayer = (EntityPlayer)TargetInfo.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityPlayer).filter(entity -> entity != TargetInfo.mc.player).map(entity -> entity).min(Comparator.comparing(c -> TargetInfo.mc.player.getDistanceToEntity(c))).orElse(null);
                if (entityPlayer != null && entityPlayer.getDistanceToEntity((Entity)TargetInfo.mc.player) <= TargetInfo.this.range.getValue()) {
                    final Color background = (Color)new GSColor(TargetInfo.this.backgroundColor.getValue(), 100);
                    context.getInterface().fillRect(context.getRect(), background, background, background, background);
                    final Color outline = (Color)new GSColor(TargetInfo.this.outlineColor.getValue(), 255);
                    context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                    context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                    final String name = entityPlayer.getName();
                    final Color nameColor = TargetInfo.this.getNameColor(entityPlayer);
                    context.getInterface().drawString(new Point(context.getPos().x + 2, context.getPos().y + 2), name, nameColor);
                    final int healthVal = (int)(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
                    final Color healthBox = TargetInfo.this.getHealthColor(entityPlayer);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + 32, context.getPos().y + 12, (int)(healthVal * 1.9444), 15), healthBox, healthBox, healthBox, healthBox);
                    final int distanceVal = (int)entityPlayer.getDistanceToEntity((Entity)TargetInfo.mc.player);
                    int width = (int)(distanceVal * 1.38);
                    if (width > 69) {
                        width = 69;
                    }
                    final Color distanceBox = getDistanceColor(entityPlayer);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + 32, context.getPos().y + 27, width, 15), distanceBox, distanceBox, distanceBox, distanceBox);
                    GameSenseGUI.renderEntity((EntityLivingBase)(TargetInfo.targetPlayer = entityPlayer), new Point(context.getPos().x + 17, context.getPos().y + 40), 15);
                    final String health = "Health: " + healthVal;
                    final Color healthColor = new Color(255, 255, 255, 255);
                    context.getInterface().drawString(new Point(context.getPos().x + 33, context.getPos().y + 14), health, healthColor);
                    final String distance = "Distance: " + distanceVal;
                    final Color distanceColor = new Color(255, 255, 255, 255);
                    context.getInterface().drawString(new Point(context.getPos().x + 33, context.getPos().y + 29), distance, distanceColor);
                }
            }
        }
        
        @Override
        public int getWidth(final Interface inter) {
            return 102;
        }
        
        @Override
        public void getHeight(final Context context) {
            context.setHeight(43);
        }
    }
}
