



package com.gamesense.client.module.modules.movement;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraftforge.client.event.*;
import me.zero.alpine.listener.*;
import com.gamesense.api.event.events.*;
import java.util.function.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.*;
import net.minecraft.client.entity.*;
import com.gamesense.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;

public class PlayerTweaks extends Module
{
    public Setting.Boolean guiMove;
    public static Setting.Boolean noPush;
    public Setting.Boolean noSlow;
    Setting.Boolean antiKnockBack;
    @EventHandler
    private final Listener<InputUpdateEvent> eventListener;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<WaterPushEvent> waterPushEventListener;
    
    public PlayerTweaks() {
        super("PlayerTweaks", Module.Category.Movement);
        this.eventListener = (Listener<InputUpdateEvent>)new Listener(event -> {
            if (this.noSlow.getValue() && PlayerTweaks.mc.player.isHandActive() && !PlayerTweaks.mc.player.isRiding()) {
                final MovementInput movementInput = event.getMovementInput();
                movementInput.moveStrafe *= 5.0f;
                final MovementInput movementInput2 = event.getMovementInput();
                movementInput2.field_192832_b *= 5.0f;
            }
        }, new Predicate[0]);
        this.receiveListener = (Listener<PacketEvent.Receive>)new Listener(event -> {
            if (this.antiKnockBack.getValue()) {
                if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).getEntityID() == PlayerTweaks.mc.player.getEntityId()) {
                    event.cancel();
                }
                if (event.getPacket() instanceof SPacketExplosion) {
                    event.cancel();
                }
            }
        }, new Predicate[0]);
        this.waterPushEventListener = (Listener<WaterPushEvent>)new Listener(event -> {
            if (PlayerTweaks.noPush.getValue()) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        this.guiMove = this.registerBoolean("Gui Move", false);
        PlayerTweaks.noPush = this.registerBoolean("No Push", false);
        this.noSlow = this.registerBoolean("No Slow", false);
        this.antiKnockBack = this.registerBoolean("Velocity", false);
    }
    
    public void onUpdate() {
        if (this.guiMove.getValue() && PlayerTweaks.mc.currentScreen != null && !(PlayerTweaks.mc.currentScreen instanceof GuiChat)) {
            if (Keyboard.isKeyDown(200)) {
                final EntityPlayerSP player = PlayerTweaks.mc.player;
                player.rotationPitch -= 5.0f;
            }
            if (Keyboard.isKeyDown(208)) {
                final EntityPlayerSP player2 = PlayerTweaks.mc.player;
                player2.rotationPitch += 5.0f;
            }
            if (Keyboard.isKeyDown(205)) {
                final EntityPlayerSP player3 = PlayerTweaks.mc.player;
                player3.rotationYaw += 5.0f;
            }
            if (Keyboard.isKeyDown(203)) {
                final EntityPlayerSP player4 = PlayerTweaks.mc.player;
                player4.rotationYaw -= 5.0f;
            }
            if (PlayerTweaks.mc.player.rotationPitch > 90.0f) {
                PlayerTweaks.mc.player.rotationPitch = 90.0f;
            }
            if (PlayerTweaks.mc.player.rotationPitch < -90.0f) {
                PlayerTweaks.mc.player.rotationPitch = -90.0f;
            }
        }
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
}
