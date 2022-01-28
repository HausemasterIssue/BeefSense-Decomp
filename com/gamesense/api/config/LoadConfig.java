



package com.gamesense.api.config;

import com.gamesense.client.module.*;
import java.util.*;
import java.nio.file.*;
import com.gamesense.client.*;
import com.gamesense.api.setting.*;
import java.io.*;
import com.gamesense.client.command.*;
import java.awt.*;
import com.gamesense.api.util.font.*;
import com.gamesense.api.util.player.friend.*;
import com.google.gson.*;
import com.gamesense.api.util.player.enemy.*;
import com.gamesense.client.clickgui.*;
import com.lukflug.panelstudio.*;
import com.gamesense.client.module.modules.misc.*;

public class LoadConfig
{
    String fileName;
    String moduleName;
    String mainName;
    String miscName;
    
    public LoadConfig() {
        this.fileName = "KiefSense/";
        this.moduleName = "Modules/";
        this.mainName = "Main/";
        this.miscName = "Misc/";
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadConfig() throws IOException {
        this.loadModules();
        this.loadEnabledModules();
        this.loadModuleKeybinds();
        this.loadDrawnModules();
        this.loadCommandPrefix();
        this.loadCustomFont();
        this.loadFriendsList();
        this.loadEnemiesList();
        this.loadClickGUIPositions();
        this.loadAutoGG();
        this.loadAutoReply();
        this.loadAutoRespawn();
    }
    
    public void loadModules() {
        final String moduleLocation = this.fileName + this.moduleName;
        for (final Module module : ModuleManager.getModules()) {
            try {
                this.loadModuleDirect(moduleLocation, module);
            }
            catch (IOException e) {
                System.out.println(module.getName());
                e.printStackTrace();
            }
        }
    }
    
    public void loadModuleDirect(final String moduleLocation, final Module module) throws IOException {
        if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json", new String[0]), new OpenOption[0]);
        final JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Module") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
        for (final Setting setting : GameSense.getInstance().settingsManager.getSettingsForMod(module)) {
            final JsonElement dataObject = settingObject.get(setting.getConfigName());
            try {
                if (dataObject == null || !dataObject.isJsonPrimitive()) {
                    continue;
                }
                switch (setting.getType()) {
                    case BOOLEAN: {
                        ((Setting.Boolean)setting).setValue(dataObject.getAsBoolean());
                        continue;
                    }
                    case INTEGER: {
                        ((Setting.Integer)setting).setValue(dataObject.getAsInt());
                        continue;
                    }
                    case DOUBLE: {
                        ((Setting.Double)setting).setValue(dataObject.getAsDouble());
                        continue;
                    }
                    case COLOR: {
                        ((Setting.ColorSetting)setting).fromInteger(dataObject.getAsInt());
                        continue;
                    }
                    case MODE: {
                        ((Setting.Mode)setting).setValue(dataObject.getAsString());
                        continue;
                    }
                }
            }
            catch (NumberFormatException e) {
                System.out.println(setting.getConfigName() + " " + module.getName());
                System.out.println(dataObject);
            }
        }
        inputStream.close();
    }
    
    public void loadEnabledModules() throws IOException {
        final String enabledLocation = this.fileName + this.mainName;
        if (!Files.exists(Paths.get(enabledLocation + "Toggle.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Toggle.json", new String[0]), new OpenOption[0]);
        final JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive() && dataObject.getAsBoolean()) {
                try {
                    module.enable();
                }
                catch (NullPointerException ex) {}
            }
        }
        inputStream.close();
    }
    
