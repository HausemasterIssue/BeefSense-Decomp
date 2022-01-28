



package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.gamesense.client.module.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;
import com.gamesense.client.module.modules.render.*;

@Mixin({ RenderGlobal.class })
public class MixinRenderGlobal
{
    @Inject(method = { "drawSelectionBox" }, at = { @At("HEAD") }, cancellable = true)
    public void drawSelectionBox(final EntityPlayer player, final RayTraceResult movingObjectPositionIn, final int execute, final float partialTicks, final CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled((Class)BlockHighlight.class)) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "drawBlockDamageTexture" }, at = { @At("HEAD") }, cancellable = true)
    public void drawBlockDamageTexture(final Tessellator tessellatorIn, final BufferBuilder bufferBuilderIn, final Entity entityIn, final float partialTicks, final CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled((Class)BreakESP.class) && BreakESP.cancelAnimation.getValue()) {
            callbackInfo.cancel();
        }
    }
}
