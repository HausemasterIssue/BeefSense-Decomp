



package com.gamesense.mixin.mixins;

import java.util.*;
import org.spongepowered.asm.mixin.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.client.module.*;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ GuiNewChat.class })
public abstract class MixinGuiNewChat
{
    @Shadow
    private int scrollPos;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;
    
    @Shadow
    public abstract int getLineCount();
    
    @Redirect(method = { "drawChat" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    private void drawRectBackgroundClean(final int left, final int top, final int right, final int bottom, final int color) {
        final ChatModifier chatModifier = (ChatModifier)ModuleManager.getModule((Class)ChatModifier.class);
        if (!chatModifier.isEnabled() || !chatModifier.clearBkg.getValue()) {
            Gui.drawRect(left, top, right, bottom, color);
        }
    }
}
