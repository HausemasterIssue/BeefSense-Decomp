



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.event.events.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.util.math.*;
import net.minecraft.item.*;
import net.minecraft.enchantment.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.font.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.*;
import com.gamesense.api.util.render.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.api.util.player.enemy.*;
import net.minecraft.util.text.*;

public class Nametags extends Module
{
    Setting.Integer range;
    Setting.Boolean durability;
    Setting.Boolean armor;
    Setting.Boolean enchantnames;
    Setting.Boolean itemName;
    Setting.Boolean gamemode;
    Setting.Boolean health;
    Setting.Boolean ping;
    Setting.Boolean entityId;
    public static Setting.Boolean customColor;
    public static Setting.ColorSetting borderColor;
    
    public Nametags() {
        super("Nametags", Module.Category.Render);
    }
    
    public void setup() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.durability = this.registerBoolean("Durability", true);
        this.armor = this.registerBoolean("Armor", true);
        this.enchantnames = this.registerBoolean("Enchants", true);
        this.itemName = this.registerBoolean("Item Name", false);
        this.gamemode = this.registerBoolean("Gamemode", false);
        this.health = this.registerBoolean("Health", true);
        this.ping = this.registerBoolean("Ping", false);
        this.entityId = this.registerBoolean("Entity Id", false);
        Nametags.customColor = this.registerBoolean("Custom Color", true);
        Nametags.borderColor = this.registerColor("Border Color");
    }
    
    public void onWorldRender(final RenderEvent event) {
        for (final Object o : Nametags.mc.world.playerEntities) {
            final Entity entity = (Entity)o;
            if (entity instanceof EntityPlayer && entity != Nametags.mc.player && entity.isEntityAlive() && entity.getDistanceToEntity((Entity)Nametags.mc.player) <= this.range.getValue()) {
                final Vec3d m = renderPosEntity(entity);
                this.renderNameTagsFor((EntityPlayer)entity, m.xCoord, m.yCoord, m.zCoord);
            }
        }
    }
    
    public void renderNameTagsFor(final EntityPlayer entityPlayer, final double n, final double n2, final double n3) {
        this.renderNametags(entityPlayer, n, n2, n3);
    }
    
    public static double timerPos(final double n, final double n2) {
        return n2 + (n - n2) * Nametags.mc.timer.field_194147_b;
    }
    
    public static Vec3d renderPosEntity(final Entity entity) {
        return new Vec3d(timerPos(entity.posX, entity.lastTickPosX), timerPos(entity.posY, entity.lastTickPosY), timerPos(entity.posZ, entity.lastTickPosZ));
    }
    
    private void renderEnchants(final ItemStack itemStack, final int x, int y) {
        GlStateManager.enableTexture2D();
        Iterator<Enchantment> iterator3;
        for (Iterator<Enchantment> iterator2 = iterator3 = EnchantmentHelper.getEnchantments(itemStack).keySet().iterator(); iterator3.hasNext(); iterator3 = iterator2) {
            final Enchantment enchantment;
            if ((enchantment = iterator2.next()) != null) {
                final Enchantment enchantment2 = enchantment;
                if (!this.enchantnames.getValue()) {
                    return;
                }
                FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), this.stringForEnchants(enchantment2, EnchantmentHelper.getEnchantmentLevel(enchantment2, itemStack)), x * 2, y, new GSColor(255, 255, 255));
                y += 8;
            }
        }
        if (itemStack.getItem().equals(Items.GOLDEN_APPLE) && itemStack.hasEffect()) {
            FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), "God", x * 2, y, new GSColor(195, 77, 65));
        }
        GlStateManager.disableTexture2D();
    }
    
    private String stringForEnchants(final Enchantment enchantment, final int n) {
        final ResourceLocation resourceLocation;
        String substring = ((resourceLocation = (ResourceLocation)Enchantment.REGISTRY.getNameForObject((Object)enchantment)) == null) ? enchantment.getName() : resourceLocation.toString();
        final int n2 = (n > 1) ? 12 : 13;
        if (substring.length() > n2) {
            substring = substring.substring(10, n2);
        }
        final StringBuilder sb = new StringBuilder();
        final String s = substring;
        final int n3 = 0;
        String s2 = sb.insert(0, s.substring(0, 1).toUpperCase()).append(substring.substring(1)).toString();
        if (n > 1) {
            s2 = new StringBuilder().insert(0, s2).append(n).toString();
        }
        return s2;
    }
    
    private void renderItemName(final ItemStack itemStack, final int x, final int y) {
        GlStateManager.enableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), itemStack.getDisplayName(), -FontUtil.getStringWidth(ColorMain.customFont.getValue(), itemStack.getDisplayName()) / 2, y, new GSColor(255, 255, 255));
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }
    
    private void renderItemDurability(final ItemStack itemStack, final int x, final int y) {
        final int maxDamage = itemStack.getMaxDamage();
        final float n3 = (maxDamage - itemStack.getItemDamage()) / (float)maxDamage;
        float green = (itemStack.getMaxDamage() - (float)itemStack.getItemDamage()) / itemStack.getMaxDamage();
        if (green > 1.0f) {
            green = 1.0f;
        }
        else if (green < 0.0f) {
            green = 0.0f;
        }
        final float red = 1.0f - green;
        GlStateManager.enableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), new StringBuilder().insert(0, (int)(n3 * 100.0f)).append('%').toString(), x * 2, y, new GSColor((int)(red * 255.0f), (int)(green * 255.0f), 0));
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }
    
    private void renderItems(final ItemStack itemStack, final int n, final int n2, final int n3) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        final int n4 = (n3 > 4) ? ((n3 - 4) * 8 / 2) : 0;
        Nametags.mc.getRenderItem().zLevel = -150.0f;
        RenderHelper.enableStandardItemLighting();
        Nametags.mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, n, n2 + n4);
        Nametags.mc.getRenderItem().renderItemOverlays(Nametags.mc.fontRendererObj, itemStack, n, n2 + n4);
        RenderHelper.disableStandardItemLighting();
        Nametags.mc.getRenderItem().zLevel = 0.0f;
        RenderUtil.prepare();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        this.renderEnchants(itemStack, n, n2 - 24);
        GlStateManager.popMatrix();
    }
    
    private void renderNametags(final EntityPlayer entityPlayer, final double n, double distance, final double n2) {
        double tempY = distance;
        tempY += (entityPlayer.isSneaking() ? 0.5 : 0.7);
        final Entity entity3;
        final Entity entity2 = entity3 = (Entity)((Nametags.mc.getRenderViewEntity() == null) ? Nametags.mc.player : Nametags.mc.getRenderViewEntity());
        final double posX = entity2.posX;
        final double posY = entity2.posY;
        final double posZ = entity2.posZ;
        final Vec3d m;
        entity2.posX = (m = renderPosEntity(entity2)).xCoord;
        entity2.posY = m.yCoord;
        entity2.posZ = m.zCoord;
        distance = entity3.getDistance(n, distance, n2);
        final String[] text = { this.renderEntityName(entityPlayer) };
        RenderUtil.drawNametag(n, tempY + 1.4, n2, text, this.renderPing(entityPlayer), 2);
        final ItemStack heldItemMainhand = entityPlayer.getHeldItemMainhand();
        final ItemStack heldItemOffhand = entityPlayer.getHeldItemOffhand();
        int n3 = 0;
        int n4 = 0;
        boolean b = false;
        for (int i = 3, n5 = 3; i >= 0; i = --n5) {
            final ItemStack itemStack;
            if (!(itemStack = (ItemStack)entityPlayer.inventory.armorInventory.get(n5)).func_190926_b()) {
                final boolean j = this.durability.getValue();
                n3 -= 8;
                if (j) {
                    b = true;
                }
                final int size;
                if (this.armor.getValue() && (size = EnchantmentHelper.getEnchantments(itemStack).size()) > n4) {
                    n4 = size;
                }
            }
        }
        if (!heldItemOffhand.func_190926_b() && (this.armor.getValue() || (this.durability.getValue() && heldItemOffhand.isItemStackDamageable()))) {
            n3 -= 8;
            if (this.durability.getValue() && heldItemOffhand.isItemStackDamageable()) {
                b = true;
            }
            final int size2;
            if (this.armor.getValue() && (size2 = EnchantmentHelper.getEnchantments(heldItemOffhand).size()) > n4) {
                n4 = size2;
            }
        }
        if (!heldItemMainhand.func_190926_b()) {
            final int size3;
            if (this.armor.getValue() && (size3 = EnchantmentHelper.getEnchantments(heldItemMainhand).size()) > n4) {
                n4 = size3;
            }
            int k = this.armorValue(n4);
            if (this.armor.getValue() || (this.durability.getValue() && heldItemMainhand.isItemStackDamageable())) {
                n3 -= 8;
            }
            if (this.armor.getValue()) {
                final ItemStack itemStack2 = heldItemMainhand;
                final int n6 = n3;
                final int n7 = k;
                k -= 32;
                this.renderItems(itemStack2, n6, n7, n4);
            }
            Nametags nametags;
            if (this.durability.getValue() && heldItemMainhand.isItemStackDamageable()) {
                final int n8 = k;
                this.renderItemDurability(heldItemMainhand, n3, k);
                k = n8 - (ColorMain.customFont.getValue() ? FontUtil.getFontHeight(ColorMain.customFont.getValue()) : Nametags.mc.fontRendererObj.FONT_HEIGHT);
                nametags = this;
            }
            else {
                if (b) {
                    k -= (ColorMain.customFont.getValue() ? FontUtil.getFontHeight(ColorMain.customFont.getValue()) : Nametags.mc.fontRendererObj.FONT_HEIGHT);
                }
                nametags = this;
            }
            if (nametags.itemName.getValue()) {
                this.renderItemName(heldItemMainhand, n3, k);
            }
            if (this.armor.getValue() || (this.durability.getValue() && heldItemMainhand.isItemStackDamageable())) {
                n3 += 16;
            }
        }
        for (int l = 3, n9 = 3; l >= 0; l = --n9) {
            final ItemStack itemStack3;
            if (!(itemStack3 = (ItemStack)entityPlayer.inventory.armorInventory.get(n9)).func_190926_b()) {
                int m2 = this.armorValue(n4);
                if (this.armor.getValue()) {
                    final ItemStack itemStack4 = itemStack3;
                    final int n10 = n3;
                    final int n11 = m2;
                    m2 -= 32;
                    this.renderItems(itemStack4, n10, n11, n4);
                }
                if (this.durability.getValue() && itemStack3.isItemStackDamageable()) {
                    this.renderItemDurability(itemStack3, n3, m2);
                }
                n3 += 16;
            }
        }
        if (!heldItemOffhand.func_190926_b()) {
            int m3 = this.armorValue(n4);
            if (this.armor.getValue()) {
                final ItemStack itemStack5 = heldItemOffhand;
                final int n12 = n3;
                final int n13 = m3;
                m3 -= 32;
                this.renderItems(itemStack5, n12, n13, n4);
            }
            if (this.durability.getValue() && heldItemOffhand.isItemStackDamageable()) {
                this.renderItemDurability(heldItemOffhand, n3, m3);
            }
            n3 += 16;
        }
        GlStateManager.popMatrix();
        final double posZ2 = posZ;
        final Entity entity4 = entity3;
        final double posY2 = posY;
        entity3.posX = posX;
        entity4.posY = posY2;
        entity4.posZ = posZ2;
    }
    
    private GSColor renderPing(final EntityPlayer entityPlayer) {
        if (Friends.isFriend(entityPlayer.getName())) {
            return ColorMain.getFriendGSColor();
        }
        if (Enemies.isEnemy(entityPlayer.getName())) {
            return ColorMain.getEnemyGSColor();
        }
        if (entityPlayer.isInvisible()) {
            return new GSColor(128, 128, 128);
        }
        if (Nametags.mc.getConnection() != null && Nametags.mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) == null) {
            return new GSColor(239, 1, 71);
        }
        if (entityPlayer.isSneaking()) {
            return new GSColor(255, 153, 0);
        }
        return new GSColor(255, 255, 255);
    }
    
    private String renderEntityName(final EntityPlayer entityPlayer) {
        String s = entityPlayer.getDisplayName().getFormattedText();
        if (this.entityId.getValue()) {
            s = new StringBuilder().insert(0, s).append(" ID: ").append(entityPlayer.getEntityId()).toString();
        }
        if (this.gamemode.getValue()) {
            if (entityPlayer.isCreative()) {
                s = new StringBuilder().insert(0, s).append(" [C]").toString();
            }
            else if (entityPlayer.isSpectator()) {
                s = new StringBuilder().insert(0, s).append(" [I]").toString();
            }
            else {
                s = new StringBuilder().insert(0, s).append(" [S]").toString();
            }
        }
        if (this.ping.getValue() && Nametags.mc.getConnection() != null && Nametags.mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) != null) {
            s = new StringBuilder().insert(0, s).append(" ").append(Nametags.mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()).getResponseTime()).append("ms").toString();
        }
        if (!this.health.getValue()) {
            return s;
        }
        String s2 = TextFormatting.GREEN.toString();
        final double ceil;
        if ((ceil = Math.ceil(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount())) > 0.0) {
            if (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() <= 5.0f) {
                s2 = TextFormatting.RED.toString();
            }
            else if (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() > 5.0f && entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() <= 10.0f) {
                s2 = TextFormatting.GOLD.toString();
            }
            else if (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() > 10.0f && entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() <= 15.0f) {
                s2 = TextFormatting.YELLOW.toString();
            }
            else if (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() > 15.0f && entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() <= 20.0f) {
                s2 = TextFormatting.DARK_GREEN.toString();
            }
            else if (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() > 20.0f) {
                s2 = TextFormatting.GREEN.toString();
            }
        }
        else {
            s2 = TextFormatting.DARK_RED.toString();
        }
        return new StringBuilder().insert(0, s).append(s2).append(" ").append((ceil > 0.0) ? Integer.valueOf((int)ceil) : "0").toString();
    }
    
    private int armorValue(final int n) {
        int n2 = this.armor.getValue() ? -26 : -27;
        if (n > 4) {
            n2 -= (n - 4) * 8;
        }
        return n2;
    }
}
