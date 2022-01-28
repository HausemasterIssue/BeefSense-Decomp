



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import net.minecraft.entity.*;
import com.gamesense.api.setting.*;
import me.zero.alpine.listener.*;
import com.gamesense.api.event.events.*;
import java.util.function.*;
import net.minecraft.entity.player.*;
import java.util.stream.*;
import com.gamesense.api.util.misc.*;
import net.minecraft.entity.item.*;
import com.mojang.realmsclient.gui.*;
import java.util.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import com.gamesense.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.world.*;

public class PvPInfo extends Module
{
    List<Entity> knownPlayers;
    List<Entity> antipearlspamplz;
    List<Entity> players;
    List<Entity> pearls;
    List<Entity> burrowedPlayers;
    List<Entity> strengthedPlayers;
    private HashMap<String, Integer> popCounterHashMap;
    Setting.Boolean visualRange;
    Setting.Boolean pearlAlert;
    Setting.Boolean strengthDetect;
    Setting.Boolean popCounter;
    Setting.Boolean burrowAlert;
    Setting.Mode ChatColor;
    @EventHandler
    private final Listener<PacketEvent.Receive> packetEventListener;
    @EventHandler
    private final Listener<TotemPopEvent> totemPopEventListener;
    
    public PvPInfo() {
        super("PvPInfo", Module.Category.Misc);
        this.knownPlayers = new ArrayList<Entity>();
        this.antipearlspamplz = new ArrayList<Entity>();
        this.burrowedPlayers = new ArrayList<Entity>();
        this.strengthedPlayers = new ArrayList<Entity>();
        this.popCounterHashMap = new HashMap<String, Integer>();
        this.packetEventListener = (Listener<PacketEvent.Receive>)new Listener(event -> {
            if (PvPInfo.mc.world == null || PvPInfo.mc.player == null) {
                return;
            }
            if (event.getPacket() instanceof SPacketEntityStatus) {
                final SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
                if (packet.getOpCode() == 35) {
                    final Entity entity = packet.getEntity((World)PvPInfo.mc.world);
                    GameSense.EVENT_BUS.post((Object)new TotemPopEvent(entity));
                }
            }
        }, new Predicate[0]);
        this.totemPopEventListener = (Listener<TotemPopEvent>)new Listener(event -> {
            if (PvPInfo.mc.world == null || PvPInfo.mc.player == null) {
                return;
            }
            if (this.popCounter.getValue()) {
                if (this.popCounterHashMap == null) {
                    this.popCounterHashMap = new HashMap<String, Integer>();
                }
                if (this.popCounterHashMap.get(event.getEntity().getName()) == null) {
                    this.popCounterHashMap.put(event.getEntity().getName(), 1);
                    MessageBus.sendClientPrefixMessage(this.getTextColor() + event.getEntity().getName() + " popped " + ChatFormatting.RED + 1 + this.getTextColor() + " totem!");
                }
                else if (this.popCounterHashMap.get(event.getEntity().getName()) != null) {
                    int popCounter = this.popCounterHashMap.get(event.getEntity().getName());
                    final int newPopCounter = ++popCounter;
                    this.popCounterHashMap.put(event.getEntity().getName(), newPopCounter);
                    MessageBus.sendClientPrefixMessage(this.getTextColor() + event.getEntity().getName() + " popped " + ChatFormatting.RED + newPopCounter + this.getTextColor() + " totems!");
                }
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        final ArrayList<String> colors = new ArrayList<String>();
        colors.add("Black");
        colors.add("Dark Green");
        colors.add("Dark Red");
        colors.add("Gold");
        colors.add("Dark Gray");
        colors.add("Green");
        colors.add("Red");
        colors.add("Yellow");
        colors.add("Dark Blue");
        colors.add("Dark Aqua");
        colors.add("Dark Purple");
        colors.add("Gray");
        colors.add("Blue");
        colors.add("Aqua");
        colors.add("Light Purple");
        colors.add("White");
        this.visualRange = this.registerBoolean("Visual Range", false);
        this.pearlAlert = this.registerBoolean("Pearl Alert", false);
        this.burrowAlert = this.registerBoolean("Burrow Alert", false);
        this.strengthDetect = this.registerBoolean("Strength Detect", false);
        this.popCounter = this.registerBoolean("Pop Counter", false);
        this.ChatColor = this.registerMode("Color", (List)colors, "Light Purple");
    }
    
    public void onUpdate() {
        if (PvPInfo.mc.player == null || PvPInfo.mc.world == null) {
            return;
        }
        if (this.visualRange.getValue()) {
            this.players = (List<Entity>)PvPInfo.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList());
            try {
                for (final Entity e2 : this.players) {
                    if (e2 instanceof EntityPlayer && !e2.getName().equalsIgnoreCase(PvPInfo.mc.player.getName()) && !this.knownPlayers.contains(e2)) {
                        this.knownPlayers.add(e2);
                        MessageBus.sendClientPrefixMessage(this.getTextColor() + e2.getName() + " has been spotted thanks to GameSense!");
                    }
                }
            }
            catch (Exception ex) {}
            try {
                for (final Entity e2 : this.knownPlayers) {
                    if (e2 instanceof EntityPlayer && !e2.getName().equalsIgnoreCase(PvPInfo.mc.player.getName()) && !this.players.contains(e2)) {
                        this.knownPlayers.remove(e2);
                    }
                }
            }
            catch (Exception ex2) {}
        }
        if (this.burrowAlert.getValue()) {
            for (final Entity entity : (List)PvPInfo.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList())) {
                if (!(entity instanceof EntityPlayer)) {
                    continue;
                }
                if (!this.burrowedPlayers.contains(entity) && this.isBurrowed(entity)) {
                    this.burrowedPlayers.add(entity);
                    MessageBus.sendClientPrefixMessage(this.getTextColor() + entity.getName() + " has just burrowed!");
                }
                else {
                    if (!this.burrowedPlayers.contains(entity) || this.isBurrowed(entity)) {
                        continue;
                    }
                    this.burrowedPlayers.remove(entity);
                    MessageBus.sendClientPrefixMessage(this.getTextColor() + entity.getName() + " is no longer burrowed!");
                }
            }
        }
        if (this.pearlAlert.getValue()) {
            this.pearls = (List<Entity>)PvPInfo.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderPearl).collect(Collectors.toList());
            try {
                for (final Entity e2 : this.pearls) {
                    if (e2 instanceof EntityEnderPearl && !this.antipearlspamplz.contains(e2)) {
                        this.antipearlspamplz.add(e2);
                        MessageBus.sendClientPrefixMessage(this.getTextColor() + e2.getEntityWorld().getClosestPlayerToEntity(e2, 3.0).getName() + " has just thrown a pearl!");
                    }
                }
            }
            catch (Exception ex3) {}
        }
        if (this.strengthDetect.getValue()) {
            for (final EntityPlayer player : PvPInfo.mc.world.playerEntities) {
                if (player.isPotionActive(MobEffects.STRENGTH) && !this.strengthedPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(this.getTextColor() + player.getName() + " has (drank) strength!");
                    this.strengthedPlayers.add((Entity)player);
                }
                if (!player.isPotionActive(MobEffects.STRENGTH) && this.strengthedPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(this.getTextColor() + player.getName() + " no longer has strength!");
                    this.strengthedPlayers.remove(player);
                }
            }
        }
        if (this.popCounter.getValue()) {
            for (final EntityPlayer player : PvPInfo.mc.world.playerEntities) {
                if (player.getHealth() <= 0.0f && this.popCounterHashMap.containsKey(player.getDisplayNameString())) {
                    MessageBus.sendClientPrefixMessage(this.getTextColor() + player.getName() + " died after popping " + ChatFormatting.GREEN + this.popCounterHashMap.get(player.getName()) + this.getTextColor() + " totems!");
                    this.popCounterHashMap.remove(player.getName(), this.popCounterHashMap.get(player.getName()));
                }
            }
        }
    }
    
