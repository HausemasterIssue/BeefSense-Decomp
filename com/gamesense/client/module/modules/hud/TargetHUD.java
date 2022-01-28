



package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.render.*;
import com.lukflug.panelstudio.theme.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.player.enemy.*;
import net.minecraft.client.network.*;
import com.gamesense.api.util.world.*;
import net.minecraft.client.*;
import com.lukflug.panelstudio.hud.*;
import net.minecraft.entity.*;
import java.awt.*;
import com.gamesense.client.clickgui.*;
import net.minecraft.util.text.*;
import net.minecraft.potion.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import java.util.*;
import com.lukflug.panelstudio.*;

public class TargetHUD extends HUDModule
{
    private Setting.ColorSetting outline;
    private Setting.ColorSetting background;
    private Setting.Integer range;
    private static EntityPlayer targetPlayer;
    
    public TargetHUD() {
        super("TargetHUD", new Point(0, 70));
    }
    
    public void setup() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.outline = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
        this.background = this.registerColor("Background", new GSColor(0, 0, 0, 255));
    }
    
    public void populate(final Theme theme) {
        this.component = new TargetHUDComponent(theme);
    }
    
    private static Color getNameColor(final String playerName) {
        if (Friends.isFriend(playerName)) {
            return (Color)new GSColor(ColorMain.getFriendGSColor(), 255);
        }
        if (Enemies.isEnemy(playerName)) {
            return (Color)new GSColor(ColorMain.getEnemyGSColor(), 255);
        }
        return (Color)new GSColor(255, 255, 255, 255);
    }
    
    private static GSColor getHealthColor(int health) {
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }
        final int red = (int)(255.0 - health * 7.0833);
        final int green = 255 - red;
        return new GSColor(red, green, 0, 255);
    }
    
    private static boolean isValidEntity(final Entity e) {
        return e instanceof EntityPlayer && e != TargetHUD.mc.player;
    }
    
    private static float getPing(final EntityPlayer player) {
        float ping = 0.0f;
        try {
            ping = EntityUtil.clamp((float)Objects.requireNonNull(TargetHUD.mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime(), 1.0f, 300.0f);
        }
        catch (NullPointerException ex) {}
        return ping;
    }
    
    public static boolean isRenderingEntity(final EntityPlayer entityPlayer) {
        return TargetHUD.targetPlayer == entityPlayer;
    }
    
    private class TargetHUDComponent extends HUDComponent
    {
        public TargetHUDComponent(final Theme theme) {
            super(TargetHUD.this.getName(), theme.getPanelRenderer(), TargetHUD.this.position);
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            if (TargetHUD.mc.world != null && TargetHUD.mc.player.ticksExisted >= 10) {
                final EntityPlayer entityPlayer = (EntityPlayer)TargetHUD.mc.world.loadedEntityList.stream().filter(entity -> isValidEntity(entity)).map(entity -> entity).min(Comparator.comparing(c -> TargetHUD.mc.player.getDistanceToEntity(c))).orElse(null);
                if (entityPlayer != null && entityPlayer.getDistanceToEntity((Entity)TargetHUD.mc.player) <= TargetHUD.this.range.getValue()) {
                    final Color bgcolor = (Color)new GSColor(TargetHUD.this.background.getValue(), 100);
                    context.getInterface().fillRect(context.getRect(), bgcolor, bgcolor, bgcolor, bgcolor);
                    final Color color = (Color)TargetHUD.this.outline.getValue();
                    context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), color, color, color, color);
                    context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), color, color, color, color);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), color, color, color, color);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), color, color, color, color);
                    TargetHUD.targetPlayer = entityPlayer;
                    GameSenseGUI.renderEntity((EntityLivingBase)entityPlayer, new Point(context.getPos().x + 35, context.getPos().y + 87 - (entityPlayer.isSneaking() ? 10 : 0)), 43);
                    TargetHUD.targetPlayer = null;
                    final String playerName = entityPlayer.getName();
                    final Color nameColor = getNameColor(playerName);
                    context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 11), TextFormatting.BOLD + playerName, nameColor);
                    final int playerHealth = (int)(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
                    final Color healthColor = (Color)getHealthColor(playerHealth);
                    context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 23), TextFormatting.WHITE + "Health: " + TextFormatting.RESET + playerHealth, healthColor);
                    context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 33), "Distance: " + (int)entityPlayer.getDistanceToEntity((Entity)TargetHUD.mc.player), new Color(255, 255, 255));
                    String info;
                    if (entityPlayer.inventory.armorItemInSlot(2).getItem().equals(Items.ELYTRA)) {
                        info = TextFormatting.LIGHT_PURPLE + "Wasp";
                    }
                    else if (entityPlayer.inventory.armorItemInSlot(2).getItem().equals(Items.DIAMOND_CHESTPLATE)) {
                        info = TextFormatting.RED + "Threat";
                    }
                    else if (entityPlayer.inventory.armorItemInSlot(3).getItem().equals(Items.field_190931_a)) {
                        info = TextFormatting.GREEN + "NewFag";
                    }
                    else {
                        info = TextFormatting.WHITE + "None";
                    }
                    context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 43), info + TextFormatting.WHITE + " | " + getPing(entityPlayer) + " ms", new Color(255, 255, 255));
                    String status = null;
                    Color statusColor = null;
                    for (final PotionEffect effect : entityPlayer.getActivePotionEffects()) {
                        if (effect.getPotion() == MobEffects.WEAKNESS) {
                            status = "Weakness!";
                            statusColor = new Color(135, 0, 25);
                        }
                        else if (effect.getPotion() == MobEffects.INVISIBILITY) {
                            status = "Invisible!";
                            statusColor = new Color(90, 90, 90);
                        }
                        else {
                            if (effect.getPotion() != MobEffects.STRENGTH) {
                                continue;
                            }
                            status = "Strength!";
                            statusColor = new Color(185, 65, 185);
                        }
                    }
                    if (status != null) {
                        context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 55), TextFormatting.WHITE + "Status: " + TextFormatting.RESET + status, statusColor);
                    }
                    int xPos = context.getPos().x + 150;
                    for (final ItemStack itemStack : entityPlayer.getArmorInventoryList()) {
                        xPos -= 20;
                        GameSenseGUI.renderItem(itemStack, new Point(xPos, context.getPos().y + 73));
                    }
                }
            }
        }
        
        @Override
        public int getWidth(final Interface inter) {
            return 162;
        }
        
        @Override
        public void getHeight(final Context context) {
            context.setHeight(94);
        }
    }
}
