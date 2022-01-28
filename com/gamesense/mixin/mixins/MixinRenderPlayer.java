



package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.gamesense.client.module.modules.render.*;
import com.gamesense.client.module.*;
import net.minecraft.entity.player.*;
import com.gamesense.client.module.modules.hud.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ RenderPlayer.class })
public abstract class MixinRenderPlayer
{
    @Inject(method = { "renderEntityName" }, at = { @At("HEAD") }, cancellable = true)
    private void renderLivingLabel(final AbstractClientPlayer entity, final double x, final double y, final double z, final String name, final double distanceSq, final CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled((Class)Nametags.class)) {
            callbackInfo.cancel();
        }
        if (ModuleManager.isModuleEnabled((Class)TargetHUD.class) && TargetHUD.isRenderingEntity((EntityPlayer)entity)) {
            callbackInfo.cancel();
        }
        if (ModuleManager.isModuleEnabled((Class)TargetInfo.class) && TargetInfo.isRenderingEntity((EntityPlayer)entity)) {
            callbackInfo.cancel();
        }
    }
}
