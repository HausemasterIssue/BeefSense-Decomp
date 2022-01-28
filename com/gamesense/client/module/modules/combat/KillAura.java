



package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import com.gamesense.api.util.world.*;
import java.util.stream.*;
import com.gamesense.api.util.misc.*;
import com.gamesense.client.*;
import com.gamesense.api.util.player.*;
import net.minecraft.entity.*;
import net.minecraft.enchantment.*;
import java.util.*;
import com.gamesense.client.module.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import com.gamesense.api.util.player.friend.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.monster.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;

public class KillAura extends Module
{
    Setting.Boolean players;
    Setting.Boolean hostileMobs;
    Setting.Boolean passiveMobs;
    Setting.Mode itemUsed;
    Setting.Boolean autoSwitch;
    Setting.Boolean swordPriority;
    Setting.Boolean caCheck;
    Setting.Boolean criticals;
    Setting.Double range;
    Setting.Double switchHealth;
    private boolean isAttacking;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public KillAura() {
        super("KillAura", Module.Category.Combat);
        this.isAttacking = false;
        this.listener = (Listener<PacketEvent.Send>)new Listener(event -> {
            if (event.getPacket() instanceof CPacketUseEntity && this.criticals.getValue() && ((CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && KillAura.mc.player.onGround && this.isAttacking) {
                KillAura.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(KillAura.mc.player.posX, KillAura.mc.player.posY + 0.10000000149011612, KillAura.mc.player.posZ, false));
                KillAura.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(KillAura.mc.player.posX, KillAura.mc.player.posY, KillAura.mc.player.posZ, false));
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        final List<String> weapons = new ArrayList<String>();
        weapons.add("Sword");
        weapons.add("Axe");
        weapons.add("Both");
        weapons.add("All");
        this.players = this.registerBoolean("Players", true);
        this.passiveMobs = this.registerBoolean("Animals", false);
        this.hostileMobs = this.registerBoolean("Monsters", false);
        this.range = this.registerDouble("Range", 5.0, 0.0, 10.0);
        this.itemUsed = this.registerMode("Item used", (List)weapons, "Sword");
        this.autoSwitch = this.registerBoolean("Switch", false);
        this.switchHealth = this.registerDouble("Min Switch Health", 0.0, 0.0, 20.0);
        this.swordPriority = this.registerBoolean("Prioritise Sword", true);
        this.criticals = this.registerBoolean("Criticals", true);
        this.caCheck = this.registerBoolean("AC Check", false);
    }
    
    public void onUpdate() {
        if (KillAura.mc.player == null || KillAura.mc.player.isDead) {
            return;
        }
        final double rangeSq = this.range.getValue() * this.range.getValue();
        final List<Entity> targets = (List<Entity>)KillAura.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).filter(entity -> !EntityUtil.basicChecksEntity(entity)).filter(entity -> KillAura.mc.player.getDistanceSqToEntity(entity) <= rangeSq).filter(this::attackCheck).sorted(Comparator.comparing(e -> KillAura.mc.player.getDistanceSqToEntity(e))).collect(Collectors.toList());
        final boolean sword = this.itemUsed.getValue().equalsIgnoreCase("Sword");
        final boolean axe = this.itemUsed.getValue().equalsIgnoreCase("Axe");
        final boolean both = this.itemUsed.getValue().equalsIgnoreCase("Both");
        final boolean all = this.itemUsed.getValue().equalsIgnoreCase("All");
        final Optional<Entity> first = targets.stream().findFirst();
        if (first.isPresent()) {
            Pair<Float, Integer> newSlot = (Pair<Float, Integer>)new Pair((Object)0.0f, (Object)(-1));
            if (this.autoSwitch.getValue() && KillAura.mc.player.getHealth() + KillAura.mc.player.getAbsorptionAmount() >= this.switchHealth.getValue()) {
                if (sword || both || all) {
                    newSlot = this.findSwordSlot();
                }
                if ((axe || both || all) && (!this.swordPriority.getValue() || (int)newSlot.getValue() == -1)) {
                    final Pair<Float, Integer> possibleSlot = this.findAxeSlot();
                    if ((float)possibleSlot.getKey() > (float)newSlot.getKey()) {
                        newSlot = possibleSlot;
                    }
                }
            }
            final int temp = KillAura.mc.player.inventory.currentItem;
            if ((int)newSlot.getValue() != -1) {
                KillAura.mc.player.inventory.currentItem = (int)newSlot.getValue();
            }
            if (this.shouldAttack(sword, axe, both, all)) {
                this.attack(first.get());
            }
            else {
                KillAura.mc.player.inventory.currentItem = temp;
            }
        }
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
    
    private Pair<Float, Integer> findSwordSlot() {
        final List<Integer> items = (List<Integer>)InventoryUtil.findAllItemSlots((Class)ItemSword.class);
        final List<ItemStack> inventory = (List<ItemStack>)KillAura.mc.player.inventory.mainInventory;
        float bestModifier = 0.0f;
        int correspondingSlot = -1;
        for (final Integer integer : items) {
            if (integer > 8) {
                continue;
            }
            final ItemStack stack = inventory.get(integer);
            final float modifier = (EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED) + 1.0f) * ((ItemSword)stack.getItem()).getDamageVsEntity();
            if (modifier <= bestModifier) {
                continue;
            }
            bestModifier = modifier;
            correspondingSlot = integer;
        }
        return (Pair<Float, Integer>)new Pair((Object)bestModifier, (Object)correspondingSlot);
    }
    
    private Pair<Float, Integer> findAxeSlot() {
        final List<Integer> items = (List<Integer>)InventoryUtil.findAllItemSlots((Class)ItemAxe.class);
        final List<ItemStack> inventory = (List<ItemStack>)KillAura.mc.player.inventory.mainInventory;
        float bestModifier = 0.0f;
        int correspondingSlot = -1;
        for (final Integer integer : items) {
            if (integer > 8) {
                continue;
            }
            final ItemStack stack = inventory.get(integer);
            final float modifier = (EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED) + 1.0f) * ((ItemAxe)stack.getItem()).damageVsEntity;
            if (modifier <= bestModifier) {
                continue;
            }
            bestModifier = modifier;
            correspondingSlot = integer;
        }
        return (Pair<Float, Integer>)new Pair((Object)bestModifier, (Object)correspondingSlot);
    }
    
    private boolean shouldAttack(final boolean sword, final boolean axe, final boolean both, final boolean all) {
        final Item item = KillAura.mc.player.getHeldItemMainhand().getItem();
        return (((sword || both) && item instanceof ItemSword) || ((axe || both) && item instanceof ItemAxe) || all) && (!this.caCheck.getValue() || !((AutoCrystalGS)ModuleManager.getModule((Class)AutoCrystalGS.class)).isActive);
    }
    
    private void attack(final Entity e) {
        if (KillAura.mc.player.getCooledAttackStrength(0.0f) >= 1.0f) {
            this.isAttacking = true;
            KillAura.mc.playerController.attackEntity((EntityPlayer)KillAura.mc.player, e);
            KillAura.mc.player.swingArm(EnumHand.MAIN_HAND);
            this.isAttacking = false;
        }
    }
    
    private boolean attackCheck(final Entity entity) {
        if (this.players.getValue() && entity instanceof EntityPlayer && !Friends.isFriend(entity.getName()) && ((EntityPlayer)entity).getHealth() > 0.0f) {
            return true;
        }
        if (this.passiveMobs.getValue() && entity instanceof EntityAnimal) {
            return !(entity instanceof EntityTameable);
        }
        return this.hostileMobs.getValue() && entity instanceof EntityMob;
    }
}
