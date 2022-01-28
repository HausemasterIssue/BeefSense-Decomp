



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import java.util.*;
import com.gamesense.api.util.render.*;
import com.gamesense.client.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.item.*;

public class Chams extends Module
{
    Setting.Mode chamsType;
    Setting.ColorSetting playerColor;
    Setting.ColorSetting mobColor;
    Setting.ColorSetting crystalColor;
    Setting.Integer colorOpacity;
    Setting.Integer wireOpacity;
    Setting.Integer lineWidth;
    Setting.Integer range;
    Setting.Boolean player;
    Setting.Boolean mob;
    Setting.Boolean crystal;
    @EventHandler
    private final Listener<RenderEntityEvent.Head> renderEntityHeadEventListener;
    @EventHandler
    private final Listener<RenderEntityEvent.Return> renderEntityReturnEventListener;
    
    public Chams() {
        super("Chams", Module.Category.Render);
        this.renderEntityHeadEventListener = (Listener<RenderEntityEvent.Head>)new Listener(event -> {
            if (event.getType() == RenderEntityEvent.Type.COLOR && this.chamsType.getValue().equalsIgnoreCase("Texture")) {
                return;
            }
            if (event.getType() == RenderEntityEvent.Type.TEXTURE && (this.chamsType.getValue().equalsIgnoreCase("Color") || this.chamsType.getValue().equalsIgnoreCase("WireFrame"))) {
                return;
            }
            if (Chams.mc.player == null || Chams.mc.world == null) {
                return;
            }
            final Entity entity1 = event.getEntity();
            if (entity1.getDistanceToEntity((Entity)Chams.mc.player) > this.range.getValue()) {
                return;
            }
            if (this.player.getValue() && entity1 instanceof EntityPlayer && entity1 != Chams.mc.player) {
                this.renderChamsPre(new GSColor(this.playerColor.getValue(), 255), true);
            }
            if (this.mob.getValue() && (entity1 instanceof EntityCreature || entity1 instanceof EntitySlime || entity1 instanceof EntitySquid)) {
                this.renderChamsPre(new GSColor(this.mobColor.getValue(), 255), false);
            }
            if (this.crystal.getValue() && entity1 instanceof EntityEnderCrystal) {
                this.renderChamsPre(new GSColor(this.crystalColor.getValue(), 255), false);
            }
        }, new Predicate[0]);
        this.renderEntityReturnEventListener = (Listener<RenderEntityEvent.Return>)new Listener(event -> {
            if (event.getType() == RenderEntityEvent.Type.COLOR && this.chamsType.getValue().equalsIgnoreCase("Texture")) {
                return;
            }
            if (event.getType() == RenderEntityEvent.Type.TEXTURE && (this.chamsType.getValue().equalsIgnoreCase("Color") || this.chamsType.getValue().equalsIgnoreCase("WireFrame"))) {
                return;
            }
            if (Chams.mc.player == null || Chams.mc.world == null) {
                return;
            }
            final Entity entity1 = event.getEntity();
            if (entity1.getDistanceToEntity((Entity)Chams.mc.player) > this.range.getValue()) {
                return;
            }
            if (this.player.getValue() && entity1 instanceof EntityPlayer && entity1 != Chams.mc.player) {
                this.renderChamsPost(true);
            }
            if (this.mob.getValue() && (entity1 instanceof EntityCreature || entity1 instanceof EntitySlime || entity1 instanceof EntitySquid)) {
                this.renderChamsPost(false);
            }
            if (this.crystal.getValue() && entity1 instanceof EntityEnderCrystal) {
                this.renderChamsPost(false);
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        final ArrayList<String> chamsTypes = new ArrayList<String>();
        chamsTypes.add("Texture");
        chamsTypes.add("Color");
        chamsTypes.add("WireFrame");
        this.chamsType = this.registerMode("Type", (List)chamsTypes, "Texture");
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.player = this.registerBoolean("Player", true);
        this.mob = this.registerBoolean("Mob", false);
        this.crystal = this.registerBoolean("Crystal", false);
        this.lineWidth = this.registerInteger("Line Width", 1, 1, 5);
        this.colorOpacity = this.registerInteger("Color Opacity", 100, 0, 255);
        this.wireOpacity = this.registerInteger("Wire Opacity", 200, 0, 255);
        this.playerColor = this.registerColor("Player Color", new GSColor(0, 255, 255, 255));
        this.mobColor = this.registerColor("Mob Color", new GSColor(255, 255, 0, 255));
        this.crystalColor = this.registerColor("Crystal Color", new GSColor(0, 255, 0, 255));
    }
    
    private void renderChamsPre(final GSColor color, final boolean isPlayer) {
        final String value = this.chamsType.getValue();
        switch (value) {
            case "Texture": {
                ChamsUtil.createChamsPre();
                break;
            }
            case "Color": {
                ChamsUtil.createColorPre(new GSColor(color, this.colorOpacity.getValue()), isPlayer);
                break;
            }
            case "WireFrame": {
                ChamsUtil.createWirePre(new GSColor(color, this.wireOpacity.getValue()), this.lineWidth.getValue(), isPlayer);
                break;
            }
        }
    }
    
    private void renderChamsPost(final boolean isPlayer) {
        final String value = this.chamsType.getValue();
        switch (value) {
            case "Texture": {
                ChamsUtil.createChamsPost();
                break;
            }
            case "Color": {
                ChamsUtil.createColorPost(isPlayer);
                break;
            }
            case "WireFrame": {
                ChamsUtil.createWirePost(isPlayer);
                break;
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
