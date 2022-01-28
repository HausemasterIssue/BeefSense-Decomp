



package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import com.gamesense.api.util.player.*;
import net.minecraft.entity.item.*;
import java.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.*;
import com.gamesense.client.module.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import com.gamesense.api.util.world.*;

public class AutoAnvil extends Module
{
    Setting.Mode anvilMode;
    Setting.Mode target;
    Setting.Mode anvilPlace;
    Setting.Double enemyRange;
    Setting.Double decrease;
    Setting.Boolean rotate;
    Setting.Boolean antiCrystal;
    Setting.Boolean fastAnvil;
    Setting.Boolean offHandObby;
    Setting.Boolean chatMsg;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;
    Setting.Integer hDistance;
    Setting.Integer minH;
    Setting.Integer failStop;
    private boolean isSneaking;
    private boolean firstRun;
    private boolean noMaterials;
    private boolean hasMoved;
    private boolean isHole;
    private boolean enoughSpace;
    private boolean blockUp;
    private int oldSlot;
    private int noKick;
    private int anvilBlock;
    private ArrayList<Integer> anvilsPositions;
    private int[] slot_mat;
    private double[] enemyCoords;
    Double[][] sur_block;
    int[][] model;
    private int blocksPlaced;
    private int delayTimeTicks;
    private int offsetSteps;
    private boolean pick_d;
    private EntityPlayer aimTarget;
    private static ArrayList<Vec3d> to_place;
    
    public AutoAnvil() {
        super("AutoAnvil", Module.Category.Combat);
        this.isSneaking = false;
        this.firstRun = false;
        this.noMaterials = false;
        this.hasMoved = false;
        this.isHole = true;
        this.enoughSpace = true;
        this.blockUp = false;
        this.oldSlot = -1;
        this.anvilsPositions = new ArrayList<Integer>();
        this.slot_mat = new int[] { -1, -1, -1, -1 };
        this.model = new int[][] { { 1, 1, 0 }, { -1, 1, 0 }, { 0, 1, 1 }, { 0, 1, -1 } };
        this.blocksPlaced = 0;
        this.delayTimeTicks = 0;
        this.offsetSteps = 0;
        this.pick_d = false;
    }
    
    public void setup() {
        final ArrayList<String> anvilTypesList = new ArrayList<String>();
        anvilTypesList.add("Pick");
        anvilTypesList.add("Feet");
        anvilTypesList.add("None");
        final ArrayList<String> targetChoose = new ArrayList<String>();
        targetChoose.add("Nearest");
        targetChoose.add("Looking");
        final ArrayList<String> anvilPlaceTypes = new ArrayList<String>();
        anvilPlaceTypes.add("single");
        anvilPlaceTypes.add("double");
        anvilPlaceTypes.add("full");
        this.anvilMode = this.registerMode("Mode", (List)anvilTypesList, "Pick");
        this.target = this.registerMode("Target", (List)targetChoose, "Nearest");
        this.anvilPlace = this.registerMode("Anvil Place", (List)anvilPlaceTypes, "single");
        this.antiCrystal = this.registerBoolean("Anti Crystal", false);
        this.fastAnvil = this.registerBoolean("Fast Anvil", true);
        this.offHandObby = this.registerBoolean("Off Hand Obby", false);
        this.rotate = this.registerBoolean("Rotate", true);
        this.enemyRange = this.registerDouble("Range", 5.9, 0.0, 6.0);
        this.decrease = this.registerDouble("Decrease", 2.0, 0.0, 6.0);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
        this.hDistance = this.registerInteger("H Distance", 7, 1, 10);
        this.minH = this.registerInteger("Min H", 3, 1, 10);
        this.failStop = this.registerInteger("Fail Stop", 2, 1, 10);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
    }
    
