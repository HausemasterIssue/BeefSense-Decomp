



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import me.zero.alpine.listener.*;
import net.minecraftforge.event.world.*;
import java.util.concurrent.*;
import com.gamesense.api.event.events.*;
import java.util.function.*;
import com.gamesense.client.*;
import net.minecraft.client.renderer.*;
import com.gamesense.api.util.render.*;
import java.text.*;
import java.util.*;
import com.gamesense.api.util.misc.*;

public class LogoutSpots extends Module
{
    Setting.Boolean chatMsg;
    Setting.Boolean nameTag;
    Setting.Integer lineWidth;
    Setting.Integer range;
    Setting.Mode renderMode;
    Setting.ColorSetting color;
    Map<Entity, String> loggedPlayers;
    Set<EntityPlayer> worldPlayers;
    Timer timer;
    @EventHandler
    private final Listener<PlayerJoinEvent> playerJoinEventListener;
    @EventHandler
    private final Listener<PlayerLeaveEvent> playerLeaveEventListener;
    @EventHandler
    private final Listener<WorldEvent.Unload> unloadListener;
    @EventHandler
    private final Listener<WorldEvent.Load> loadListener;
    
    public LogoutSpots() {
        super("LogoutSpots", Module.Category.Render);
        this.loggedPlayers = new ConcurrentHashMap<Entity, String>();
        this.worldPlayers = (Set<EntityPlayer>)ConcurrentHashMap.newKeySet();
        this.timer = new Timer();
        this.playerJoinEventListener = (Listener<PlayerJoinEvent>)new Listener(event -> {
            if (LogoutSpots.mc.world != null) {
                this.loggedPlayers.keySet().removeIf(entity -> {
                    if (entity.getName().equalsIgnoreCase(event.getName())) {
                        if (this.chatMsg.getValue()) {
                            MessageBus.sendClientPrefixMessage(event.getName() + " reconnected!");
                        }
                        return true;
                    }
                    else {
                        return false;
                    }
                });
            }
        }, new Predicate[0]);
        this.playerLeaveEventListener = (Listener<PlayerLeaveEvent>)new Listener(event -> {
            if (LogoutSpots.mc.world != null) {
                String date;
                String location;
                this.worldPlayers.removeIf(entity -> {
                    if (entity.getName().equalsIgnoreCase(event.getName())) {
                        date = new SimpleDateFormat("k:mm").format(new Date());
                        this.loggedPlayers.put((Entity)entity, date);
                        if (this.chatMsg.getValue() && this.timer.getTimePassed() / 50L >= 5L) {
                            location = "(" + (int)entity.posX + "," + (int)entity.posY + "," + (int)entity.posZ + ")";
                            MessageBus.sendClientPrefixMessage(event.getName() + " disconnected at " + location + "!");
                            this.timer.reset();
                        }
                        return true;
                    }
                    else {
                        return false;
                    }
                });
            }
        }, new Predicate[0]);
        this.unloadListener = (Listener<WorldEvent.Unload>)new Listener(event -> {
            this.worldPlayers.clear();
            if (LogoutSpots.mc.player == null || LogoutSpots.mc.world == null) {
                this.loggedPlayers.clear();
            }
        }, new Predicate[0]);
        this.loadListener = (Listener<WorldEvent.Load>)new Listener(event -> {
            this.worldPlayers.clear();
            if (LogoutSpots.mc.player == null || LogoutSpots.mc.world == null) {
                this.loggedPlayers.clear();
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        final ArrayList<String> renderModes = new ArrayList<String>();
        renderModes.add("Both");
        renderModes.add("Outline");
        renderModes.add("Fill");
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
        this.nameTag = this.registerBoolean("Nametag", true);
        this.lineWidth = this.registerInteger("Width", 1, 1, 10);
        this.renderMode = this.registerMode("Render", (List)renderModes, "Both");
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    }
    
    public void onUpdate() {
        LogoutSpots.mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer != LogoutSpots.mc.player).filter(entityPlayer -> entityPlayer.getDistanceToEntity((Entity)LogoutSpots.mc.player) <= this.range.getValue()).forEach(entityPlayer -> this.worldPlayers.add(entityPlayer));
    }
    
    public void onWorldRender(final RenderEvent event) {
        if (LogoutSpots.mc.player != null && LogoutSpots.mc.world != null) {
            this.loggedPlayers.forEach(this::startFunction);
        }
    }
    
    public void onEnable() {
        this.loggedPlayers.clear();
        this.worldPlayers = (Set<EntityPlayer>)ConcurrentHashMap.newKeySet();
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        this.worldPlayers.clear();
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
    
    private void startFunction(final Entity entity, final String string) {
        if (entity.getDistanceToEntity((Entity)LogoutSpots.mc.player) > this.range.getValue()) {
            return;
        }
        final int posX = (int)entity.posX;
        final int posY = (int)entity.posY;
        final int posZ = (int)entity.posZ;
        final String[] nameTagMessage = { entity.getName() + " (" + string + ")", "(" + posX + "," + posY + "," + posZ + ")" };
        GlStateManager.pushMatrix();
        RenderUtil.drawNametag(entity, nameTagMessage, this.color.getValue(), 0);
        final String value = this.renderMode.getValue();
        switch (value) {
            case "Both": {
                RenderUtil.drawBoundingBox(entity.getRenderBoundingBox(), (double)this.lineWidth.getValue(), this.color.getValue());
                RenderUtil.drawBox(entity.getRenderBoundingBox(), true, -0.4, new GSColor(this.color.getValue(), 50), 63);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(entity.getRenderBoundingBox(), (double)this.lineWidth.getValue(), this.color.getValue());
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(entity.getRenderBoundingBox(), true, -0.4, new GSColor(this.color.getValue(), 50), 63);
                break;
            }
        }
        GlStateManager.popMatrix();
    }
}
