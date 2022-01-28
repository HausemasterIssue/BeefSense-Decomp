



package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.scoreboard.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.player.enemy.*;

@Mixin({ GuiPlayerTabOverlay.class })
public class MixinGuiPlayerTabOverlay
{
    @Inject(method = { "getPlayerName" }, at = { @At("HEAD") }, cancellable = true)
    public void getPlayerName(final NetworkPlayerInfo networkPlayerInfoIn, final CallbackInfoReturnable callbackInfoReturnable) {
        callbackInfoReturnable.cancel();
        callbackInfoReturnable.setReturnValue((Object)this.getPlayerName(networkPlayerInfoIn));
    }
    
    public String getPlayerName(final NetworkPlayerInfo networkPlayerInfoIn) {
        final String displayName = (networkPlayerInfoIn.getDisplayName() != null) ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName((Team)networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (Friends.isFriend(displayName)) {
            return ColorMain.getFriendColor() + displayName;
        }
        if (Enemies.isEnemy(displayName)) {
            return ColorMain.getEnemyColor() + displayName;
        }
        return displayName;
    }
}
