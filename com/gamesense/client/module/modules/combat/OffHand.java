



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.block.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import java.util.function.*;
import com.gamesense.api.util.player.*;
import net.minecraft.init.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.entity.item.*;
import com.gamesense.api.util.combat.*;
import net.minecraft.item.*;
import com.mojang.realmsclient.gui.*;

public class OffHand extends Module
{
    public static Setting.Mode defaultItem;
    public static Setting.Mode nonDefaultItem;
    public static Setting.Mode noPlayerItem;
    Setting.Mode potionChoose;
    Setting.Integer healthSwitch;
    Setting.Integer tickDelay;
    Setting.Integer fallDistance;
    Setting.Integer maxSwitchPerSecond;
    Setting.Double biasDamage;
    Setting.Double playerDistance;
    Setting.Boolean pickObby;
    Setting.Boolean leftGap;
    Setting.Boolean shiftPot;
    Setting.Boolean swordCheck;
    Setting.Boolean fallDistanceBol;
    Setting.Boolean antiWeakness;
    Setting.Boolean chatMsg;
    Setting.Boolean noHotBar;
    Setting.Boolean crystObby;
    Setting.Boolean pickObbyShift;
    Setting.Boolean onlyHotBar;
    Setting.Boolean crystalCheck;
    Setting.Boolean hotBarTotem;
    int prevSlot;
    int tickWaited;
    int totems;
    boolean returnBack;
    boolean stepChanging;
    private static boolean activeT;
    private static int forceObby;
    private ArrayList<Long> switchDone;
    private final ArrayList<Item> ignoreNoSword;
    Map<String, Item> allowedItemsItem;
    Map<String, Block> allowedItemsBlock;
    
    public static boolean isActive() {
        return OffHand.activeT;
    }
    
    public static void requestObsidian() {
        ++OffHand.forceObby;
    }
    
    public static void removeObsidian() {
        if (OffHand.forceObby != 0) {
            --OffHand.forceObby;
        }
    }
    
    public OffHand() {
        super("Offhand", Module.Category.Combat);
        this.switchDone = new ArrayList<Long>();
        this.ignoreNoSword = new ArrayList<Item>() {
            {
                this.add(Items.GOLDEN_APPLE);
                this.add(Items.EXPERIENCE_BOTTLE);
                this.add((Item)Items.BOW);
                this.add((Item)Items.POTIONITEM);
            }
        };
        this.allowedItemsItem = new HashMap<String, Item>() {
            {
                this.put("Totem", Items.field_190929_cY);
                this.put("Crystal", Items.END_CRYSTAL);
                this.put("Gapple", Items.GOLDEN_APPLE);
                this.put("Pot", (Item)Items.POTIONITEM);
                this.put("Exp", Items.EXPERIENCE_BOTTLE);
            }
        };
        this.allowedItemsBlock = new HashMap<String, Block>() {
            {
                this.put("Plates", Blocks.WOODEN_PRESSURE_PLATE);
                this.put("Obby", Blocks.OBSIDIAN);
            }
        };
    }
    
    public void setup() {
        OffHand.activeT = false;
        final String[] allowedItems = { "Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot", "Exp" };
        final String[] allowedPotions = { "first", "strength", "swiftness" };
        final ArrayList<String> defaultItems = new ArrayList<String>(Arrays.asList(allowedItems));
        final ArrayList<String> defaultPotions = new ArrayList<String>(Arrays.asList(allowedPotions));
        OffHand.defaultItem = this.registerMode("Default", (List)defaultItems, "Totem");
        OffHand.nonDefaultItem = this.registerMode("Non Default", (List)defaultItems, "Crystal");
        OffHand.noPlayerItem = this.registerMode("No Player", (List)defaultItems, "Gapple");
        this.potionChoose = this.registerMode("Potion", (List)defaultPotions, "first");
        this.healthSwitch = this.registerInteger("Health Switch", 14, 0, 36);
        this.tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
        this.fallDistance = this.registerInteger("Fall Distance", 12, 0, 30);
        this.maxSwitchPerSecond = this.registerInteger("Max Switch", 6, 2, 10);
        this.biasDamage = this.registerDouble("Bias Damage", 1.0, 0.0, 3.0);
        this.playerDistance = this.registerDouble("Player Distance", 0.0, 0.0, 30.0);
        this.pickObby = this.registerBoolean("Pick Obby", false);
        this.pickObbyShift = this.registerBoolean("Pick Obby On Shift", false);
        this.crystObby = this.registerBoolean("Cryst Shift Obby", false);
        this.leftGap = this.registerBoolean("Left Click Gap", false);
        this.shiftPot = this.registerBoolean("Shift Pot", false);
        this.swordCheck = this.registerBoolean("Only Sword", true);
        this.fallDistanceBol = this.registerBoolean("Fall Distance", true);
        this.crystalCheck = this.registerBoolean("Crystal Check", false);
        this.noHotBar = this.registerBoolean("No HotBar", false);
        this.onlyHotBar = this.registerBoolean("Only HotBar", false);
        this.antiWeakness = this.registerBoolean("AntiWeakness", false);
        this.hotBarTotem = this.registerBoolean("HotBar Totem", false);
        this.chatMsg = this.registerBoolean("Chat Msg", true);
    }
    
