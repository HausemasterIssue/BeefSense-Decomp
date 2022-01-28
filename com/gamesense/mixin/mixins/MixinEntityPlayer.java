



package com.gamesense.mixin.mixins;

import net.minecraft.entity.player.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.*;
import com.gamesense.client.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.gamesense.api.event.events.*;

@Mixin({ EntityPlayer.class })
public abstract class MixinEntityPlayer
{
    @Shadow
    public abstract String getName();
    
    @Inject(method = { "jump" }, at = { @At("HEAD") }, cancellable = true)
    public void onJump(final CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().player.getName() == this.getName()) {
            GameSense.EVENT_BUS.post((Object)new PlayerJumpEvent());
        }
    }
    
    @Inject(method = { "isPushedByWater" }, at = { @At("HEAD") }, cancellable = true)
    private void onPushedByWater(final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final WaterPushEvent event = new WaterPushEvent();
        GameSense.EVENT_BUS.post((Object)event);
        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue((Object)false);
        }
    }
}
