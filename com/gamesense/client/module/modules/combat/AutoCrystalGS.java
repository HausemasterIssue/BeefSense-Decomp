



package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.*;
import me.zero.alpine.listener.*;
import com.gamesense.api.util.player.*;
import net.minecraft.entity.item.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.*;
import com.gamesense.api.util.world.*;
import java.util.stream.*;
import net.minecraft.network.play.client.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.client.module.*;
import com.gamesense.api.util.combat.*;
import net.minecraft.util.math.*;
import net.minecraft.client.entity.*;
import com.gamesense.api.event.events.*;
import com.gamesense.api.util.render.*;
import java.util.*;
import net.minecraft.entity.*;
import java.util.function.*;
import com.gamesense.client.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.client.*;

public class AutoCrystalGS extends Module
{
    Setting.Boolean breakCrystal;
    Setting.Boolean antiWeakness;
    Setting.Boolean placeCrystal;
    Setting.Boolean autoSwitch;
    Setting.Boolean raytrace;
    Setting.Boolean rotate;
    Setting.Boolean spoofRotations;
    Setting.Boolean chat;
    Setting.Boolean showDamage;
    Setting.Boolean antiSuicide;
    Setting.Boolean multiPlace;
    public static Setting.Boolean endCrystalMode;
    Setting.Boolean cancelCrystal;
    Setting.Boolean noGapSwitch;
    Setting.Boolean refresh;
    Setting.Integer facePlaceValue;
    Setting.Integer attackSpeed;
    Setting.Integer antiSuicideValue;
    Setting.Integer attackValue;
    Setting.Double maxSelfDmg;
    Setting.Double wallsRange;
    Setting.Double minDmg;
    Setting.Double minBreakDmg;
    Setting.Double enemyRange;
    public static Setting.Double placeRange;
    public static Setting.Double breakRange;
    Setting.Mode handBreak;
    Setting.Mode breakMode;
    Setting.Mode hudDisplay;
    Setting.Mode breakType;
    Setting.ColorSetting color;
    private boolean switchCooldown;
    private boolean isAttacking;
    public boolean isActive;
    public static boolean stopAC;
    private static boolean togglePitch;
    private int oldSlot;
    private Entity renderEnt;
    private BlockPos render;
    public static final ArrayList<BlockPos> PlacedCrystals;
    private EnumFacing enumFacing;
    Timer timer;
    Timer stuckTimer;
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener;
    
