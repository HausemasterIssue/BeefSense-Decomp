



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import java.util.concurrent.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import net.minecraftforge.event.entity.living.*;
import java.util.function.*;
import com.gamesense.client.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.misc.*;
import net.minecraft.entity.*;
import net.minecraft.network.play.client.*;
import net.minecraft.world.*;
import java.util.*;

public class AutoGG extends Module
{
    public static AutoGG INSTANCE;
    static List<String> AutoGgMessages;
    private ConcurrentHashMap targetedPlayers;
    int index;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    @EventHandler
    private final Listener<LivingDeathEvent> livingDeathEventListener;
    
    public AutoGG() {
        super("AutoGG", Module.Category.Misc);
        this.targetedPlayers = null;
        this.index = -1;
        this.sendListener = (Listener<PacketEvent.Send>)new Listener(event -> {
            if (AutoGG.mc.player != null) {
                if (this.targetedPlayers == null) {
                    this.targetedPlayers = new ConcurrentHashMap();
                }
                if (event.getPacket() instanceof CPacketUseEntity) {
                    final CPacketUseEntity cPacketUseEntity = (CPacketUseEntity)event.getPacket();
                    if (cPacketUseEntity.getAction().equals((Object)CPacketUseEntity.Action.ATTACK)) {
                        final Entity targetEntity = cPacketUseEntity.getEntityFromWorld((World)AutoGG.mc.world);
                        if (targetEntity instanceof EntityPlayer) {
                            this.addTargetedPlayer(targetEntity.getName());
                        }
                    }
                }
            }
        }, new Predicate[0]);
        this.livingDeathEventListener = (Listener<LivingDeathEvent>)new Listener(event -> {
            if (AutoGG.mc.player != null) {
                if (this.targetedPlayers == null) {
                    this.targetedPlayers = new ConcurrentHashMap();
                }
                final EntityLivingBase entity = event.getEntityLiving();
                if (entity != null && entity instanceof EntityPlayer) {
                    final EntityPlayer player = (EntityPlayer)entity;
                    if (player.getHealth() <= 0.0f) {
                        final String name = player.getName();
                        if (this.shouldAnnounce(name)) {
                            this.doAnnounce(name);
                        }
                    }
                }
            }
        }, new Predicate[0]);
        AutoGG.INSTANCE = this;
    }
    
    public void onEnable() {
        this.targetedPlayers = new ConcurrentHashMap();
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        this.targetedPlayers = null;
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
    
    public void onUpdate() {
        if (this.targetedPlayers == null) {
            this.targetedPlayers = new ConcurrentHashMap();
        }
        for (final Entity entity : AutoGG.mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)entity;
                if (player.getHealth() > 0.0f) {
                    continue;
                }
                final String name = player.getName();
                if (this.shouldAnnounce(name)) {
                    this.doAnnounce(name);
                    break;
                }
                continue;
            }
        }
        this.targetedPlayers.forEach((namex, timeout) -> {
            if (timeout <= 0) {
                this.targetedPlayers.remove(namex);
            }
            else {
                this.targetedPlayers.put(namex, timeout - 1);
            }
        });
    }
    
    private boolean shouldAnnounce(final String name) {
        return this.targetedPlayers.containsKey(name);
    }
    
    private void doAnnounce(final String name) {
        this.targetedPlayers.remove(name);
        if (this.index >= AutoGG.AutoGgMessages.size() - 1) {
            this.index = -1;
        }
        ++this.index;
        String message;
        if (AutoGG.AutoGgMessages.size() > 0) {
            message = AutoGG.AutoGgMessages.get(this.index);
        }
        else {
            message = "GG! GameSense v0.0.1 is on top!";
        }
        String messageSanitized = message.replaceAll("\u00e0¸¢\u00e0¸\u2021", "").replace("{name}", name);
        if (messageSanitized.length() > 255) {
            messageSanitized = messageSanitized.substring(0, 255);
        }
        MessageBus.sendServerMessage(messageSanitized);
    }
    
    public void addTargetedPlayer(final String name) {
        if (!Objects.equals(name, AutoGG.mc.player.getName())) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }
            this.targetedPlayers.put(name, 20);
        }
    }
    
    public static void addAutoGgMessage(final String s) {
        AutoGG.AutoGgMessages.add(s);
    }
    
    public static List<String> getAutoGgMessages() {
        return AutoGG.AutoGgMessages;
    }
    
    static {
        AutoGG.AutoGgMessages = new ArrayList<String>();
    }
}
