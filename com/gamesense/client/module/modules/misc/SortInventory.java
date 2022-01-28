



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.client.module.modules.combat.*;
import com.gamesense.client.command.commands.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import java.util.*;

public class SortInventory extends Module
{
    Setting.Boolean chatMsg;
    Setting.Boolean debugMode;
    Setting.Boolean confirmSort;
    Setting.Boolean instaSort;
    Setting.Boolean closeAfter;
    Setting.Integer tickDelay;
    private HashMap<Integer, String> planInventory;
    private HashMap<String, Integer> nItems;
    private ArrayList<Integer> sortItems;
    private int delayTimeTicks;
    private int stepNow;
    private boolean openedBefore;
    private boolean finishSort;
    private boolean doneBefore;
    
    public SortInventory() {
        super("SortInventory", Module.Category.Misc);
        this.planInventory = new HashMap<Integer, String>();
        this.nItems = new HashMap<String, Integer>();
        this.sortItems = new ArrayList<Integer>();
    }
    
    public void setup() {
        this.tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
        this.confirmSort = this.registerBoolean("Confirm Sort", true);
        this.chatMsg = this.registerBoolean("Chat Msg", true);
        this.instaSort = this.registerBoolean("Insta Sort", false);
        this.closeAfter = this.registerBoolean("Close After", false);
        this.debugMode = this.registerBoolean("Debug Mode", false);
    }
    