    public AutoCrystalGS() {
        super("AutoCrystalGS", Module.Category.Combat);
        this.switchCooldown = false;
        this.isAttacking = false;
        this.isActive = false;
        this.oldSlot = -1;
        this.timer = new Timer();
        this.stuckTimer = new Timer();
        this.packetReceiveListener = (Listener<PacketEvent.Receive>)new Listener(event -> {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (final Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
                        if (e instanceof EntityEnderCrystal && e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0) {
                            e.setDead();
                        }
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        final ArrayList<String> hands = new ArrayList<String>();
        hands.add("Main");
        hands.add("Offhand");
        hands.add("Both");
        final ArrayList<String> breakModes = new ArrayList<String>();
        breakModes.add("All");
        breakModes.add("Smart");
        breakModes.add("Own");
        final ArrayList<String> hudModes = new ArrayList<String>();
        hudModes.add("Mode");
        hudModes.add("None");
        final ArrayList<String> breakTypes = new ArrayList<String>();
        breakTypes.add("Swing");
        breakTypes.add("Packet");
        this.breakMode = this.registerMode("Target", (List)breakModes, "All");
        this.handBreak = this.registerMode("Hand", (List)hands, "Main");
        this.breakType = this.registerMode("Type", (List)breakTypes, "Swing");
        this.breakCrystal = this.registerBoolean("Break", true);
        this.placeCrystal = this.registerBoolean("Place", true);
        this.attackSpeed = this.registerInteger("Attack Speed", 16, 0, 20);
        this.attackValue = this.registerInteger("Hit Amount", 1, 1, 10);
        AutoCrystalGS.breakRange = this.registerDouble("Hit Range", 4.4, 0.0, 10.0);
        AutoCrystalGS.placeRange = this.registerDouble("Place Range", 4.4, 0.0, 6.0);
        this.wallsRange = this.registerDouble("Walls Range", 3.5, 0.0, 10.0);
        this.enemyRange = this.registerDouble("Enemy Range", 6.0, 0.0, 16.0);
        this.refresh = this.registerBoolean("Refresh", true);
        this.antiWeakness = this.registerBoolean("Anti Weakness", true);
        this.antiSuicide = this.registerBoolean("Anti Suicide", true);
        this.antiSuicideValue = this.registerInteger("Min Health", 14, 1, 36);
        this.autoSwitch = this.registerBoolean("Switch", true);
        this.noGapSwitch = this.registerBoolean("No Gap Switch", false);
        this.multiPlace = this.registerBoolean("Multi Place", false);
        AutoCrystalGS.endCrystalMode = this.registerBoolean("1.13 Place", false);
        this.cancelCrystal = this.registerBoolean("Cancel Crystal", false);
        this.minDmg = this.registerDouble("Min Damage", 5.0, 0.0, 36.0);
        this.minBreakDmg = this.registerDouble("Min Break Dmg", 5.0, 0.0, 36.0);
        this.maxSelfDmg = this.registerDouble("Max Self Dmg", 10.0, 1.0, 36.0);
        this.facePlaceValue = this.registerInteger("FacePlace HP", 8, 0, 36);
        this.rotate = this.registerBoolean("Rotate", true);
        this.spoofRotations = this.registerBoolean("Spoof Angles", true);
        this.raytrace = this.registerBoolean("Raytrace", false);
        this.showDamage = this.registerBoolean("Render Dmg", true);
        this.chat = this.registerBoolean("Chat Msgs", true);
        this.hudDisplay = this.registerMode("HUD", (List)hudModes, "Mode");
        this.color = this.registerColor("Color", new GSColor(0, 255, 0, 50));
    }
    
    public void onUpdate() {
        if (AutoCrystalGS.mc.player == null || AutoCrystalGS.mc.world == null || AutoCrystalGS.mc.player.isDead) {
            this.disable();
            return;
        }
        if (AutoCrystalGS.stopAC) {
            return;
        }
        if (this.refresh.getValue() && this.stuckTimer.getTimePassed() / 1000L >= 2L) {
            this.stuckTimer.reset();
            AutoCrystalGS.PlacedCrystals.clear();
        }
        if (this.antiSuicide.getValue() && AutoCrystalGS.mc.player.getHealth() + AutoCrystalGS.mc.player.getAbsorptionAmount() <= this.antiSuicideValue.getValue()) {
            return;
        }
        RotationUtil.ROTATION_UTIL.shouldSpoofAngles(this.spoofRotations.getValue());
        this.isActive = false;
        final EntityEnderCrystal crystal = (EntityEnderCrystal)AutoCrystalGS.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).filter(e -> AutoCrystalGS.mc.player.getDistanceToEntity(e) <= AutoCrystalGS.breakRange.getValue()).filter(this::crystalCheck).map(entity -> entity).min(Comparator.comparing(c -> AutoCrystalGS.mc.player.getDistanceToEntity(c))).orElse(null);
        if (this.breakCrystal.getValue() && crystal != null) {
            if (!AutoCrystalGS.mc.player.canEntityBeSeen((Entity)crystal) && AutoCrystalGS.mc.player.getDistanceToEntity((Entity)crystal) > this.wallsRange.getValue()) {
                return;
            }
            if (this.antiWeakness.getValue() && AutoCrystalGS.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!this.isAttacking) {
                    this.oldSlot = AutoCrystalGS.mc.player.inventory.currentItem;
                    this.isAttacking = true;
                }
                int newSlot = -1;
                for (int i = 0; i < 9; ++i) {
                    final ItemStack stack = AutoCrystalGS.mc.player.inventory.getStackInSlot(i);
                    if (stack != ItemStack.field_190927_a) {
                        if (stack.getItem() instanceof ItemSword) {
                            newSlot = i;
                            break;
                        }
                        if (stack.getItem() instanceof ItemTool) {
                            newSlot = i;
                            break;
                        }
                    }
                }
                if (newSlot != -1) {
                    AutoCrystalGS.mc.player.inventory.currentItem = newSlot;
                    this.switchCooldown = true;
                }
            }
            if (this.timer.getTimePassed() / 50L >= 20 - this.attackSpeed.getValue()) {
                this.timer.reset();
                this.isActive = true;
                if (this.rotate.getValue()) {
                    RotationUtil.ROTATION_UTIL.lookAtPacket(crystal.posX + 0.5, crystal.posY + 0.5, crystal.posZ + 0.5, (EntityPlayer)AutoCrystalGS.mc.player);
                }
                final Entity crystal2;
                IntStream.range(0, this.attackValue.getValue()).forEach(i -> {
                    if (this.breakType.getValue().equalsIgnoreCase("Swing")) {
                        this.breakCrystal((EntityEnderCrystal)crystal2);
                    }
                    else if (this.breakType.getValue().equalsIgnoreCase("Packet")) {
                        AutoCrystalGS.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(crystal2));
                        this.swingArm();
                    }
                    return;
                });
                if (this.cancelCrystal.getValue()) {
                    crystal.setDead();
                    AutoCrystalGS.mc.world.removeAllEntities();
                    AutoCrystalGS.mc.world.getLoadedEntityList();
                }
                this.isActive = false;
            }
            if (!this.multiPlace.getValue()) {
                return;
            }
        }
        else {
            RotationUtil.ROTATION_UTIL.resetRotation();
            if (this.oldSlot != -1) {
                AutoCrystalGS.mc.player.inventory.currentItem = this.oldSlot;
                this.oldSlot = -1;
            }
            this.isAttacking = false;
            this.isActive = false;
        }
        int crystalSlot = (AutoCrystalGS.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? AutoCrystalGS.mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (AutoCrystalGS.mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL && AutoCrystalGS.mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() != Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }
        boolean offhand = false;
        if (AutoCrystalGS.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        }
        else if (crystalSlot == -1) {
            return;
        }
        final List<BlockPos> blocks = (List<BlockPos>)CrystalUtil.findCrystalBlocks((float)AutoCrystalGS.placeRange.getValue(), AutoCrystalGS.endCrystalMode.getValue());
        final List<Entity> entities = (List<Entity>)AutoCrystalGS.mc.world.playerEntities.stream().filter(entityPlayer -> !EntityUtil.basicChecksEntity(entityPlayer)).sorted(Comparator.comparing(e -> AutoCrystalGS.mc.player.getDistanceToEntity(e))).collect(Collectors.toList());
        BlockPos q = null;
        double damage = 0.5;
        final Iterator var9 = entities.iterator();
        while (true) {
            if (!var9.hasNext()) {
                if (damage == 0.5) {
                    this.render = null;
                    this.renderEnt = null;
                    RotationUtil.ROTATION_UTIL.resetRotation();
                    return;
                }
                this.render = q;
                if (this.placeCrystal.getValue()) {
                    if (!offhand && AutoCrystalGS.mc.player.inventory.currentItem != crystalSlot) {
                        if (this.autoSwitch.getValue() && ((this.noGapSwitch.getValue() && AutoCrystalGS.mc.player.getHeldItemMainhand().getItem() != Items.GOLDEN_APPLE) || !this.noGapSwitch.getValue())) {
                            AutoCrystalGS.mc.player.inventory.currentItem = crystalSlot;
                            RotationUtil.ROTATION_UTIL.resetRotation();
                            this.switchCooldown = true;
                        }
                        return;
                    }
                    if (this.rotate.getValue()) {
                        RotationUtil.ROTATION_UTIL.lookAtPacket(q.getX() + 0.5, q.getY() + 0.5, q.getZ() + 0.5, (EntityPlayer)AutoCrystalGS.mc.player);
                    }
                    final RayTraceResult result = AutoCrystalGS.mc.world.rayTraceBlocks(new Vec3d(AutoCrystalGS.mc.player.posX, AutoCrystalGS.mc.player.posY + AutoCrystalGS.mc.player.getEyeHeight(), AutoCrystalGS.mc.player.posZ), new Vec3d(q.getX() + 0.5, q.getY() - 0.5, q.getZ() + 0.5));
                    if (this.raytrace.getValue()) {
                        if (result == null || result.sideHit == null) {
                            this.enumFacing = null;
                            this.render = null;
                            RotationUtil.ROTATION_UTIL.resetRotation();
                            this.isActive = false;
                            return;
                        }
                        this.enumFacing = result.sideHit;
                    }
                    if (this.switchCooldown) {
                        this.switchCooldown = false;
                        return;
                    }
                    if (AutoCrystalGS.mc.player != null) {
                        this.isActive = true;
                        if (this.raytrace.getValue() && this.enumFacing != null) {
                            AutoCrystalGS.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(q, this.enumFacing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                        }
                        else if (q.getY() == 255) {
                            AutoCrystalGS.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                        }
                        else {
                            AutoCrystalGS.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                        }
                        AutoCrystalGS.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                        AutoCrystalGS.PlacedCrystals.add(q);
                        if (ModuleManager.isModuleEnabled((Class)AutoGG.class)) {
                            AutoGG.INSTANCE.addTargetedPlayer(this.renderEnt.getName());
                        }
                    }
                    if (RotationUtil.ROTATION_UTIL.isSpoofingAngles()) {
                        if (AutoCrystalGS.togglePitch) {
                            final EntityPlayerSP var10 = AutoCrystalGS.mc.player;
                            var10.rotationPitch += (float)4.0E-4;
                            AutoCrystalGS.togglePitch = false;
                        }
                        else {
                            final EntityPlayerSP var10 = AutoCrystalGS.mc.player;
                            var10.rotationPitch -= (float)4.0E-4;
                            AutoCrystalGS.togglePitch = true;
                        }
                    }
                    return;
                }
            }
            final EntityPlayer entity2 = var9.next();
            if (entity2 != AutoCrystalGS.mc.player && entity2.getHealth() > 0.0f) {
                for (final BlockPos blockPos : blocks) {
                    final double x = blockPos.getX() + 0.0;
                    final double y = blockPos.getY() + 1.0;
                    final double z = blockPos.getZ() + 0.0;
                    if (entity2.getDistanceSq(x, y, z) < this.enemyRange.getValue() * this.enemyRange.getValue()) {
                        final double d = DamageUtil.calculateDamage(blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), blockPos.getZ() + 0.5, (Entity)entity2);
                        if (d <= damage) {
                            continue;
                        }
                        final double targetDamage = DamageUtil.calculateDamage(blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), blockPos.getZ() + 0.5, (Entity)entity2);
                        final float targetHealth = entity2.getHealth() + entity2.getAbsorptionAmount();
                        if (targetDamage < this.minDmg.getValue() && targetHealth > this.facePlaceValue.getValue()) {
                            continue;
                        }
                        final double self = DamageUtil.calculateDamage(blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), blockPos.getZ() + 0.5, (Entity)AutoCrystalGS.mc.player);
                        if (self >= this.maxSelfDmg.getValue() || self >= AutoCrystalGS.mc.player.getHealth() + AutoCrystalGS.mc.player.getAbsorptionAmount()) {
                            continue;
                        }
                        damage = d;
                        q = blockPos;
                        this.renderEnt = (Entity)entity2;
                    }
                }
            }
        }
    }
    
    public void onWorldRender(final RenderEvent event) {
        if (this.render != null) {
            RenderUtil.drawBox(this.render, 1.0, new GSColor(this.color.getValue(), 50), 63);
            RenderUtil.drawBoundingBox(this.render, 1.0, 1.0f, new GSColor(this.color.getValue(), 255));
        }
        if (this.showDamage.getValue() && this.render != null && this.renderEnt != null) {
            final double d = DamageUtil.calculateDamage(this.render.getX() + 0.5, (double)(this.render.getY() + 1), this.render.getZ() + 0.5, this.renderEnt);
            final String[] damageText = { ((Math.floor(d) == d) ? Integer.valueOf((int)d) : String.format("%.1f", d)) + "" };
            RenderUtil.drawNametag(this.render.getX() + 0.5, this.render.getY() + 0.5, this.render.getZ() + 0.5, damageText, new GSColor(255, 255, 255), 1);
        }
    }
    
    private boolean crystalCheck(final Entity crystal) {
        if (!(crystal instanceof EntityEnderCrystal)) {
            return false;
        }
        if (this.breakMode.getValue().equalsIgnoreCase("All")) {
            return true;
        }
        if (this.breakMode.getValue().equalsIgnoreCase("Own")) {
            for (final BlockPos pos : new ArrayList<BlockPos>(AutoCrystalGS.PlacedCrystals)) {
                if (pos != null && pos.getDistance((int)crystal.posX, (int)crystal.posY, (int)crystal.posZ) <= 3.0) {
                    return true;
                }
            }
        }
        else if (this.breakMode.getValue().equalsIgnoreCase("Smart")) {
            final EntityLivingBase target = (EntityLivingBase)((this.renderEnt != null) ? this.renderEnt : this.GetNearTarget(crystal));
            if (target == null || target == AutoCrystalGS.mc.player) {
                return false;
            }
            final float targetDmg = DamageUtil.calculateDamage(crystal.posX + 0.5, crystal.posY + 1.0, crystal.posZ + 0.5, (Entity)target);
            return targetDmg >= this.minBreakDmg.getValue() || (targetDmg > this.minBreakDmg.getValue() && target.getHealth() > this.facePlaceValue.getValue());
        }
        return false;
    }
    
    private EntityLivingBase GetNearTarget(final Entity distanceTarget) {
        return (EntityLivingBase)AutoCrystalGS.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).filter(entity -> !EntityUtil.basicChecksEntity(entity)).map(entity -> entity).min(Comparator.comparing((Function<? super T, ? extends Comparable>)distanceTarget::getDistanceSqToEntity)).orElse(null);
    }
    
    private void breakCrystal(final EntityEnderCrystal crystal) {
        AutoCrystalGS.mc.playerController.attackEntity((EntityPlayer)AutoCrystalGS.mc.player, (Entity)crystal);
        this.swingArm();
    }
    
    private void swingArm() {
        if (this.handBreak.getValue().equalsIgnoreCase("Both")) {
            AutoCrystalGS.mc.player.swingArm(EnumHand.MAIN_HAND);
            AutoCrystalGS.mc.player.swingArm(EnumHand.OFF_HAND);
        }
        else if (this.handBreak.getValue().equalsIgnoreCase("Offhand")) {
            AutoCrystalGS.mc.player.swingArm(EnumHand.OFF_HAND);
        }
        else {
            AutoCrystalGS.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    public void onEnable() {
        RotationUtil.ROTATION_UTIL.onEnable();
        GameSense.EVENT_BUS.subscribe((Object)this);
        AutoCrystalGS.PlacedCrystals.clear();
        this.isActive = false;
        if (this.chat.getValue() && AutoCrystalGS.mc.player != null) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoCrystalGS turned ON!");
        }
    }
    
    public void onDisable() {
        RotationUtil.ROTATION_UTIL.onDisable();
        GameSense.EVENT_BUS.unsubscribe((Object)this);
        this.render = null;
        this.renderEnt = null;
        RotationUtil.ROTATION_UTIL.resetRotation();
        AutoCrystalGS.PlacedCrystals.clear();
        this.isActive = false;
        if (this.chat.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "AutoCrystalGS turned OFF!");
        }
    }
    
    public String getHudInfo() {
        String t = "";
        if (this.hudDisplay.getValue().equalsIgnoreCase("Mode")) {
            if (this.breakMode.getValue().equalsIgnoreCase("All")) {
                t = "[" + ChatFormatting.WHITE + "All" + ChatFormatting.GRAY + "]";
            }
            if (this.breakMode.getValue().equalsIgnoreCase("Smart")) {
                t = "[" + ChatFormatting.WHITE + "Smart" + ChatFormatting.GRAY + "]";
            }
            if (this.breakMode.getValue().equalsIgnoreCase("Own")) {
                t = "[" + ChatFormatting.WHITE + "Own" + ChatFormatting.GRAY + "]";
            }
        }
        if (this.hudDisplay.getValue().equalsIgnoreCase("None")) {
            t = "";
        }
        return t;
    }
    
    static {
        AutoCrystalGS.stopAC = false;
        AutoCrystalGS.togglePitch = false;
        PlacedCrystals = new ArrayList<BlockPos>();
    }
}
