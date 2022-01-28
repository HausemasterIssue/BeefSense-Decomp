



package com.gamesense.api.event;

import net.minecraft.client.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import com.google.common.collect.*;
import com.gamesense.client.module.*;
import com.gamesense.client.*;
import net.minecraftforge.fml.common.eventhandler.*;
import org.lwjgl.input.*;
import com.gamesense.client.command.*;
import com.mojang.realmsclient.gui.*;
import com.gamesense.api.util.misc.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.world.*;
import net.minecraftforge.client.event.*;
import java.net.*;
import org.apache.commons.io.*;
import org.json.simple.*;
import java.io.*;
import org.json.simple.parser.*;
import net.minecraftforge.common.*;
import net.minecraft.network.play.server.*;
import com.gamesense.api.event.events.*;
import java.util.*;

public class EventProcessor
{
    public static EventProcessor INSTANCE;
    Minecraft mc;
    CommandManager commandManager;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    private final Map<String, String> uuidNameCache;
    
    public EventProcessor() {
        this.mc = Minecraft.getMinecraft();
        this.commandManager = new CommandManager();
        this.receiveListener = (Listener<PacketEvent.Receive>)new Listener(event -> {
            if (event.getPacket() instanceof SPacketPlayerListItem) {
                final SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
                if (packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                    for (final SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
                        if (playerData.getProfile().getId() != this.mc.session.getProfile().getId()) {
                            final String name;
                            new Thread(() -> {
                                name = this.resolveName(playerData.getProfile().getId().toString());
                                if (name != null && this.mc.player != null && this.mc.player.ticksExisted >= 1000) {
                                    GameSense.EVENT_BUS.post((Object)new PlayerJoinEvent(name));
                                }
                                return;
                            }).start();
                        }
                    }
                }
                if (packet.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                    for (final SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
                        if (playerData.getProfile().getId() != this.mc.session.getProfile().getId()) {
                            final SPacketPlayerListItem.AddPlayerData playerData2;
                            final String name2;
                            new Thread(() -> {
                                name2 = this.resolveName(playerData2.getProfile().getId().toString());
                                if (name2 != null && this.mc.player != null && this.mc.player.ticksExisted >= 1000) {
                                    GameSense.EVENT_BUS.post((Object)new PlayerLeaveEvent(name2));
                                }
                            }).start();
                        }
                    }
                }
            }
        }, new Predicate[0]);
        this.uuidNameCache = (Map<String, String>)Maps.newConcurrentMap();
        EventProcessor.INSTANCE = this;
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (this.mc.player != null) {
            ModuleManager.onUpdate();
        }
    }
    
    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        ModuleManager.onWorldRender(event);
    }
    
    @SubscribeEvent
    public void onRender(final RenderGameOverlayEvent.Post event) {
        GameSense.EVENT_BUS.post((Object)event);
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            ModuleManager.onRender();
        }
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            if (Keyboard.getEventKey() == 0 || Keyboard.getEventKey() == 0) {
                return;
            }
            ModuleManager.onBind(Keyboard.getEventKey());
            GameSense.getInstance().gameSenseGUI.handleKeyEvent(Keyboard.getEventKey());
        }
    }
    
    @SubscribeEvent
    public void onMouseInput(final InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButtonState()) {
            GameSense.EVENT_BUS.post((Object)event);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(final ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                this.mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                this.commandManager.callCommand(event.getMessage().substring(1));
            }
            catch (Exception e) {
                e.printStackTrace();
                MessageBus.sendCommandMessage(ChatFormatting.DARK_RED + "Error: " + e.getMessage(), true);
            }
        }
    }
    
    @SubscribeEvent
    public void onRenderScreen(final RenderGameOverlayEvent.Text event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onChatReceived(final ClientChatReceivedEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onDrawBlockHighlight(final DrawBlockHighlightEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onRenderBlockOverlay(final RenderBlockOverlayEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onLivingDamage(final LivingDamageEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onLivingEntityUseItemFinish(final LivingEntityUseItemEvent.Finish event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onInputUpdate(final InputUpdateEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onLivingDeath(final LivingDeathEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onPlayerPush(final PlayerSPPushOutOfBlocksEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onWorldLoad(final WorldEvent.Load event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    @SubscribeEvent
    public void onGuiOpen(final GuiOpenEvent event) {
        GameSense.EVENT_BUS.post((Object)event);
    }
    
    public String resolveName(String uuid) {
        uuid = uuid.replace("-", "");
        if (this.uuidNameCache.containsKey(uuid)) {
            return this.uuidNameCache.get(uuid);
        }
        final String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
        try {
            final String nameJson = IOUtils.toString(new URL(url));
            if (nameJson != null && nameJson.length() > 0) {
                final JSONArray jsonArray = (JSONArray)JSONValue.parseWithException(nameJson);
                if (jsonArray != null) {
                    final JSONObject latestName = (JSONObject)jsonArray.get(jsonArray.size() - 1);
                    if (latestName != null) {
                        return latestName.get((Object)"name").toString();
                    }
                }
            }
        }
        catch (IOException | ParseException ex2) {
            final Exception ex;
            final Exception e = ex;
            e.printStackTrace();
        }
        return null;
    }
    
    public void init() {
        GameSense.EVENT_BUS.subscribe((Object)this);
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
}
