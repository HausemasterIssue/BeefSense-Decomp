



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.entity.*;
import me.zero.alpine.listener.*;
import net.minecraftforge.client.event.*;
import com.gamesense.api.event.events.*;
import java.util.function.*;
import com.gamesense.client.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.entity.*;
import net.minecraft.network.play.client.*;

public class Freecam extends Module
{
    Setting.Boolean cancelPackets;
    Setting.Double speed;
    private double posX;
    private double posY;
    private double posZ;
    private float pitch;
    private float yaw;
    private EntityOtherPlayerMP clonedPlayer;
    private boolean isRidingEntity;
    private Entity ridingEntity;
    @EventHandler
    private final Listener<PlayerMoveEvent> moveListener;
    @EventHandler
    private final Listener<PlayerSPPushOutOfBlocksEvent> pushListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    
    public Freecam() {
        super("Freecam", Module.Category.Render);
        this.moveListener = (Listener<PlayerMoveEvent>)new Listener(event -> Freecam.mc.player.noClip = true, new Predicate[0]);
        this.pushListener = (Listener<PlayerSPPushOutOfBlocksEvent>)new Listener(event -> event.setCanceled(true), new Predicate[0]);
        this.sendListener = (Listener<PacketEvent.Send>)new Listener(event -> {
            if ((event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput) && this.cancelPackets.getValue()) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        this.cancelPackets = this.registerBoolean("Cancel Packets", true);
        this.speed = this.registerDouble("Speed", 10.0, 0.0, 20.0);
    }
    
    protected void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
        if (Freecam.mc.player != null) {
            this.isRidingEntity = (Freecam.mc.player.getRidingEntity() != null);
            if (Freecam.mc.player.getRidingEntity() == null) {
                this.posX = Freecam.mc.player.posX;
                this.posY = Freecam.mc.player.posY;
                this.posZ = Freecam.mc.player.posZ;
            }
            else {
                this.ridingEntity = Freecam.mc.player.getRidingEntity();
                Freecam.mc.player.dismountRidingEntity();
            }
            this.pitch = Freecam.mc.player.rotationPitch;
            this.yaw = Freecam.mc.player.rotationYaw;
            (this.clonedPlayer = new EntityOtherPlayerMP((World)Freecam.mc.world, Freecam.mc.getSession().getProfile())).copyLocationAndAnglesFrom((Entity)Freecam.mc.player);
            this.clonedPlayer.rotationYawHead = Freecam.mc.player.rotationYawHead;
            Freecam.mc.world.addEntityToWorld(-100, (Entity)this.clonedPlayer);
            Freecam.mc.player.capabilities.isFlying = true;
            Freecam.mc.player.capabilities.setFlySpeed((float)(this.speed.getValue() / 100.0));
            Freecam.mc.player.noClip = true;
        }
    }
    
    protected void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
        final EntityPlayer localPlayer = (EntityPlayer)Freecam.mc.player;
        if (localPlayer != null) {
            Freecam.mc.player.setPositionAndRotation(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            Freecam.mc.world.removeEntityFromWorld(-100);
            this.clonedPlayer = null;
            final double posX = 0.0;
            this.posZ = posX;
            this.posY = posX;
            this.posX = posX;
            final float n = 0.0f;
            this.yaw = n;
            this.pitch = n;
            Freecam.mc.player.capabilities.isFlying = false;
            Freecam.mc.player.capabilities.setFlySpeed(0.05f);
            Freecam.mc.player.noClip = false;
            final EntityPlayerSP player = Freecam.mc.player;
            final EntityPlayerSP player2 = Freecam.mc.player;
            final EntityPlayerSP player3 = Freecam.mc.player;
            final double motionX = 0.0;
            player3.motionZ = motionX;
            player2.motionY = motionX;
            player.motionX = motionX;
            if (this.isRidingEntity) {
                Freecam.mc.player.startRiding(this.ridingEntity, true);
            }
        }
    }
    
    public void onUpdate() {
        Freecam.mc.player.capabilities.isFlying = true;
        Freecam.mc.player.capabilities.setFlySpeed((float)(this.speed.getValue() / 100.0));
        Freecam.mc.player.noClip = true;
        Freecam.mc.player.onGround = false;
        Freecam.mc.player.fallDistance = 0.0f;
    }
}
