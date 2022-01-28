



package com.gamesense.mixin.mixins;

import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.multiplayer.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.client.module.*;
import org.spongepowered.asm.mixin.injection.*;
import com.gamesense.mixin.mixins.accessor.*;

@Mixin({ Minecraft.class })
public class MixinMinecraft
{
    @Shadow
    public EntityPlayerSP player;
    @Shadow
    public PlayerControllerMP playerController;
    private boolean handActive;
    private boolean isHittingBlock;
    
    public MixinMinecraft() {
        this.handActive = false;
        this.isHittingBlock = false;
    }
    
    @Inject(method = { "rightClickMouse" }, at = { @At("HEAD") })
    public void rightClickMousePre(final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled((Class)MultiTask.class)) {
            this.isHittingBlock = this.playerController.getIsHittingBlock();
            this.playerController.isHittingBlock = false;
        }
    }
    
    @Inject(method = { "rightClickMouse" }, at = { @At("RETURN") })
    public void rightClickMousePost(final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled((Class)MultiTask.class) && !this.playerController.getIsHittingBlock()) {
            this.playerController.isHittingBlock = this.isHittingBlock;
        }
    }
    
    @Inject(method = { "sendClickBlockToController" }, at = { @At("HEAD") })
    public void sendClickBlockToControllerPre(final boolean leftClick, final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled((Class)MultiTask.class)) {
            this.handActive = this.player.isHandActive();
            ((AccessorEntityPlayerSP)this.player).gsSetHandActive(false);
        }
    }
    
    @Inject(method = { "sendClickBlockToController" }, at = { @At("RETURN") })
    public void sendClickBlockToControllerPost(final boolean leftClick, final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled((Class)MultiTask.class) && !this.player.isHandActive()) {
            ((AccessorEntityPlayerSP)this.player).gsSetHandActive(this.handActive);
        }
    }
}
