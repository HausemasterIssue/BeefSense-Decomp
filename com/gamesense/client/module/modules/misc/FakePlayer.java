



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import net.minecraft.client.entity.*;
import java.util.*;
import com.mojang.authlib.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;

public class FakePlayer extends Module
{
    private EntityOtherPlayerMP clonedPlayer;
    
    public FakePlayer() {
        super("FakePlayer", Module.Category.Misc);
    }
    
    public void onEnable() {
        if (FakePlayer.mc.player == null || FakePlayer.mc.player.isDead) {
            this.disable();
            return;
        }
        (this.clonedPlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.world, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), "Fit"))).copyLocationAndAnglesFrom((Entity)FakePlayer.mc.player);
        this.clonedPlayer.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
        this.clonedPlayer.rotationYaw = FakePlayer.mc.player.rotationYaw;
        this.clonedPlayer.rotationPitch = FakePlayer.mc.player.rotationPitch;
        this.clonedPlayer.setGameType(GameType.SURVIVAL);
        this.clonedPlayer.setHealth(20.0f);
        FakePlayer.mc.world.addEntityToWorld(-1234, (Entity)this.clonedPlayer);
        this.clonedPlayer.onLivingUpdate();
    }
    
    public void onDisable() {
        if (FakePlayer.mc.world != null) {
            FakePlayer.mc.world.removeEntityFromWorld(-1234);
        }
    }
}
