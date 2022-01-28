



package com.gamesense.api.config;

import java.nio.file.*;
import java.nio.file.attribute.*;
import com.gamesense.client.module.*;
import java.util.*;
import java.nio.charset.*;
import java.io.*;
import com.gamesense.client.*;
import com.gamesense.api.setting.*;
import com.gamesense.client.command.*;
import com.google.gson.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.api.util.player.enemy.*;
import com.gamesense.client.clickgui.*;
import com.lukflug.panelstudio.*;
import com.gamesense.client.module.modules.misc.*;

public class SaveConfig
{
    public static final String fileName = "KiefSense/";
    String moduleName;
    String mainName;
    String miscName;
    
    public SaveConfig() {
        this.moduleName = "Modules/";
        this.mainName = "Main/";
        this.miscName = "Misc/";
        try {
            this.saveConfig();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveConfig() throws IOException {
        if (!Files.exists(Paths.get("KiefSense/", new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("KiefSense/", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
        if (!Files.exists(Paths.get("KiefSense/" + this.moduleName, new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("KiefSense/" + this.moduleName, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
        if (!Files.exists(Paths.get("KiefSense/" + this.mainName, new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("KiefSense/" + this.mainName, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
        if (!Files.exists(Paths.get("KiefSense/" + this.miscName, new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("KiefSense/" + this.miscName, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
    }
    
    public void registerFiles(final String location, final String name) throws IOException {
        if (!Files.exists(Paths.get("KiefSense/" + location + name + ".json", new String[0]), new LinkOption[0])) {
            Files.createFile(Paths.get("KiefSense/" + location + name + ".json", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
        else {
            final File file = new File("KiefSense/" + location + name + ".json");
            file.delete();
            Files.createFile(Paths.get("KiefSense/" + location + name + ".json", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
    }
    
    public void saveModules() {
        for (final Module module : ModuleManager.getModules()) {
            try {
                this.saveModuleDirect(module);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void saveModuleDirect(final Module module) throws IOException {
        this.registerFiles(this.moduleName, module.getName());
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.moduleName + module.getName() + ".json"), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject settingObject = new JsonObject();
        moduleObject.add("Module", (JsonElement)new JsonPrimitive(module.getName()));
        for (final Setting setting : GameSense.getInstance().settingsManager.getSettingsForMod(module)) {
            switch (setting.getType()) {
                case BOOLEAN: {
                    settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(((Setting.Boolean)setting).getValue())));
                    continue;
                }
                case INTEGER: {
                    settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Number)((Setting.Integer)setting).getValue()));
                    continue;
                }
                case DOUBLE: {
                    settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Number)((Setting.Double)setting).getValue()));
                    continue;
                }
                case COLOR: {
                    settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Number)((Setting.ColorSetting)setting).toInteger()));
                    continue;
                }
                case MODE: {
                    settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive(((Setting.Mode)setting).getValue()));
                    continue;
                }
            }
        }
        moduleObject.add("Settings", (JsonElement)settingObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveEnabledModules() throws IOException {
        this.registerFiles(this.mainName, "Toggle");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.mainName + "Toggle.json"), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject enabledObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            enabledObject.add(module.getName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(module.isEnabled())));
        }
        moduleObject.add("Modules", (JsonElement)enabledObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveModuleKeybinds() throws IOException {
        this.registerFiles(this.mainName, "Bind");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.mainName + "Bind.json"), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject bindObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            bindObject.add(module.getName(), (JsonElement)new JsonPrimitive((Number)module.getBind()));
        }
        moduleObject.add("Modules", (JsonElement)bindObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveDrawnModules() throws IOException {
        this.registerFiles(this.mainName, "Drawn");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.mainName + "Drawn.json"), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject drawnObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            drawnObject.add(module.getName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(module.isDrawn())));
        }
        moduleObject.add("Modules", (JsonElement)drawnObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveCommandPrefix() throws IOException {
        this.registerFiles(this.mainName, "CommandPrefix");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.mainName + "CommandPrefix.json"), StandardCharsets.UTF_8);
        final JsonObject prefixObject = new JsonObject();
        prefixObject.add("Prefix", (JsonElement)new JsonPrimitive(Command.getCommandPrefix()));
        final String jsonString = gson.toJson(new JsonParser().parse(prefixObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveCustomFont() throws IOException {
        this.registerFiles(this.miscName, "CustomFont");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.miscName + "CustomFont.json"), StandardCharsets.UTF_8);
        final JsonObject fontObject = new JsonObject();
        fontObject.add("Font Name", (JsonElement)new JsonPrimitive(GameSense.getInstance().cFontRenderer.getFontName()));
        fontObject.add("Font Size", (JsonElement)new JsonPrimitive((Number)GameSense.getInstance().cFontRenderer.getFontSize()));
        final String jsonString = gson.toJson(new JsonParser().parse(fontObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveFriendsList() throws IOException {
        this.registerFiles(this.miscName, "Friends");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.miscName + "Friends.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray friendArray = new JsonArray();
        for (final Friend friend : Friends.getFriends()) {
            friendArray.add(friend.getName());
        }
        mainObject.add("Friends", (JsonElement)friendArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveEnemiesList() throws IOException {
        this.registerFiles(this.miscName, "Enemies");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.miscName + "Enemies.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray enemyArray = new JsonArray();
        for (final Enemy enemy : Enemies.getEnemies()) {
            enemyArray.add(enemy.getName());
        }
        mainObject.add("Enemies", (JsonElement)enemyArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveClickGUIPositions() throws IOException {
        this.registerFiles(this.mainName, "ClickGUI");
        GameSense.getInstance().gameSenseGUI.gui.saveConfig(new GuiConfig("KiefSense/" + this.mainName));
    }
    
    public void saveAutoGG() throws IOException {
        this.registerFiles(this.miscName, "AutoGG");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.miscName + "AutoGG.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray messageArray = new JsonArray();
        for (final String autoGG : AutoGG.getAutoGgMessages()) {
            messageArray.add(autoGG);
        }
        mainObject.add("Messages", (JsonElement)messageArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveAutoReply() throws IOException {
        this.registerFiles(this.miscName, "AutoReply");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.miscName + "AutoReply.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonObject messageObject = new JsonObject();
        messageObject.add("Message", (JsonElement)new JsonPrimitive(AutoReply.getReply()));
        mainObject.add("AutoReply", (JsonElement)messageObject);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    public void saveAutoRespawn() throws IOException {
        this.registerFiles(this.miscName, "AutoRespawn");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("KiefSense/" + this.miscName + "AutoRespawn.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        mainObject.add("Message", (JsonElement)new JsonPrimitive(AutoRespawn.getAutoRespawnMessages()));
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
}