    public void onEnable() {
        if (this.chatMsg.getValue()) {
            PistonCrystal.printChat("AutoSort Turned On!", Boolean.valueOf(false));
        }
        final String curConfigName = AutoGearCommand.getCurrentSet();
        if (curConfigName.equals("")) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            PistonCrystal.printChat("Config " + curConfigName + " actived", Boolean.valueOf(false));
        }
        final String inventoryConfig = AutoGearCommand.getInventoryKit(curConfigName);
        if (inventoryConfig.equals("")) {
            this.disable();
            return;
        }
        final String[] inventoryDivided = inventoryConfig.split(" ");
        this.planInventory = new HashMap<Integer, String>();
        this.nItems = new HashMap<String, Integer>();
        for (int i = 0; i < inventoryDivided.length; ++i) {
            if (!inventoryDivided[i].contains("air")) {
                this.planInventory.put(i, inventoryDivided[i]);
                if (this.nItems.containsKey(inventoryDivided[i])) {
                    this.nItems.put(inventoryDivided[i], this.nItems.get(inventoryDivided[i]) + 1);
                }
                else {
                    this.nItems.put(inventoryDivided[i], 1);
                }
            }
        }
        this.delayTimeTicks = 0;
        final boolean b = false;
        this.doneBefore = b;
        this.openedBefore = b;
        if (this.instaSort.getValue()) {
            SortInventory.mc.displayGuiScreen((GuiScreen)new GuiInventory((EntityPlayer)SortInventory.mc.player));
        }
    }
    
    public void onDisable() {
        if (this.chatMsg.getValue() && this.planInventory.size() > 0) {
            PistonCrystal.printChat("AutoSort Turned Off!", Boolean.valueOf(true));
        }
    }
    
    public void onUpdate() {
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (this.planInventory.size() == 0) {
            this.disable();
        }
        if (SortInventory.mc.currentScreen instanceof GuiInventory) {
            this.sortInventoryAlgo();
        }
        else {
            this.openedBefore = false;
        }
    }
    
    private void sortInventoryAlgo() {
        if (!this.openedBefore) {
            if (this.chatMsg.getValue() && !this.doneBefore) {
                PistonCrystal.printChat("Start sorting inventory...", Boolean.valueOf(false));
            }
            this.sortItems = this.getInventorySort();
            if (this.sortItems.size() == 0 && !this.doneBefore) {
                this.finishSort = false;
                if (this.chatMsg.getValue()) {
                    PistonCrystal.printChat("Inventory arleady sorted...", Boolean.valueOf(true));
                }
                if (this.instaSort.getValue() || this.closeAfter.getValue()) {
                    SortInventory.mc.player.closeScreen();
                    if (this.instaSort.getValue()) {
                        this.disable();
                    }
                }
            }
            else {
                this.finishSort = true;
                this.stepNow = 0;
            }
            this.openedBefore = true;
        }
        else if (this.finishSort) {
            if (this.sortItems.size() != 0) {
                final int slotChange = this.sortItems.get(this.stepNow++);
                SortInventory.mc.playerController.windowClick(0, (slotChange < 9) ? (slotChange + 36) : slotChange, 0, ClickType.PICKUP, (EntityPlayer)SortInventory.mc.player);
            }
            if (this.stepNow == this.sortItems.size()) {
                if (this.confirmSort.getValue() && !this.doneBefore) {
                    this.openedBefore = false;
                    this.finishSort = false;
                    this.doneBefore = true;
                    this.checkLastItem();
                    return;
                }
                this.finishSort = false;
                if (this.chatMsg.getValue()) {
                    PistonCrystal.printChat("Inventory sorted", Boolean.valueOf(false));
                }
                this.checkLastItem();
                this.doneBefore = false;
                if (this.instaSort.getValue() || this.closeAfter.getValue()) {
                    SortInventory.mc.player.closeScreen();
                    if (this.instaSort.getValue()) {
                        this.disable();
                    }
                }
            }
        }
    }
    
    private void checkLastItem() {
        if (this.sortItems.size() != 0) {
            final int slotChange = this.sortItems.get(this.sortItems.size() - 1);
            if (SortInventory.mc.player.inventory.getStackInSlot(slotChange).func_190926_b()) {
                SortInventory.mc.playerController.windowClick(0, (slotChange < 9) ? (slotChange + 36) : slotChange, 0, ClickType.PICKUP, (EntityPlayer)SortInventory.mc.player);
            }
        }
    }
    
    private ArrayList<Integer> getInventorySort() {
        final ArrayList<Integer> planMove = new ArrayList<Integer>();
        final ArrayList<String> copyInventory = this.getInventoryCopy();
        final HashMap<Integer, String> planInventoryCopy = (HashMap<Integer, String>)this.planInventory.clone();
        final HashMap<String, Integer> nItemsCopy = (HashMap<String, Integer>)this.nItems.clone();
        final ArrayList<Integer> ignoreValues = new ArrayList<Integer>();
        for (int i = 0; i < this.planInventory.size(); ++i) {
            final int value = (int)this.planInventory.keySet().toArray()[i];
            if (copyInventory.get(value).equals(planInventoryCopy.get(value))) {
                ignoreValues.add(value);
                nItemsCopy.put(planInventoryCopy.get(value), nItemsCopy.get(planInventoryCopy.get(value)) - 1);
                if (nItemsCopy.get(planInventoryCopy.get(value)) == 0) {
                    nItemsCopy.remove(planInventoryCopy.get(value));
                }
                planInventoryCopy.remove(value);
            }
        }
        String pickedItem = null;
        for (int j = 0; j < copyInventory.size(); ++j) {
            if (!ignoreValues.contains(j)) {
                final String itemCheck = copyInventory.get(j);
                final Optional<Map.Entry<Integer, String>> momentAim = planInventoryCopy.entrySet().stream().filter(x -> x.getValue().equals(itemCheck)).findFirst();
                if (momentAim.isPresent()) {
                    if (pickedItem == null) {
                        planMove.add(j);
                    }
                    final int aimKey = momentAim.get().getKey();
                    planMove.add(aimKey);
                    if (pickedItem == null || !pickedItem.equals(itemCheck)) {
                        ignoreValues.add(aimKey);
                    }
                    nItemsCopy.put(itemCheck, nItemsCopy.get(itemCheck) - 1);
                    if (nItemsCopy.get(itemCheck) == 0) {
                        nItemsCopy.remove(itemCheck);
                    }
                    copyInventory.set(j, copyInventory.get(aimKey));
                    copyInventory.set(aimKey, itemCheck);
                    if (!copyInventory.get(aimKey).equals("minecraft:air0")) {
                        if (j >= copyInventory.size()) {
                            continue;
                        }
                        pickedItem = copyInventory.get(j);
                        --j;
                    }
                    else {
                        pickedItem = null;
                    }
                    planInventoryCopy.remove(aimKey);
                }
                else if (pickedItem != null) {
                    planMove.add(j);
                    copyInventory.set(j, pickedItem);
                    pickedItem = null;
                }
            }
        }
        if (planMove.size() != 0 && planMove.get(planMove.size() - 1).equals(planMove.get(planMove.size() - 2))) {
            planMove.remove(planMove.size() - 1);
        }
        if (this.debugMode.getValue()) {
            for (final int valuePath : planMove) {
                PistonCrystal.printChat(Integer.toString(valuePath), Boolean.valueOf(false));
            }
        }
        return planMove;
    }
    
    private ArrayList<String> getInventoryCopy() {
        final ArrayList<String> output = new ArrayList<String>();
        for (final ItemStack i : SortInventory.mc.player.inventory.mainInventory) {
            output.add(Objects.requireNonNull(i.getItem().getRegistryName()).toString() + i.getMetadata());
        }
        return output;
    }
}
