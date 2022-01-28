



package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.gamesense.api.event.events.*;
import com.gamesense.client.*;
import net.minecraft.entity.item.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ RenderManager.class })
public class MixinRenderManager
{
    @Inject(method = { "renderEntity" }, at = { @At("HEAD") }, cancellable = true)
    public void renderEntityHead(final Entity entityIn, final double x, final double y, final double z, final float yaw, final float partialTicks, final boolean p_188391_10_, final CallbackInfo callbackInfo) {
        final RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.TEXTURE);
        GameSense.EVENT_BUS.post((Object)renderEntityHeadEvent);
        if (entityIn instanceof EntityEnderPearl || entityIn instanceof EntityXPOrb || entityIn instanceof EntityExpBottle || entityIn instanceof EntityEnderCrystal) {
            final RenderEntityEvent.Head renderEntityEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.COLOR);
            GameSense.EVENT_BUS.post((Object)renderEntityEvent);
            if (renderEntityEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        }
        if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "renderEntity" }, at = { @At("RETURN") }, cancellable = true)
    public void renderEntityReturn(final Entity entityIn, final double x, final double y, final double z, final float yaw, final float partialTicks, final boolean p_188391_10_, final CallbackInfo callbackInfo) {
        final RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return(entityIn, RenderEntityEvent.Type.TEXTURE);
        GameSense.EVENT_BUS.post((Object)renderEntityReturnEvent);
        if (entityIn instanceof EntityEnderPearl || entityIn instanceof EntityXPOrb || entityIn instanceof EntityExpBottle || entityIn instanceof EntityEnderCrystal) {
            final RenderEntityEvent.Return renderEntityEvent = new RenderEntityEvent.Return(entityIn, RenderEntityEvent.Type.COLOR);
            GameSense.EVENT_BUS.post((Object)renderEntityEvent);
            if (renderEntityEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        }
        if (renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