    public void onEnable() {
        OffHand.activeT = true;
        OffHand.forceObby = 0;
        this.returnBack = false;
        if (this.chatMsg.getValue()) {
            PistonCrystal.printChat("OffHand enabled", false);
        }
    }
    
    public void onDisable() {
        OffHand.activeT = false;
        OffHand.forceObby = 0;
        if (this.chatMsg.getValue()) {
            PistonCrystal.printChat("OffHand disabled", true);
        }
    }
    
    public void onUpdate() {
        if (OffHand.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.stepChanging) {
            if (this.tickWaited++ < this.tickDelay.getValue()) {
                return;
            }
            this.tickWaited = 0;
            this.stepChanging = false;
            OffHand.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)OffHand.mc.player);
            this.switchDone.add(System.currentTimeMillis());
        }
        this.totems = OffHand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum();
        if (this.returnBack) {
            if (this.tickWaited++ < this.tickDelay.getValue()) {
                return;
            }
            this.changeBack();
        }
        final String itemCheck = this.getItem();
        if (this.offHandSame(itemCheck)) {
            boolean done = false;
            if (this.hotBarTotem.getValue() && itemCheck.equals("Totem")) {
                done = this.switchItemTotemHot();
            }
            if (!done) {
                this.switchItemNormal(itemCheck);
            }
        }
    }
    
    private void changeBack() {
        if (!OffHand.mc.player.inventory.getStackInSlot(this.prevSlot).func_190926_b() || this.prevSlot == -1) {
            this.prevSlot = this.findEmptySlot();
        }
        if (this.prevSlot != -1) {
            OffHand.mc.playerController.windowClick(0, (this.prevSlot < 9) ? (this.prevSlot + 36) : this.prevSlot, 0, ClickType.PICKUP, (EntityPlayer)OffHand.mc.player);
        }
        else {
            PistonCrystal.printChat("Your inventory is full. the item that was on your offhand is going to be dropped. Open your inventory and choose where to put it", true);
        }
        this.returnBack = false;
        this.tickWaited = 0;
    }
    
    private boolean switchItemTotemHot() {
        final int slot = InventoryUtil.findTotemSlot(0, 8);
        if (slot != -1) {
            if (OffHand.mc.player.inventory.currentItem != slot) {
                OffHand.mc.player.inventory.currentItem = slot;
            }
            return true;
        }
        return false;
    }
    
    private void switchItemNormal(final String itemCheck) {
        final int t = this.getInventorySlot(itemCheck);
        if (t == -1) {
            return;
        }
        if (!itemCheck.equals("Totem") && this.canSwitch()) {
            return;
        }
        this.toOffHand(t);
    }
    
    private String getItem() {
        String itemCheck = "";
        boolean normalOffHand = true;
        if ((this.fallDistanceBol.getValue() && OffHand.mc.player.fallDistance >= this.fallDistance.getValue() && OffHand.mc.player.prevPosY != OffHand.mc.player.posY && !OffHand.mc.player.isElytraFlying()) || (this.crystalCheck.getValue() && this.crystalDamage())) {
            normalOffHand = false;
            itemCheck = "Totem";
        }
        final Item mainHandItem = OffHand.mc.player.getHeldItemMainhand().getItem();
        if (OffHand.forceObby > 0 || (normalOffHand && ((this.crystObby.getValue() && OffHand.mc.gameSettings.keyBindSneak.isKeyDown() && mainHandItem == Items.END_CRYSTAL) || (this.pickObby.getValue() && mainHandItem == Items.DIAMOND_PICKAXE && (!this.pickObbyShift.getValue() || OffHand.mc.gameSettings.keyBindSneak.isKeyDown()))))) {
            itemCheck = "Obby";
            normalOffHand = false;
        }
        if (normalOffHand && OffHand.mc.gameSettings.keyBindUseItem.isKeyDown() && (!this.swordCheck.getValue() || mainHandItem == Items.DIAMOND_SWORD)) {
            if (OffHand.mc.gameSettings.keyBindSneak.isKeyDown()) {
                if (this.shiftPot.getValue()) {
                    itemCheck = "Pot";
                    normalOffHand = false;
                }
            }
            else if (this.leftGap.getValue() && !this.ignoreNoSword.contains(mainHandItem)) {
                itemCheck = "Gapple";
                normalOffHand = false;
            }
        }
        if (normalOffHand && this.antiWeakness.getValue() && OffHand.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            normalOffHand = false;
            itemCheck = "Crystal";
        }
        if (normalOffHand && !this.nearPlayer()) {
            normalOffHand = false;
            itemCheck = OffHand.noPlayerItem.getValue();
        }
        itemCheck = this.getItemToCheck(itemCheck);
        return itemCheck;
    }
    
    private boolean canSwitch() {
        final boolean result = false;
        final long now = System.currentTimeMillis();
        for (int i = 0; i < this.switchDone.size() && now - this.switchDone.get(i) > 1000L; ++i) {
            this.switchDone.remove(i);
        }
        if (this.switchDone.size() / 2 >= this.maxSwitchPerSecond.getValue()) {
            return true;
        }
        this.switchDone.add(now);
        return false;
    }
    
    private boolean nearPlayer() {
        if ((int)this.playerDistance.getValue() == 0) {
            return true;
        }
        for (final EntityPlayer pl : OffHand.mc.world.playerEntities) {
            if (pl != OffHand.mc.player && OffHand.mc.player.getDistanceToEntity((Entity)pl) < this.playerDistance.getValue()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean crystalDamage() {
        double ris2 = 0.0;
        for (final Entity t : OffHand.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && OffHand.mc.player.getDistanceToEntity(t) <= 12.0f && (ris2 = DamageUtil.calculateDamage(t.posX, t.posY, t.posZ, (Entity)OffHand.mc.player) * this.biasDamage.getValue()) >= OffHand.mc.player.getHealth()) {
                return true;
            }
        }
        return false;
    }
    
    private int findEmptySlot() {
        for (int i = 35; i > -1; --i) {
            if (OffHand.mc.player.inventory.getStackInSlot(i).func_190926_b()) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean offHandSame(final String itemCheck) {
        final Item offHandItem = OffHand.mc.player.getHeldItemOffhand().getItem();
        if (this.allowedItemsBlock.containsKey(itemCheck)) {
            final Block item = this.allowedItemsBlock.get(itemCheck);
            return !(offHandItem instanceof ItemBlock) || ((ItemBlock)offHandItem).getBlock() != item;
        }
        final Item item2 = this.allowedItemsItem.get(itemCheck);
        return item2 != offHandItem;
    }
    
    private String getItemToCheck(final String str) {
        return (OffHand.mc.player.getHealth() + OffHand.mc.player.getAbsorptionAmount() > this.healthSwitch.getValue()) ? (str.equals("") ? OffHand.nonDefaultItem.getValue() : str) : OffHand.defaultItem.getValue();
    }
    
    private int getInventorySlot(final String itemName) {
        boolean blockBool = false;
        Object item;
        if (this.allowedItemsItem.containsKey(itemName)) {
            item = this.allowedItemsItem.get(itemName);
        }
        else {
            item = this.allowedItemsBlock.get(itemName);
            blockBool = true;
        }
        for (int i = this.onlyHotBar.getValue() ? 8 : 35; i > (this.noHotBar.getValue() ? 9 : -1); --i) {
            final Item temp = OffHand.mc.player.inventory.getStackInSlot(i).getItem();
            if (blockBool) {
                if (temp instanceof ItemBlock && ((ItemBlock)temp).getBlock() == item) {
                    return i;
                }
            }
            else if (item == temp) {
                if (!itemName.equals("Pot") || this.potionChoose.getValue().equals("first") || OffHand.mc.player.inventory.getStackInSlot(i).stackTagCompound.toString().split(":")[2].contains(this.potionChoose.getValue())) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private void toOffHand(final int t) {
        if (!OffHand.mc.player.getHeldItemOffhand().func_190926_b()) {
            this.prevSlot = t;
            this.returnBack = true;
        }
        else {
            this.prevSlot = -1;
        }
        OffHand.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, (EntityPlayer)OffHand.mc.player);
        this.stepChanging = true;
        this.tickWaited = 0;
    }
    
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + this.totems + ChatFormatting.GRAY + "]";
    }
}
