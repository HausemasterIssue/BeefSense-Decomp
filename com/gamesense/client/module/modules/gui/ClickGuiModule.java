



package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.*;
import com.gamesense.api.util.render.*;
import java.util.*;
import com.gamesense.client.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.client.module.*;
import com.gamesense.api.util.misc.*;

public class ClickGuiModule extends Module
{
    public ClickGuiModule INSTANCE;
    public static Setting.Integer scrollSpeed;
    public static Setting.Integer opacity;
    public static Setting.ColorSetting enabledColor;
    public static Setting.ColorSetting outlineColor;
    public static Setting.ColorSetting backgroundColor;
    public static Setting.ColorSetting settingBackgroundColor;
    public static Setting.ColorSetting fontColor;
    public static Setting.Integer animationSpeed;
    public static Setting.Mode scrolling;
    public static Setting.Boolean showHUD;
    public static Setting.Mode theme;
    
    public ClickGuiModule() {
        super("ClickGUI", Module.Category.GUI);
        this.setBind(24);
        this.setDrawn(false);
        this.INSTANCE = this;
    }
    
    public void setup() {
        final ArrayList<String> models = new ArrayList<String>();
        models.add("Screen");
        models.add("Container");
        final ArrayList<String> themes = new ArrayList<String>();
        themes.add("2.2");
        themes.add("2.1.2");
        themes.add("2.0");
        ClickGuiModule.opacity = this.registerInteger("Opacity", 150, 50, 255);
        ClickGuiModule.scrollSpeed = this.registerInteger("Scroll Speed", 10, 1, 20);
        ClickGuiModule.outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
        ClickGuiModule.enabledColor = this.registerColor("Enabled", new GSColor(255, 0, 0, 255));
        ClickGuiModule.backgroundColor = this.registerColor("Background", new GSColor(0, 0, 0, 255));
        ClickGuiModule.settingBackgroundColor = this.registerColor("Setting", new GSColor(30, 30, 30, 255));
        ClickGuiModule.fontColor = this.registerColor("Font", new GSColor(255, 255, 255, 255));
        ClickGuiModule.animationSpeed = this.registerInteger("Animation Speed", 200, 0, 1000);
        ClickGuiModule.scrolling = this.registerMode("Scrolling", (List)models, "Screen");
        ClickGuiModule.showHUD = this.registerBoolean("Show HUD Panels", false);
        ClickGuiModule.theme = this.registerMode("Skin", (List)themes, "2.2");
    }
    
    public void onEnable() {
        GameSense.getInstance().gameSenseGUI.enterGUI();
        final Announcer announcer = (Announcer)ModuleManager.getModule((Class)Announcer.class);
        if (announcer.clickGui.getValue() && announcer.isEnabled() && ClickGuiModule.mc.player != null) {
            if (announcer.clientSide.getValue()) {
                MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
            }
            else {
                MessageBus.sendServerMessage(Announcer.guiMessage);
            }
        }
        this.disable();
    }
}
