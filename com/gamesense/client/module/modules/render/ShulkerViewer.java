



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.item.*;
import com.gamesense.api.util.render.*;
import net.minecraft.client.renderer.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.font.*;
import net.minecraft.util.*;
import net.minecraft.inventory.*;
import java.awt.*;
import com.gamesense.client.clickgui.*;

public class ShulkerViewer extends Module
{
    public static Setting.ColorSetting outlineColor;
    public static Setting.ColorSetting fillColor;
    
    public ShulkerViewer() {
        super("ShulkerViewer", Module.Category.Render);
    }
    
    public void setup() {
        ShulkerViewer.outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
        ShulkerViewer.fillColor = this.registerColor("Fill", new GSColor(0, 0, 0, 255));
    }
    
    public static void renderShulkerPreview(final ItemStack itemStack, final int posX, final int posY, final int width, final int height) {
        final GSColor outline = new GSColor(ShulkerViewer.outlineColor.getValue(), 255);
        final GSColor fill = new GSColor(ShulkerViewer.fillColor.getValue(), 200);
        RenderUtil.draw2DRect(posX + 1, posY + 1, width - 2, height - 2, 1000, fill);
        RenderUtil.draw2DRect(posX, posY, width, 1, 1000, outline);
        RenderUtil.draw2DRect(posX, posY + height - 1, width, 1, 1000, outline);
        RenderUtil.draw2DRect(posX, posY, 1, height, 1000, outline);
        RenderUtil.draw2DRect(posX + width - 1, posY, 1, height, 1000, outline);
        GlStateManager.disableDepth();
        FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), itemStack.getDisplayName(), posX + 3, posY + 3, new GSColor(255, 255, 255, 255));
        GlStateManager.enableDepth();
        final NonNullList<ItemStack> contentItems = (NonNullList<ItemStack>)NonNullList.func_191197_a(27, (Object)ItemStack.field_190927_a);
        ItemStackHelper.func_191283_b(itemStack.getTagCompound().getCompoundTag("BlockEntityTag"), (NonNullList)contentItems);
        for (int i = 0; i < contentItems.size(); ++i) {
            final int finalX = posX + 1 + i % 9 * 18;
            final int finalY = posY + 31 + (i / 9 - 1) * 18;
            GameSenseGUI.renderItem((ItemStack)contentItems.get(i), new Point(finalX, finalY));
        }
    }
}
