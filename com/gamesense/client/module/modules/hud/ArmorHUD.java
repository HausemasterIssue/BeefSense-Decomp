



package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import net.minecraft.item.*;
import com.gamesense.api.util.render.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.font.*;
import java.util.*;

public class ArmorHUD extends Module
{
    private static final RenderItem itemRender;
    
    public ArmorHUD() {
        super("ArmorHUD", Module.Category.HUD);
    }
    
    public void onRender() {
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        final ScaledResolution resolution = new ScaledResolution(ArmorHUD.mc);
        final int i = resolution.getScaledWidth() / 2;
        int iteration = 0;
        final int y = resolution.getScaledHeight() - 55 - (ArmorHUD.mc.player.isInWater() ? 10 : 0);
        for (final ItemStack is : ArmorHUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.func_190926_b()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            ArmorHUD.itemRender.zLevel = 200.0f;
            ArmorHUD.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            ArmorHUD.itemRender.renderItemOverlayIntoGUI(ArmorHUD.mc.fontRendererObj, is, x, y, "");
            ArmorHUD.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.func_190916_E() > 1) ? (is.func_190916_E() + "") : "";
            ArmorHUD.mc.fontRendererObj.drawStringWithShadow(s, (float)(x + 19 - 2 - ArmorHUD.mc.fontRendererObj.getStringWidth(s)), (float)(y + 9), new GSColor(255, 255, 255).getRGB());
            float green = (is.getMaxDamage() - (float)is.getItemDamage()) / is.getMaxDamage();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            if (green > 1.0f) {
                green = 1.0f;
            }
            else if (green < 0.0f) {
                green = 0.0f;
            }
            if (red > 1.0f) {
                red = 1.0f;
            }
            if (dmg < 0) {
                dmg = 0;
            }
            FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), dmg + "", x + 8 - ArmorHUD.mc.fontRendererObj.getStringWidth(dmg + "") / 2, y - 11, new GSColor((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
    
    static {
        itemRender = ArmorHUD.mc.getRenderItem();
    }
}