    public void loadModuleKeybinds() throws IOException {
        final String bindLocation = this.fileName + this.mainName;
        if (!Files.exists(Paths.get(bindLocation + "Bind.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(bindLocation + "Bind.json", new String[0]), new OpenOption[0]);
        final JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setBind(dataObject.getAsInt());
            }
        }
        inputStream.close();
    }
    
    public void loadDrawnModules() throws IOException {
        final String drawnLocation = this.fileName + this.mainName;
        if (!Files.exists(Paths.get(drawnLocation + "Drawn.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(drawnLocation + "Drawn.json", new String[0]), new OpenOption[0]);
        final JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setDrawn(dataObject.getAsBoolean());
            }
        }
        inputStream.close();
    }
    
    public void loadCommandPrefix() throws IOException {
        final String prefixLocation = this.fileName + this.mainName;
        if (!Files.exists(Paths.get(prefixLocation + "CommandPrefix.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(prefixLocation + "CommandPrefix.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Prefix") == null) {
            return;
        }
        final JsonElement prefixObject = mainObject.get("Prefix");
        if (prefixObject != null && prefixObject.isJsonPrimitive()) {
            Command.setCommandPrefix(prefixObject.getAsString());
        }
        inputStream.close();
    }
    
    public void loadCustomFont() throws IOException {
        final String fontLocation = this.fileName + this.miscName;
        if (!Files.exists(Paths.get(fontLocation + "CustomFont.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(fontLocation + "CustomFont.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Font Name") == null || mainObject.get("Font Size") == null) {
            return;
        }
        final JsonElement fontNameObject = mainObject.get("Font Name");
        String name = null;
        if (fontNameObject != null && fontNameObject.isJsonPrimitive()) {
            name = fontNameObject.getAsString();
        }
        final JsonElement fontSizeObject = mainObject.get("Font Size");
        int size = -1;
        if (fontSizeObject != null && fontSizeObject.isJsonPrimitive()) {
            size = fontSizeObject.getAsInt();
        }
        if (name != null && size != -1) {
            (GameSense.getInstance().cFontRenderer = new CFontRenderer(new Font(name, 0, size), true, true)).setFont(new Font(name, 0, size));
            GameSense.getInstance().cFontRenderer.setAntiAlias(true);
            GameSense.getInstance().cFontRenderer.setFractionalMetrics(true);
            GameSense.getInstance().cFontRenderer.setFontName(name);
            GameSense.getInstance().cFontRenderer.setFontSize(size);
        }
        inputStream.close();
    }
    
    public void loadFriendsList() throws IOException {
        final String friendLocation = this.fileName + this.miscName;
        if (!Files.exists(Paths.get(friendLocation + "Friends.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(friendLocation + "Friends.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Friends") == null) {
            return;
        }
        final JsonArray friendObject = mainObject.get("Friends").getAsJsonArray();
        friendObject.forEach(object -> Friends.addFriend(object.getAsString()));
        inputStream.close();
    }
    
    public void loadEnemiesList() throws IOException {
        final String enemyLocation = this.fileName + this.miscName;
        if (!Files.exists(Paths.get(enemyLocation + "Enemies.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(enemyLocation + "Enemies.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Enemies") == null) {
            return;
        }
        final JsonArray enemyObject = mainObject.get("Enemies").getAsJsonArray();
        enemyObject.forEach(object -> Enemies.addEnemy(object.getAsString()));
        inputStream.close();
    }
    
    public void loadClickGUIPositions() throws IOException {
        GameSense.getInstance().gameSenseGUI.gui.loadConfig(new GuiConfig(this.fileName + this.mainName));
    }
    
    public void loadAutoGG() throws IOException {
        final String fileLocation = this.fileName + this.miscName;
        if (!Files.exists(Paths.get(fileLocation + "AutoGG.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoGG.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Messages") == null) {
            return;
        }
        final JsonArray messageObject = mainObject.get("Messages").getAsJsonArray();
        messageObject.forEach(object -> AutoGG.addAutoGgMessage(object.getAsString()));
        inputStream.close();
    }
    
    public void loadAutoReply() throws IOException {
        final String fileLocation = this.fileName + this.miscName;
        if (!Files.exists(Paths.get(fileLocation + "AutoReply.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoReply.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("AutoReply") == null) {
            return;
        }
        final JsonObject arObject = mainObject.get("AutoReply").getAsJsonObject();
        final JsonElement dataObject = arObject.get("Message");
        if (dataObject != null && dataObject.isJsonPrimitive()) {
            AutoReply.setReply(dataObject.getAsString());
        }
        inputStream.close();
    }
    
    public void loadAutoRespawn() throws IOException {
        final String fileLocation = this.fileName + this.miscName;
        if (!Files.exists(Paths.get(fileLocation + "AutoRespawn.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoRespawn.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Message") == null) {
            return;
        }
        final JsonElement dataObject = mainObject.get("Message");
        if (dataObject != null && dataObject.isJsonPrimitive()) {
            AutoRespawn.setAutoRespawnMessage(dataObject.getAsString());
        }
        inputStream.close();
    }
}
