



package com.gamesense.client.module.modules.movement;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.client.entity.*;
import net.minecraft.network.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.concurrent.*;
import java.util.function.*;
import com.gamesense.client.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import java.util.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.network.play.client.*;

public class Blink extends Module
{
    Setting.Boolean ghostPlayer;
    EntityOtherPlayerMP entity;
    private final Queue<Packet> packets;
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener;
    
    public Blink() {
        super("Blink", Module.Category.Movement);
        this.packets = new ConcurrentLinkedQueue<Packet>();
        this.packetSendListener = (Listener<PacketEvent.Send>)new Listener(event -> {
            final Packet packet = event.getPacket();
            if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
                return;
            }
            if (Blink.mc.player == null || Blink.mc.player.isDead) {
                this.packets.add(packet);
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        this.ghostPlayer = this.registerBoolean("Ghost Player", true);
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
        if (this.ghostPlayer.getValue() && Blink.mc.player != null) {
            (this.entity = new EntityOtherPlayerMP((World)Blink.mc.world, Blink.mc.getSession().getProfile())).copyLocationAndAnglesFrom((Entity)Blink.mc.player);
            this.entity.inventory.copyInventory(Blink.mc.player.inventory);
            this.entity.rotationYaw = Blink.mc.player.rotationYaw;
            this.entity.rotationYawHead = Blink.mc.player.rotationYawHead;
            Blink.mc.world.addEntityToWorld(667, (Entity)this.entity);
        }
    }
    
    public void onUpdate() {
        if (!this.ghostPlayer.getValue() && this.entity != null) {
            Blink.mc.world.removeEntity((Entity)this.entity);
        }
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
        if (this.entity != null) {
            Blink.mc.world.removeEntity((Entity)this.entity);
        }
        if (this.packets.size() > 0 && Blink.mc.player != null) {
            for (final Packet packet : this.packets) {
                Blink.mc.player.connection.sendPacket(packet);
            }
            this.packets.clear();
        }
    }
    
    public String getHudInfo() {
        final String t = "[" + ChatFormatting.WHITE + this.packets.size() + ChatFormatting.GRAY + "]";
        return t;
    }
}
