



package com.gamesense.mixin.mixins;

import net.minecraft.client.multiplayer.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.util.math.*;
import com.gamesense.client.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.util.*;
import com.gamesense.api.event.events.*;
import com.gamesense.client.module.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.gamesense.client.module.modules.misc.*;
import net.minecraft.entity.player.*;
import com.gamesense.client.module.modules.exploits.*;
import net.minecraft.item.*;

@Mixin({ PlayerControllerMP.class })
public abstract class MixinPlayerControllerMP
{
    @Shadow
    public abstract void syncCurrentPlayItem();
    
    @Inject(method = { "onPlayerDestroyBlock" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V") }, cancellable = true)
    private void onPlayerDestroyBlock(final BlockPos pos, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        GameSense.EVENT_BUS.post((Object)new DestroyBlockEvent(pos));
    }
    
    @Inject(method = { "onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z" }, at = { @At("HEAD") }, cancellable = true)
    private void onPlayerDamageBlock(final BlockPos posBlock, final EnumFacing directionFacing, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final DamageBlockEvent event = new DamageBlockEvent(posBlock, directionFacing);
        GameSense.EVENT_BUS.post((Object)event);
        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue((Object)false);
        }
    }
    
    @Inject(method = { "getBlockReachDistance" }, at = { @At("RETURN") }, cancellable = true)
    private void getReachDistanceHook(final CallbackInfoReturnable<Float> distance) {
        if (ModuleManager.isModuleEnabled((Class)Reach.class)) {
            distance.setReturnValue((Object)(float)Reach.distance.getValue());
        }
    }
    
    @Inject(method = { "resetBlockRemoving" }, at = { @At("HEAD") }, cancellable = true)
    private void resetBlock(final CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled((Class)MultiTask.class)) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "onStoppedUsingItem" }, at = { @At("HEAD") }, cancellable = true)
    public void onStoppedUsingItem(final EntityPlayer playerIn, final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled((Class)PacketUse.class) && ((PacketUse.food.getValue() && playerIn.getHeldItem(playerIn.getActiveHand()).getItem() instanceof ItemFood) || (PacketUse.potion.getValue() && playerIn.getHeldItem(playerIn.getActiveHand()).getItem() instanceof ItemPotion) || PacketUse.all.getValue())) {
            this.syncCurrentPlayItem();
            playerIn.stopActiveHand();
            ci.cancel();
        }
    }
}
