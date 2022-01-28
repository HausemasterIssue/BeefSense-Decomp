



package com.gamesense.api.util.font;

import net.minecraft.client.*;
import com.gamesense.api.util.render.*;
import com.gamesense.client.*;

public class FontUtil
{
    private static final Minecraft mc;
    
    public static float drawStringWithShadow(final boolean customFont, final String text, final int x, final int y, final GSColor color) {
        if (customFont) {
            return GameSense.getInstance().cFontRenderer.drawStringWithShadow(text, (double)x, (double)y, color);
        }
        return (float)FontUtil.mc.fontRendererObj.drawStringWithShadow(text, (float)x, (float)y, color.getRGB());
    }
    
    public static int getStringWidth(final boolean customFont, final String string) {
        if (customFont) {
            return GameSense.getInstance().cFontRenderer.getStringWidth(string);
        }
        return FontUtil.mc.fontRendererObj.getStringWidth(string);
    }
    
    public static int getFontHeight(final boolean customFont) {
        if (customFont) {
            return GameSense.getInstance().cFontRenderer.getHeight();
        }
        return FontUtil.mc.fontRendererObj.FONT_HEIGHT;
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