    private boolean isBurrowed(final Entity entity) {
        final BlockPos entityPos = new BlockPos(this.roundValueToCenter(entity.posX), entity.posY + 0.2, this.roundValueToCenter(entity.posZ));
        return PvPInfo.mc.world.getBlockState(entityPos).getBlock() == Blocks.OBSIDIAN || PvPInfo.mc.world.getBlockState(entityPos).getBlock() == Blocks.ENDER_CHEST;
    }
    
    private double roundValueToCenter(final double inputVal) {
        double roundVal = (double)Math.round(inputVal);
        if (roundVal > inputVal) {
            roundVal -= 0.5;
        }
        else if (roundVal <= inputVal) {
            roundVal += 0.5;
        }
        return roundVal;
    }
    
    public ChatFormatting getTextColor() {
        if (this.ChatColor.getValue().equalsIgnoreCase("Black")) {
            return ChatFormatting.BLACK;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Dark Green")) {
            return ChatFormatting.DARK_GREEN;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Dark Red")) {
            return ChatFormatting.DARK_RED;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Gold")) {
            return ChatFormatting.GOLD;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Dark Gray")) {
            return ChatFormatting.DARK_GRAY;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Green")) {
            return ChatFormatting.GREEN;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Red")) {
            return ChatFormatting.RED;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Yellow")) {
            return ChatFormatting.YELLOW;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Dark Blue")) {
            return ChatFormatting.DARK_BLUE;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Dark Aqua")) {
            return ChatFormatting.DARK_AQUA;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Dark Purple")) {
            return ChatFormatting.DARK_PURPLE;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Gray")) {
            return ChatFormatting.GRAY;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Blue")) {
            return ChatFormatting.BLUE;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Light Purple")) {
            return ChatFormatting.LIGHT_PURPLE;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("White")) {
            return ChatFormatting.WHITE;
        }
        if (this.ChatColor.getValue().equalsIgnoreCase("Aqua")) {
            return ChatFormatting.AQUA;
        }
        return null;
    }
    
    public void onEnable() {
        this.popCounterHashMap = new HashMap<String, Integer>();
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        this.knownPlayers.clear();
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
}
