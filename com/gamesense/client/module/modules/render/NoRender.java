



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import me.zero.alpine.listener.*;
import net.minecraftforge.client.event.*;
import com.gamesense.api.event.events.*;
import java.util.function.*;
import net.minecraft.init.*;
import com.gamesense.client.*;
import net.minecraft.block.material.*;

public class NoRender extends Module
{
    public Setting.Boolean armor;
    Setting.Boolean fire;
    Setting.Boolean blind;
    Setting.Boolean nausea;
    public Setting.Boolean hurtCam;
    public Setting.Boolean noOverlay;
    Setting.Boolean noBossBar;
    public Setting.Boolean noSkylight;
    public static Setting.Boolean noCluster;
    public static Setting.Integer maxNoClusterRender;
    public static int currentClusterAmount;
    @EventHandler
    public Listener<RenderBlockOverlayEvent> blockOverlayEventListener;
    @EventHandler
    private final Listener<EntityViewRenderEvent.FogDensity> fogDensityListener;
    @EventHandler
    private final Listener<RenderBlockOverlayEvent> renderBlockOverlayEventListener;
    @EventHandler
    private final Listener<RenderGameOverlayEvent> renderGameOverlayEventListener;
    @EventHandler
    private final Listener<BossbarEvent> bossbarEventListener;
    
    public NoRender() {
        super("NoRender", Module.Category.Render);
        this.blockOverlayEventListener = (Listener<RenderBlockOverlayEvent>)new Listener(event -> {
            if (this.fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
                event.setCanceled(true);
            }
            if (this.noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) {
                event.setCanceled(true);
            }
            if (this.noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK) {
                event.setCanceled(true);
            }
        }, new Predicate[0]);
        this.fogDensityListener = (Listener<EntityViewRenderEvent.FogDensity>)new Listener(event -> {
            if (this.noOverlay.getValue() && (event.getState().getMaterial().equals(Material.WATER) || event.getState().getMaterial().equals(Material.LAVA))) {
                event.setDensity(0.0f);
                event.setCanceled(true);
            }
        }, new Predicate[0]);
        this.renderBlockOverlayEventListener = (Listener<RenderBlockOverlayEvent>)new Listener(event -> event.setCanceled(true), new Predicate[0]);
        this.renderGameOverlayEventListener = (Listener<RenderGameOverlayEvent>)new Listener(event -> {
            if (this.noOverlay.getValue()) {
                if (event.getType().equals((Object)RenderGameOverlayEvent.ElementType.HELMET)) {
                    event.setCanceled(true);
                }
                if (event.getType().equals((Object)RenderGameOverlayEvent.ElementType.PORTAL)) {
                    event.setCanceled(true);
                }
            }
        }, new Predicate[0]);
        this.bossbarEventListener = (Listener<BossbarEvent>)new Listener(event -> {
            if (this.noBossBar.getValue()) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        this.armor = this.registerBoolean("Armor", false);
        this.fire = this.registerBoolean("Fire", false);
        this.blind = this.registerBoolean("Blind", false);
        this.nausea = this.registerBoolean("Nausea", false);
        this.hurtCam = this.registerBoolean("HurtCam", false);
        this.noSkylight = this.registerBoolean("Skylight", false);
        this.noOverlay = this.registerBoolean("No Overlay", false);
        this.noBossBar = this.registerBoolean("No Boss Bar", false);
        NoRender.noCluster = this.registerBoolean("No Cluster", false);
        NoRender.maxNoClusterRender = this.registerInteger("No Cluster Max", 5, 1, 25);
    }
    
    public void onUpdate() {
        if (this.blind.getValue() && NoRender.mc.player.isPotionActive(MobEffects.BLINDNESS)) {
            NoRender.mc.player.removePotionEffect(MobEffects.BLINDNESS);
        }
        if (this.nausea.getValue() && NoRender.mc.player.isPotionActive(MobEffects.NAUSEA)) {
            NoRender.mc.player.removePotionEffect(MobEffects.NAUSEA);
        }
    }
    
    public void onRender() {
        NoRender.currentClusterAmount = 0;
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
    
    public static boolean incrementNoClusterRender() {
        ++NoRender.currentClusterAmount;
        return NoRender.currentClusterAmount <= NoRender.maxNoClusterRender.getValue();
    }
    
    public static boolean getNoClusterRender() {
        return NoRender.currentClusterAmount <= NoRender.maxNoClusterRender.getValue();
    }
    
    static {
        NoRender.currentClusterAmount = 0;
    }
}
