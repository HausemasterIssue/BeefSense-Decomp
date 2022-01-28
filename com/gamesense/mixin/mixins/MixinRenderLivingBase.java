



package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.entity.*;
import com.gamesense.client.module.modules.render.*;
import com.gamesense.client.module.*;
import net.minecraft.client.renderer.*;
import com.gamesense.api.event.events.*;
import com.gamesense.client.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ RenderLivingBase.class })
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T>
{
    protected final Minecraft mc;
    private boolean isClustered;
    
    protected MixinRenderLivingBase() {
        super((RenderManager)null);
        this.mc = Minecraft.getMinecraft();
    }
    
    @Inject(method = { "renderModel" }, at = { @At("HEAD") }, cancellable = true)
    protected void renderModel(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final CallbackInfo callbackInfo) {
        if (!this.bindEntityTexture((Entity)entitylivingbaseIn)) {
            return;
        }
        final NoRender noRender = (NoRender)ModuleManager.getModule((Class)NoRender.class);
        if (noRender.isEnabled() && NoRender.noCluster.getValue() && this.mc.player.getDistanceToEntity((Entity)entitylivingbaseIn) < 1.0f && entitylivingbaseIn != this.mc.player) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            this.isClustered = true;
            if (!NoRender.incrementNoClusterRender()) {
                callbackInfo.cancel();
            }
        }
        else {
            this.isClustered = false;
        }
        final RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head((Entity)entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
        GameSense.EVENT_BUS.post((Object)renderEntityHeadEvent);
        if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "renderModel" }, at = { @At("RETURN") }, cancellable = true)
    protected void renderModelReturn(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final CallbackInfo callbackInfo) {
        final RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return((Entity)entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
        GameSense.EVENT_BUS.post((Object)renderEntityReturnEvent);
        if (!renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "renderLayers" }, at = { @At("HEAD") }, cancellable = true)
    protected void renderLayers(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleIn, final CallbackInfo callbackInfo) {
        if (this.isClustered && !NoRender.getNoClusterRender()) {
            callbackInfo.cancel();
        }
    }
    
    @Redirect(method = { "setBrightness" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal = 6))
    protected void glTexEnvi0(int target, final int parameterName, final int parameter) {
        if (!this.isClustered) {
            GlStateManager.glTexEnvi(target, parameterName, parameter);
        }
    }
    
    @Redirect(method = { "setBrightness" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal = 7))
    protected void glTexEnvi1(int target, final int parameterName, final int parameter) {
        if (!this.isClustered) {
            GlStateManager.glTexEnvi(target, parameterName, parameter);
        }
    }
    
    @Redirect(method = { "setBrightness" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal = 8))
    protected void glTexEnvi2(int target, final int parameterName, final int parameter) {
        if (!this.isClustered) {
            GlStateManager.glTexEnvi(target, parameterName, parameter);
        }
    }
}
