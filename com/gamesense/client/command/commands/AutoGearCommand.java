



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.api.util.misc.*;
import com.google.gson.*;
import com.gamesense.client.module.modules.combat.*;
import net.minecraft.item.*;
import java.util.*;
import java.io.*;

public class AutoGearCommand extends Command
{
    private static final String pathSave = "KiefSense/Misc/AutoGear.json";
    private static final HashMap<String, String> errorMessage;
    
    public AutoGearCommand() {
        super("AutoGear");
        this.setCommandSyntax(Command.getCommandPrefix() + "gear set/save/del/list [name]");
        this.setCommandAlias(new String[] { "gear", "gr", "kit" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String lowerCase = message[0].toLowerCase();
        switch (lowerCase) {
            case "list": {
                if (message.length == 1) {
                    this.listMessage();
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            case "set": {
                if (message.length == 2) {
                    this.set(message[1]);
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            case "save":
            case "add":
            case "create": {
                if (message.length == 2) {
                    this.save(message[1]);
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            case "del": {
                if (message.length == 2) {
                    this.delete(message[1]);
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            default: {
                MessageBus.sendCommandMessage("AutoGear message is: gear set/save/del/list [name]", true);
                break;
            }
        }
    }
    
    private void listMessage() {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("KiefSense/Misc/AutoGear.json")).getAsJsonObject();
            for (int lenghtJson = completeJson.entrySet().size(), i = 0; i < lenghtJson; ++i) {
                final String item = new JsonParser().parse((Reader)new FileReader("KiefSense/Misc/AutoGear.json")).getAsJsonObject().entrySet().toArray()[i].toString().split("=")[0];
                if (!item.equals("pointer")) {
                    PistonCrystal.printChat("Kit avaible: " + item, false);
                }
            }
        }
        catch (IOException e) {
            errorMessage("NoEx");
        }
    }
    
    private void delete(final String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("KiefSense/Misc/AutoGear.json")).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                completeJson.remove(name);
                if (completeJson.get("pointer").getAsString().equals(name)) {
                    completeJson.addProperty("pointer", "none");
                }
                this.saveFile(completeJson, name, "deleted");
            }
            else {
                errorMessage("NoEx");
            }
        }
        catch (IOException e) {
            errorMessage("NoEx");
        }
    }
    
    private void set(final String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("KiefSense/Misc/AutoGear.json")).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                completeJson.addProperty("pointer", name);
                this.saveFile(completeJson, name, "selected");
            }
            else {
                errorMessage("NoEx");
            }
        }
        catch (IOException e) {
            errorMessage("NoEx");
        }
    }
    
    private void save(final String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("KiefSense/Misc/AutoGear.json")).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                errorMessage("Exist");
                return;
            }
        }
        catch (IOException e) {
            completeJson.addProperty("pointer", "none");
        }
        final StringBuilder jsonInventory = new StringBuilder();
        for (final ItemStack item : AutoGearCommand.mc.player.inventory.mainInventory) {
            jsonInventory.append(item.getItem().getRegistryName().toString() + item.getMetadata()).append(" ");
        }
        completeJson.addProperty(name, jsonInventory.toString());
        this.saveFile(completeJson, name, "saved");
    }
    
    private void saveFile(final JsonObject completeJson, final String name, final String operation) {
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter("KiefSense/Misc/AutoGear.json"));
            bw.write(completeJson.toString());
            bw.close();
            PistonCrystal.printChat("Kit " + name + " " + operation, false);
        }
        catch (IOException e) {
            errorMessage("Saving");
        }
    }
    
    private static void errorMessage(final String e) {
        PistonCrystal.printChat("Error: " + AutoGearCommand.errorMessage.get(e), true);
    }
    
    public static String getCurrentSet() {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("KiefSense/Misc/AutoGear.json")).getAsJsonObject();
            if (!completeJson.get("pointer").getAsString().equals("none")) {
                return completeJson.get("pointer").getAsString();
            }
        }
        catch (IOException ex) {}
        errorMessage("NoEx");
        return "";
    }
    
    public static String getInventoryKit(final String kit) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("KiefSense/Misc/AutoGear.json")).getAsJsonObject();
            return completeJson.get(kit).getAsString();
        }
        catch (IOException ex) {
            errorMessage("NoEx");
            return "";
        }
    }
    
    static {
        errorMessage = new HashMap<String, String>() {
            {
                this.put("NoPar", "Not enough parameters");
                this.put("Exist", "This kit arleady exist");
                this.put("Saving", "Error saving the file");
                this.put("NoEx", "Kit not found");
            }
        };
    }
}
