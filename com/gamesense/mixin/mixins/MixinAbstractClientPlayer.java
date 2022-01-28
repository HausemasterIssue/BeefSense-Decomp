



package com.gamesense.mixin.mixins;

import net.minecraft.client.entity.*;
import net.minecraft.client.network.*;
import org.spongepowered.asm.mixin.*;
import javax.annotation.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.util.*;
import com.gamesense.client.module.modules.render.*;
import com.gamesense.client.module.*;
import com.gamesense.client.*;
import java.util.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ AbstractClientPlayer.class })
public abstract class MixinAbstractClientPlayer
{
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();
    
    @Inject(method = { "getLocationCape" }, at = { @At("HEAD") }, cancellable = true)
    public void getLocationCape(final CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        final UUID uuid = this.getPlayerInfo().getGameProfile().getId();
        final CapesModule capesModule = (CapesModule)ModuleManager.getModule((Class)CapesModule.class);
        if (capesModule.isEnabled() && GameSense.getInstance().capeUtil.hasCape(uuid)) {
            if (capesModule.capeMode.getValue().equalsIgnoreCase("Black")) {
                callbackInfoReturnable.setReturnValue((Object)new ResourceLocation("gamesense:capeblack.png"));
            }
            else {
                callbackInfoReturnable.setReturnValue((Object)new ResourceLocation("gamesense:capewhite.png"));
            }
        }
    }
}
