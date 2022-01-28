



package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.render.*;
import com.gamesense.api.util.font.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ FontRenderer.class })
public class MixinFontRenderer
{
    @Redirect(method = { "drawStringWithShadow" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"))
    public int drawCustomFontStringWithShadow(final FontRenderer fontRenderer, final String text, final float x, final float y, final int color, final boolean dropShadow) {
        return ColorMain.textFont.getValue() ? ((int)FontUtil.drawStringWithShadow(true, text, (int)x, (int)y, new GSColor(color))) : fontRenderer.drawString(text, x, y, color, true);
    }
}
