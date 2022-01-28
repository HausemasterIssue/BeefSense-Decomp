



package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.util.math.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.gamesense.client.module.modules.movement.*;
import com.gamesense.client.module.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ BlockSoulSand.class })
public class MixinBlockSoulSand
{
    @Inject(method = { "onEntityCollision" }, at = { @At("HEAD") }, cancellable = true)
    public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final IBlockState state, final Entity entityIn, final CallbackInfo callbackInfo) {
        final PlayerTweaks playerTweaks = (PlayerTweaks)ModuleManager.getModule((Class)PlayerTweaks.class);
        if (playerTweaks.isEnabled() && playerTweaks.noSlow.getValue()) {
            callbackInfo.cancel();
        }
    }
}