    public void onEnable() {
        if (this.anvilMode.getValue().equalsIgnoreCase("Pick")) {
            this.pick_d = true;
        }
        this.blocksPlaced = 0;
        this.isHole = true;
        final boolean b = false;
        this.blockUp = b;
        this.hasMoved = b;
        this.firstRun = true;
        this.slot_mat = new int[] { -1, -1, -1, -1 };
        AutoAnvil.to_place = new ArrayList<Vec3d>();
        if (AutoAnvil.mc.player == null) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            PistonCrystal.printChat("AutoAnvil turned ON!", false);
        }
        this.oldSlot = AutoAnvil.mc.player.inventory.currentItem;
    }
    
    public void onDisable() {
        if (AutoAnvil.mc.player == null) {
            return;
        }
        if (this.chatMsg.getValue()) {
            if (this.noMaterials) {
                PistonCrystal.printChat("No Materials Detected... AutoAnvil turned OFF!", true);
            }
            else if (!this.isHole) {
                PistonCrystal.printChat("Enemy is not in a hole... AutoAnvil turned OFF!", true);
            }
            else if (!this.enoughSpace) {
                PistonCrystal.printChat("Not enough space... AutoAnvil turned OFF!", true);
            }
            else if (this.hasMoved) {
                PistonCrystal.printChat("Enemy moved away from the hole... AutoAnvil turned OFF!", true);
            }
            else if (this.blockUp) {
                PistonCrystal.printChat("Enemy head blocked.. AutoAnvil turned OFF!", true);
            }
            else {
                PistonCrystal.printChat("AutoAnvil turned OFF!", true);
            }
        }
        if (this.isSneaking) {
            AutoAnvil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoAnvil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != AutoAnvil.mc.player.inventory.currentItem && this.oldSlot != -1) {
            AutoAnvil.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
        this.noMaterials = false;
        this.firstRun = true;
        AutoCrystalGS.stopAC = false;
        if (this.slot_mat[0] == -2) {
            OffHand.removeObsidian();
        }
    }
    
    public void onUpdate() {
        if (AutoAnvil.mc.player == null) {
            this.disable();
            return;
        }
        if (this.firstRun) {
            if (this.target.getValue().equals("Nearest")) {
                this.aimTarget = PlayerUtil.findClosestTarget(this.enemyRange.getValue(), this.aimTarget);
            }
            else if (this.target.getValue().equals("Looking")) {
                this.aimTarget = PlayerUtil.findLookingPlayer(this.enemyRange.getValue());
            }
            if (this.aimTarget == null) {
                return;
            }
            this.firstRun = false;
            if (this.getMaterialsSlot()) {
                if (this.is_in_hole()) {
                    this.enemyCoords = new double[] { this.aimTarget.posX, this.aimTarget.posY, this.aimTarget.posZ };
                    this.enoughSpace = this.createStructure();
                }
                else {
                    this.isHole = false;
                }
            }
            else {
                this.noMaterials = true;
            }
        }
        else {
            if (this.delayTimeTicks < this.tickDelay.getValue()) {
                ++this.delayTimeTicks;
                return;
            }
            this.delayTimeTicks = 0;
            if ((int)this.enemyCoords[0] != (int)this.aimTarget.posX || (int)this.enemyCoords[2] != (int)this.aimTarget.posZ) {
                this.hasMoved = true;
            }
            if (!(BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + 2.0, this.enemyCoords[2]) instanceof BlockAir) && !(BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + 2.0, this.enemyCoords[2]) instanceof BlockAnvil)) {
                this.blockUp = true;
            }
        }
        this.blocksPlaced = 0;
        if (this.noMaterials || !this.isHole || !this.enoughSpace || this.hasMoved || this.blockUp) {
            this.disable();
            return;
        }
        this.anvilsPositions = new ArrayList<Integer>();
        for (final Entity everyEntity : AutoAnvil.mc.world.loadedEntityList) {
            if (everyEntity instanceof EntityFallingBlock) {
                this.anvilsPositions.add((int)everyEntity.posY);
                this.anvilsPositions.add((int)everyEntity.posY + 1);
            }
        }
        this.noKick = 0;
        while (this.blocksPlaced <= this.blocksPerTick.getValue()) {
            final int maxSteps = AutoAnvil.to_place.size();
            if (this.offsetSteps >= maxSteps) {
                this.offsetSteps = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos((Vec3d)AutoAnvil.to_place.get(this.offsetSteps));
            final BlockPos targetPos = new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean tryPlacing = true;
            if (this.offsetSteps > 0 && this.offsetSteps < AutoAnvil.to_place.size() - 1) {
                for (final Entity entity : AutoAnvil.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(targetPos))) {
                    if (entity instanceof EntityPlayer) {
                        tryPlacing = false;
                        break;
                    }
                }
            }
            if (tryPlacing && this.placeBlock(targetPos, this.offsetSteps)) {
                ++this.blocksPlaced;
            }
            ++this.offsetSteps;
            if (this.isSneaking) {
                AutoAnvil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoAnvil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
            if (this.noKick == this.failStop.getValue()) {
                break;
            }
        }
    }
    
    private boolean placeBlock(final BlockPos pos, final int step) {
        final Block block = AutoAnvil.mc.world.getBlockState(pos).getBlock();
        final EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (step == AutoAnvil.to_place.size() - 1 && block instanceof BlockAnvil && side != null) {
            AutoAnvil.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
            ++this.noKick;
        }
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = AutoAnvil.mc.world.getBlockState(neighbour).getBlock();
        EnumHand handSwing = EnumHand.MAIN_HAND;
        final int utilSlot = (step == 0 && this.anvilMode.getValue().equalsIgnoreCase("feet")) ? 2 : ((step >= AutoAnvil.to_place.size() - this.anvilBlock) ? 1 : 0);
        if (step == 1 && this.anvilsPositions.contains(pos.y)) {
            return false;
        }
        if (this.offHandObby.getValue() && OffHand.isActive() && this.slot_mat[utilSlot] == -2) {
            if (!(AutoAnvil.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) || !(((ItemBlock)AutoAnvil.mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian)) {
                return false;
            }
            handSwing = EnumHand.OFF_HAND;
        }
        else {
            if (AutoAnvil.mc.player.inventory.getStackInSlot(this.slot_mat[utilSlot]) == ItemStack.field_190927_a) {
                return false;
            }
            if (AutoAnvil.mc.player.inventory.currentItem != this.slot_mat[utilSlot]) {
                AutoAnvil.mc.player.inventory.currentItem = this.slot_mat[utilSlot];
            }
        }
        if ((!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            AutoAnvil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoAnvil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled((Class)AutoCrystalGS.class)) {
            AutoCrystalGS.stopAC = true;
            stoppedAC = true;
        }
        if (this.rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec, Boolean.valueOf(true));
        }
        final int bef = AutoAnvil.mc.rightClickDelayTimer;
        if (step == AutoAnvil.to_place.size() - 1) {
            final EntityPlayer found = this.getPlayerFromName(this.aimTarget.gameProfile.getName());
            if (found == null || (int)found.posX != (int)this.enemyCoords[0] || (int)found.posZ != (int)this.enemyCoords[2]) {
                this.hasMoved = true;
                return false;
            }
            if (this.fastAnvil.getValue()) {
                AutoAnvil.mc.rightClickDelayTimer = 0;
            }
        }
        AutoAnvil.mc.playerController.processRightClickBlock(AutoAnvil.mc.player, AutoAnvil.mc.world, neighbour, opposite, hitVec, handSwing);
        AutoAnvil.mc.player.swingArm(handSwing);
        if (this.fastAnvil.getValue() && step == AutoAnvil.to_place.size() - 1) {
            AutoAnvil.mc.rightClickDelayTimer = bef;
        }
        if (stoppedAC) {
            AutoCrystalGS.stopAC = false;
            stoppedAC = false;
        }
        if (this.pick_d && step == AutoAnvil.to_place.size() - 1) {
            final EnumFacing prova = BlockUtil.getPlaceableSide(new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]));
            if (prova != null) {
                AutoAnvil.mc.player.inventory.currentItem = this.slot_mat[3];
                AutoAnvil.mc.player.swingArm(EnumHand.MAIN_HAND);
                AutoAnvil.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]), prova));
                AutoAnvil.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]), prova));
            }
        }
        return true;
    }
    
    private EntityPlayer getPlayerFromName(final String name) {
        final List<EntityPlayer> playerList = (List<EntityPlayer>)AutoAnvil.mc.world.playerEntities;
        for (final EntityPlayer entityPlayer : playerList) {
            if (entityPlayer.gameProfile.getName().equals(name)) {
                return entityPlayer;
            }
        }
        return null;
    }
    
    private boolean getMaterialsSlot() {
        boolean feet = false;
        boolean pick = false;
        if (this.anvilMode.getValue().equalsIgnoreCase("Feet")) {
            feet = true;
        }
        if (this.anvilMode.getValue().equalsIgnoreCase("Pick")) {
            pick = true;
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoAnvil.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.field_190927_a) {
                if (pick && stack.getItem() instanceof ItemPickaxe) {
                    this.slot_mat[3] = i;
                }
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockObsidian) {
                        this.slot_mat[0] = i;
                    }
                    else if (block instanceof BlockAnvil) {
                        this.slot_mat[1] = i;
                    }
                    else if (feet && (block instanceof BlockPressurePlate || block instanceof BlockButton)) {
                        this.slot_mat[2] = i;
                    }
                }
            }
        }
        if (this.offHandObby.getValue() && OffHand.isActive()) {
            this.slot_mat[0] = -2;
            OffHand.requestObsidian();
        }
        int count = 0;
        for (final int val : this.slot_mat) {
            if (val != -1) {
                ++count;
            }
        }
        return count - ((feet || pick) ? 1 : 0) == 2;
    }
    
    private boolean is_in_hole() {
        this.sur_block = new Double[][] { { this.aimTarget.posX + 1.0, this.aimTarget.posY, this.aimTarget.posZ }, { this.aimTarget.posX - 1.0, this.aimTarget.posY, this.aimTarget.posZ }, { this.aimTarget.posX, this.aimTarget.posY, this.aimTarget.posZ + 1.0 }, { this.aimTarget.posX, this.aimTarget.posY, this.aimTarget.posZ - 1.0 } };
        this.enemyCoords = new double[] { this.aimTarget.posX, this.aimTarget.posY, this.aimTarget.posZ };
        return HoleUtil.isHole(EntityUtil.getPosition((Entity)this.aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }
    
    private boolean createStructure() {
        if (this.anvilMode.getValue().equalsIgnoreCase("feet")) {
            AutoAnvil.to_place.add(new Vec3d(0.0, 0.0, 0.0));
        }
        AutoAnvil.to_place.add(new Vec3d(1.0, 1.0, 0.0));
        AutoAnvil.to_place.add(new Vec3d(-1.0, 1.0, 0.0));
        AutoAnvil.to_place.add(new Vec3d(0.0, 1.0, 1.0));
        AutoAnvil.to_place.add(new Vec3d(0.0, 1.0, -1.0));
        AutoAnvil.to_place.add(new Vec3d(1.0, 2.0, 0.0));
        AutoAnvil.to_place.add(new Vec3d(-1.0, 2.0, 0.0));
        AutoAnvil.to_place.add(new Vec3d(0.0, 2.0, 1.0));
        AutoAnvil.to_place.add(new Vec3d(0.0, 2.0, -1.0));
        int hDistanceMod = this.hDistance.getValue();
        for (double distEnemy = AutoAnvil.mc.player.getDistanceToEntity((Entity)this.aimTarget); distEnemy > this.decrease.getValue(); distEnemy -= this.decrease.getValue()) {
            --hDistanceMod;
        }
        int add = (int)(AutoAnvil.mc.player.posY - this.aimTarget.posY);
        if (add > 1) {
            add = 2;
        }
        hDistanceMod += (int)(AutoAnvil.mc.player.posY - this.aimTarget.posY);
        double min_found = Double.MAX_VALUE;
        final double[] coords_blocks_min = { -1.0, -1.0, -1.0 };
        int cor = -1;
        int i = 0;
        for (final Double[] cord_b : this.sur_block) {
            final double[] coords_blocks_temp = { cord_b[0], cord_b[1], cord_b[2] };
            final double distance_now;
            if ((distance_now = AutoAnvil.mc.player.getDistanceSq(new BlockPos((double)cord_b[0], (double)cord_b[1], (double)cord_b[2]))) < min_found) {
                min_found = distance_now;
                cor = i;
            }
            ++i;
        }
        boolean possible = false;
        int incr;
        for (incr = 1; BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + incr, this.enemyCoords[2]) instanceof BlockAir && incr < hDistanceMod; ++incr) {
            if (!this.antiCrystal.getValue()) {
                AutoAnvil.to_place.add(new Vec3d((double)this.model[cor][0], (double)(this.model[cor][1] + incr), (double)this.model[cor][2]));
            }
            else {
                for (int ij = 0; ij < 4; ++ij) {
                    AutoAnvil.to_place.add(new Vec3d((double)this.model[ij][0], (double)(this.model[ij][1] + incr), (double)this.model[ij][2]));
                }
            }
        }
        if (!(BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + incr, this.enemyCoords[2]) instanceof BlockAir)) {
            --incr;
        }
        if (incr >= this.minH.getValue()) {
            possible = true;
        }
        final double yRef = AutoAnvil.to_place.get(AutoAnvil.to_place.size() - 1).yCoord;
        this.anvilBlock = 0;
        final String value = this.anvilPlace.getValue();
        switch (value) {
            case "full": {
                AutoAnvil.to_place.add(new Vec3d(0.0, 3.0, 0.0));
                ++this.anvilBlock;
            }
            case "double": {
                AutoAnvil.to_place.add(new Vec3d(0.0, 2.0, 0.0));
                ++this.anvilBlock;
            }
            case "single": {
                AutoAnvil.to_place.add(new Vec3d(0.0, yRef, 0.0));
                ++this.anvilBlock;
                break;
            }
        }
        return possible;
    }
    
    static {
        AutoAnvil.to_place = new ArrayList<Vec3d>();
    }
}
