



package com.gamesense.client.module;

import com.lukflug.panelstudio.settings.*;
import net.minecraft.client.*;
import com.gamesense.api.event.events.*;
import com.gamesense.api.setting.*;
import com.gamesense.client.*;
import java.util.*;
import com.gamesense.api.util.render.*;
import org.lwjgl.input.*;

public abstract class Module implements Toggleable, KeybindSetting
{
    protected static final Minecraft mc;
    String name;
    Category category;
    int bind;
    boolean enabled;
    boolean drawn;
    
    public Module(final String name, final Category category) {
        this.name = name;
        this.category = category;
        this.bind = 0;
        this.enabled = false;
        this.drawn = true;
        this.setup();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public void setCategory(final Category category) {
        this.category = category;
    }
    
    public int getBind() {
        return this.bind;
    }
    
    public void setBind(final int bind) {
        this.bind = bind;
    }
    
    protected void onEnable() {
    }
    
    protected void onDisable() {
    }
    
    public void onUpdate() {
    }
    
    public void onRender() {
    }
    
    public void onWorldRender(final RenderEvent event) {
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void enable() {
        this.setEnabled(true);
        this.onEnable();
    }
    
    public void disable() {
        this.setEnabled(false);
        this.onDisable();
    }
    
    @Override
    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        }
        else if (!this.isEnabled()) {
            this.enable();
        }
    }
    
    public String getHudInfo() {
        return "";
    }
    
    public void setup() {
    }
    
    public boolean isDrawn() {
        return this.drawn;
    }
    
    public void setDrawn(final boolean drawn) {
        this.drawn = drawn;
    }
    
    protected Setting.Integer registerInteger(final String name, final int value, final int min, final int max) {
        final Setting.Integer setting = new Setting.Integer(name, this, this.getCategory(), value, min, max);
        GameSense.getInstance().settingsManager.addSetting((Setting)setting);
        return setting;
    }
    
    protected Setting.Double registerDouble(final String name, final double value, final double min, final double max) {
        final Setting.Double setting = new Setting.Double(name, this, this.getCategory(), value, min, max);
        GameSense.getInstance().settingsManager.addSetting((Setting)setting);
        return setting;
    }
    
    protected Setting.Boolean registerBoolean(final String name, final boolean value) {
        final Setting.Boolean setting = new Setting.Boolean(name, this, this.getCategory(), value);
        GameSense.getInstance().settingsManager.addSetting((Setting)setting);
        return setting;
    }
    
    protected Setting.Mode registerMode(final String name, final List<String> modes, final String value) {
        final Setting.Mode setting = new Setting.Mode(name, this, this.getCategory(), (List)modes, value);
        GameSense.getInstance().settingsManager.addSetting((Setting)setting);
        return setting;
    }
    
    protected Setting.ColorSetting registerColor(final String name, final GSColor color) {
        final Setting.ColorSetting setting = new Setting.ColorSetting(name, this, this.getCategory(), false, color);
        GameSense.getInstance().settingsManager.addSetting((Setting)setting);
        return setting;
    }
    
    protected Setting.ColorSetting registerColor(final String name) {
        return this.registerColor(name, new GSColor(90, 145, 240));
    }
    
    @Override
    public boolean isOn() {
        return this.enabled;
    }
    
    @Override
    public int getKey() {
        return this.bind;
    }
    
    @Override
    public void setKey(final int key) {
        this.bind = key;
    }
    
    @Override
    public String getKeyName() {
        return Keyboard.getKeyName(this.bind);
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
    
    public enum Category
    {
        Combat, 
        Exploits, 
        Movement, 
        Misc, 
        Render, 
        HUD, 
        GUI;
    }
}
