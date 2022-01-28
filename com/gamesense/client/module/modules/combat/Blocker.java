



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;
import net.minecraft.entity.*;
import com.gamesense.api.util.world.*;
import net.minecraft.util.math.*;
import java.util.*;
import net.minecraft.entity.item.*;
import net.minecraft.util.*;
import com.gamesense.api.util.player.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.combat.*;

public class Blocker extends Module
{
    Setting.Boolean chatMsg;
    Setting.Boolean rotate;
    Setting.Boolean anvilBlocker;
    Setting.Boolean pistonBlocker;
    Setting.Boolean offHandObby;
    Setting.Integer tickDelay;
    private int delayTimeTicks;
    private boolean noObby;
    private boolean noActive;
    private boolean activedBefore;
    
    public Blocker() {
        super("Blocker", Module.Category.Combat);
        this.delayTimeTicks = 0;
    }
    
    public void setup() {
        this.rotate = this.registerBoolean("Rotate", true);
        this.anvilBlocker = this.registerBoolean("Anvil", true);
        this.offHandObby = this.registerBoolean("Off Hand Obby", true);
        this.pistonBlocker = this.registerBoolean("Piston", true);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
    }
    
    public void onEnable() {
        RotationUtil.ROTATION_UTIL.onEnable();
        PlacementUtil.onEnable();
        if (Blocker.mc.player == null) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            String output = "";
            if (this.anvilBlocker.getValue()) {
                output += "Anvil ";
            }
            if (this.pistonBlocker.getValue()) {
                output += " Piston ";
            }
            if (!output.equals("")) {
                this.noActive = false;
                MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + output + " turned ON!");
            }
            else {
                this.noActive = true;
                this.disable();
            }
        }
        this.noObby = false;
    }
    
    public void onDisable() {
        RotationUtil.ROTATION_UTIL.onDisable();
        PlacementUtil.onDisable();
        if (Blocker.mc.player == null) {
            return;
        }
        if (this.chatMsg.getValue()) {
            if (this.noActive) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Nothing is active... Blocker turned OFF!");
            }
            else if (this.noObby) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Obsidian not found... Blocker turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Blocker turned OFF!");
            }
        }
    }
    
    public void onUpdate() {
        if (Blocker.mc.player == null) {
            this.disable();
            return;
        }
        if (this.noObby) {
            this.disable();
            return;
        }
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
        }
        else {
            RotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
            this.delayTimeTicks = 0;
            if (this.anvilBlocker.getValue()) {
                this.blockAnvil();
            }
            if (this.pistonBlocker.getValue()) {
                this.blockPiston();
            }
        }
    }
    
    private void blockAnvil() {
        boolean found = false;
        for (final Entity t : Blocker.mc.world.loadedEntityList) {
            if (t instanceof EntityFallingBlock) {
                final Block ex = ((EntityFallingBlock)t).fallTile.getBlock();
                if (!(ex instanceof BlockAnvil) || (int)t.posX != (int)Blocker.mc.player.posX || (int)t.posZ != (int)Blocker.mc.player.posZ || !(BlockUtil.getBlock(Blocker.mc.player.posX, Blocker.mc.player.posY + 2.0, Blocker.mc.player.posZ) instanceof BlockAir)) {
                    continue;
                }
                this.placeBlock(new BlockPos(Blocker.mc.player.posX, Blocker.mc.player.posY + 2.0, Blocker.mc.player.posZ));
                MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoAnvil detected... Anvil Blocked!");
                found = true;
            }
        }
        if (!found && this.activedBefore) {
            this.activedBefore = false;
            OffHand.removeObsidian();
        }
    }
    
    private void blockPiston() {
        for (final Entity t : Blocker.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && t.posX >= Blocker.mc.player.posX - 1.5 && t.posX <= Blocker.mc.player.posX + 1.5 && t.posZ >= Blocker.mc.player.posZ - 1.5 && t.posZ <= Blocker.mc.player.posZ + 1.5) {
                for (int i = -2; i < 3; ++i) {
                    for (int j = -2; j < 3; ++j) {
                        if ((i == 0 || j == 0) && BlockUtil.getBlock(t.posX + i, t.posY, t.posZ + j) instanceof BlockPistonBase) {
                            this.breakCrystalPiston(t);
                            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "PistonCrystal detected... Destroyed crystal!");
                        }
                    }
                }
            }
        }
    }
    
    private void placeBlock(final BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        final int obsidianSlot = InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedBefore);
        if (obsidianSlot == -1) {
            this.noObby = true;
            return;
        }
        if (obsidianSlot == 9) {
            this.activedBefore = true;
            if (!(Blocker.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) || !(((ItemBlock)Blocker.mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian)) {
                return;
            }
            handSwing = EnumHand.OFF_HAND;
        }
        if (Blocker.mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != 9) {
            Blocker.mc.player.inventory.currentItem = obsidianSlot;
        }
        PlacementUtil.place(pos, handSwing, this.rotate.getValue());
    }
    
    private void breakCrystalPiston(final Entity crystal) {
        if (this.rotate.getValue()) {
            RotationUtil.ROTATION_UTIL.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, (EntityPlayer)Blocker.mc.player);
        }
        CrystalUtil.breakCrystal(crystal);
        if (this.rotate.getValue()) {
            RotationUtil.ROTATION_UTIL.resetRotation();
        }
    }
}
