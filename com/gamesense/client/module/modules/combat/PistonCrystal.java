



package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.player.*;
import com.gamesense.client.module.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.entity.item.*;
import java.util.*;
import com.gamesense.api.util.combat.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.network.play.client.*;
import org.apache.logging.log4j.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import com.gamesense.api.util.world.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;

public class PistonCrystal extends Module
{
    Setting.Mode breakType;
    Setting.Mode placeMode;
    Setting.Mode target;
    Setting.Double enemyRange;
    Setting.Double torchRange;
    Setting.Double crystalDeltaBreak;
    Setting.Integer blocksPerTick;
    Setting.Integer startDelay;
    Setting.Integer supBlocksDelay;
    Setting.Integer pistonDelay;
    Setting.Integer crystalDelay;
    Setting.Integer hitDelay;
    Setting.Integer midHitDelay;
    Setting.Integer stuckDetector;
    Setting.Integer maxYincr;
    Setting.Boolean rotate;
    Setting.Boolean blockPlayer;
    Setting.Boolean confirmBreak;
    Setting.Boolean confirmPlace;
    Setting.Boolean allowCheapMode;
    Setting.Boolean betterPlacement;
    Setting.Boolean bypassObsidian;
    Setting.Boolean antiWeakness;
    Setting.Boolean debugMode;
    Setting.Boolean speedMeter;
    Setting.Boolean chatMsg;
    private boolean noMaterials;
    private boolean hasMoved;
    private boolean isSneaking;
    private boolean yUnder;
    private boolean isHole;
    private boolean enoughSpace;
    private boolean redstoneBlockMode;
    private boolean fastModeActive;
    private boolean broken;
    private boolean brokenCrystalBug;
    private boolean brokenRedstoneTorch;
    private boolean stoppedCa;
    private boolean deadPl;
    private boolean rotationPlayerMoved;
    private int oldSlot;
    private int stage;
    private int delayTimeTicks;
    private int stuck;
    private int hitTryTick;
    private int round;
    private int nCrystal;
    private long startTime;
    private long endTime;
    private int[] slot_mat;
    private int[] delayTable;
    private int[] meCoordsInt;
    private int[] enemyCoordsInt;
    private double[] enemyCoordsDouble;
    private structureTemp toPlace;
    int[][] disp_surblock;
    Double[][] sur_block;
    private EntityPlayer aimTarget;
    boolean redstoneAbovePiston;
    
    public PistonCrystal() {
        super("PistonCrystal", Module.Category.Combat);
        this.noMaterials = false;
        this.hasMoved = false;
        this.isSneaking = false;
        this.yUnder = false;
        this.isHole = true;
        this.enoughSpace = true;
        this.redstoneBlockMode = false;
        this.fastModeActive = false;
        this.oldSlot = -1;
        this.stuck = 0;
        this.disp_surblock = new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
        this.sur_block = new Double[4][3];
    }
    
    public void setup() {
        final ArrayList<String> breakTypes = new ArrayList<String>();
        breakTypes.add("Swing");
        breakTypes.add("Packet");
        final ArrayList<String> placeModes = new ArrayList<String>();
        placeModes.add("Torch");
        placeModes.add("Block");
        placeModes.add("Both");
        final ArrayList<String> targetChoose = new ArrayList<String>();
        targetChoose.add("Nearest");
        targetChoose.add("Looking");
        this.breakType = this.registerMode("Type", (List)breakTypes, "Swing");
        this.placeMode = this.registerMode("Place", (List)placeModes, "Torch");
        this.target = this.registerMode("Target", (List)targetChoose, "Nearest");
        this.enemyRange = this.registerDouble("Range", 4.9, 0.0, 6.0);
        this.torchRange = this.registerDouble("Torch Range", 5.5, 0.0, 6.0);
        this.crystalDeltaBreak = this.registerDouble("Center Break", 0.1, 0.0, 0.5);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 20);
        this.supBlocksDelay = this.registerInteger("Surround Delay", 4, 0, 20);
        this.startDelay = this.registerInteger("Start Delay", 4, 0, 20);
        this.pistonDelay = this.registerInteger("Piston Delay", 2, 0, 20);
        this.crystalDelay = this.registerInteger("Crystal Delay", 2, 0, 20);
        this.midHitDelay = this.registerInteger("Mid Hit Delay", 5, 0, 20);
        this.hitDelay = this.registerInteger("Hit Delay", 2, 0, 20);
        this.stuckDetector = this.registerInteger("Stuck Check", 35, 0, 200);
        this.maxYincr = this.registerInteger("Max Y", 3, 0, 5);
        this.blockPlayer = this.registerBoolean("Trap Player", true);
        this.rotate = this.registerBoolean("Rotate", false);
        this.confirmBreak = this.registerBoolean("No Glitch Break", true);
        this.confirmPlace = this.registerBoolean("No Glitch Place", true);
        this.allowCheapMode = this.registerBoolean("Cheap Mode", false);
        this.betterPlacement = this.registerBoolean("Better Place", true);
        this.bypassObsidian = this.registerBoolean("Bypass Obsidian", false);
        this.antiWeakness = this.registerBoolean("Anti Weakness", false);
        this.debugMode = this.registerBoolean("Debug Mode", false);
        this.speedMeter = this.registerBoolean("Speed Meter", false);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
        this.round = 0;
    }
    
    public void onEnable() {
        RotationUtil.ROTATION_UTIL.onEnable();
        this.initValues();
        if (this.getAimTarget()) {
            return;
        }
        this.playerChecks();
    }
    
    private boolean getAimTarget() {
        if (this.target.getValue().equals("Nearest")) {
            this.aimTarget = PlayerUtil.findClosestTarget(this.enemyRange.getValue(), this.aimTarget);
        }
        else {
            this.aimTarget = PlayerUtil.findLookingPlayer(this.enemyRange.getValue());
        }
        if (this.aimTarget == null || !this.target.getValue().equals("Looking")) {
            if (!this.target.getValue().equals("Looking") && this.aimTarget == null) {
                this.disable();
            }
            if (this.aimTarget == null) {
                return true;
            }
        }
        return false;
    }
    
    private void playerChecks() {
        if (this.getMaterialsSlot()) {
            if (this.is_in_hole()) {
                this.enemyCoordsDouble = new double[] { this.aimTarget.posX, this.aimTarget.posY, this.aimTarget.posZ };
                this.enemyCoordsInt = new int[] { (int)this.enemyCoordsDouble[0], (int)this.enemyCoordsDouble[1], (int)this.enemyCoordsDouble[2] };
                this.meCoordsInt = new int[] { (int)PistonCrystal.mc.player.posX, (int)PistonCrystal.mc.player.posY, (int)PistonCrystal.mc.player.posZ };
                this.antiAutoDestruction();
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
    
    private void antiAutoDestruction() {
        if (this.redstoneBlockMode || this.rotate.getValue()) {
            this.betterPlacement.setValue(false);
        }
    }
    
    private void initValues() {
        this.aimTarget = null;
        this.delayTable = new int[] { this.startDelay.getValue(), this.supBlocksDelay.getValue(), this.pistonDelay.getValue(), this.crystalDelay.getValue(), this.hitDelay.getValue() };
        this.toPlace = new structureTemp(0.0, 0, null);
        this.isHole = true;
        final boolean hasMoved = false;
        this.fastModeActive = hasMoved;
        this.redstoneBlockMode = hasMoved;
        this.yUnder = hasMoved;
        this.brokenRedstoneTorch = hasMoved;
        this.brokenCrystalBug = hasMoved;
        this.broken = hasMoved;
        this.deadPl = hasMoved;
        this.rotationPlayerMoved = hasMoved;
        this.hasMoved = hasMoved;
        this.slot_mat = new int[] { -1, -1, -1, -1, -1, -1 };
        final int stage = 0;
        this.stuck = stage;
        this.delayTimeTicks = stage;
        this.stage = stage;
        if (PistonCrystal.mc.player == null) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            printChat("PistonCrystal turned ON!", false);
        }
        this.oldSlot = PistonCrystal.mc.player.inventory.currentItem;
        this.stoppedCa = false;
        if (ModuleManager.isModuleEnabled((Class)AutoCrystalGS.class)) {
            AutoCrystalGS.stopAC = true;
            this.stoppedCa = true;
        }
        if (this.debugMode.getValue() || this.speedMeter.getValue()) {
            printChat("Started pistonCrystal n^" + ++this.round, false);
            this.startTime = System.currentTimeMillis();
            this.nCrystal = 0;
        }
    }
    
    public void onDisable() {
        RotationUtil.ROTATION_UTIL.onDisable();
        if (PistonCrystal.mc.player == null) {
            return;
        }
        if (this.chatMsg.getValue()) {
            String output = "";
            String materialsNeeded = "";
            if (this.aimTarget == null) {
                output = "No target found...";
            }
            else if (this.yUnder) {
                output = String.format("Sorry but you cannot be 2+ blocks under the enemy or %d above...", this.maxYincr.getValue());
            }
            else if (this.noMaterials) {
                output = "No Materials Detected...";
                materialsNeeded = this.getMissingMaterials();
            }
            else if (!this.isHole) {
                output = "The enemy is not in a hole...";
            }
            else if (!this.enoughSpace) {
                output = "Not enough space...";
            }
            else if (this.hasMoved) {
                output = "Out of range...";
            }
            else if (this.deadPl) {
                output = "Enemy is dead, gg! ";
            }
            else if (this.rotationPlayerMoved) {
                output = "You cannot move from your hole if you have rotation on. ";
            }
            printChat(output + "PistonCrystal turned OFF!", true);
            if (!materialsNeeded.equals("")) {
                printChat("Materials missing:" + materialsNeeded, true);
            }
            if (this.stoppedCa) {
                AutoCrystalGS.stopAC = false;
                this.stoppedCa = false;
            }
        }
        if (this.isSneaking) {
            PistonCrystal.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)PistonCrystal.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != PistonCrystal.mc.player.inventory.currentItem && this.oldSlot != -1) {
            PistonCrystal.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
        this.noMaterials = false;
        AutoCrystalGS.stopAC = false;
        if (this.debugMode.getValue() || this.speedMeter.getValue()) {
            printChat("Ended pistonCrystal n^" + this.round, false);
        }
    }
    
    private String getMissingMaterials() {
        final StringBuilder output = new StringBuilder();
        if (this.slot_mat[0] == -1) {
            output.append(" Obsidian");
        }
        if (this.slot_mat[1] == -1) {
            output.append(" Piston");
        }
        if (this.slot_mat[2] == -1) {
            output.append(" Crystals");
        }
        if (this.slot_mat[3] == -1) {
            output.append(" Redstone");
        }
        if (this.antiWeakness.getValue() && this.slot_mat[4] == -1) {
            output.append(" Sword");
        }
        if (this.redstoneBlockMode && this.slot_mat[5] == -1) {
            output.append(" Pick");
        }
        return output.toString();
    }
    
    public void onUpdate() {
        if (PistonCrystal.mc.player == null) {
            this.disable();
            return;
        }
        if (this.delayTimeTicks < this.delayTable[this.stage]) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        RotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
        if (this.enemyCoordsDouble == null || this.aimTarget == null) {
            if (this.aimTarget == null) {
                this.aimTarget = PlayerUtil.findLookingPlayer(this.enemyRange.getValue());
                if (this.aimTarget != null) {
                    this.playerChecks();
                }
            }
            else {
                this.checkVariable();
            }
            return;
        }
        if (this.aimTarget.isDead) {
            this.deadPl = true;
        }
        if (this.rotate.getValue() && (int)PistonCrystal.mc.player.posX != this.meCoordsInt[0] && (int)PistonCrystal.mc.player.posZ != this.meCoordsInt[2]) {
            this.rotationPlayerMoved = true;
        }
        if ((int)this.aimTarget.posX != (int)this.enemyCoordsDouble[0] || (int)this.aimTarget.posZ != (int)this.enemyCoordsDouble[2]) {
            this.hasMoved = true;
        }
        if (this.checkVariable()) {
            return;
        }
        if (this.placeSupport()) {
            switch (this.stage) {
                case 1: {
                    if (this.debugMode.getValue()) {
                        printChat("step 1", false);
                    }
                    if (!this.fastModeActive && !this.breakRedstone()) {
                        break;
                    }
                    if (!this.fastModeActive || this.checkCrystalPlace()) {
                        this.placeBlockThings(this.stage, false);
                        break;
                    }
                    this.stage = 2;
                    break;
                }
                case 2: {
                    if (this.debugMode.getValue()) {
                        printChat("step 2", false);
                    }
                    if (this.fastModeActive || !this.confirmPlace.getValue() || this.checkPistonPlace()) {
                        this.placeBlockThings(this.stage, false);
                        break;
                    }
                    break;
                }
                case 3: {
                    if (this.debugMode.getValue()) {
                        printChat("step 3", false);
                    }
                    if (!this.fastModeActive && this.confirmPlace.getValue() && !this.checkCrystalPlace()) {
                        break;
                    }
                    this.placeBlockThings(this.stage, true);
                    this.hitTryTick = 0;
                    if (this.fastModeActive && !this.checkPistonPlace()) {
                        this.stage = 1;
                        break;
                    }
                    break;
                }
                case 4: {
                    if (this.debugMode.getValue()) {
                        printChat("step 4", false);
                    }
                    this.destroyCrystalAlgo();
                    break;
                }
            }
        }
    }
    
    public void destroyCrystalAlgo() {
        Entity crystal = null;
        for (final Entity t : PistonCrystal.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && (((int)t.posX == this.enemyCoordsInt[0] && ((int)(t.posZ - this.crystalDeltaBreak.getValue()) == this.enemyCoordsInt[2] || (int)(t.posZ + this.crystalDeltaBreak.getValue()) == this.enemyCoordsInt[2])) || ((int)t.posZ == this.enemyCoordsInt[2] && ((int)(t.posX - this.crystalDeltaBreak.getValue()) == this.enemyCoordsInt[0] || (int)(t.posX + this.crystalDeltaBreak.getValue()) == this.enemyCoordsInt[0])))) {
                crystal = t;
            }
        }
        if (this.confirmBreak.getValue() && this.broken && crystal == null) {
            final int n = 0;
            this.stuck = n;
            this.stage = n;
            this.broken = false;
            if ((this.debugMode.getValue() || this.speedMeter.getValue()) && ++this.nCrystal == 3) {
                this.printTimeCrystals();
            }
        }
        if (crystal != null) {
            this.breakCrystalPiston(crystal);
            if (this.confirmBreak.getValue()) {
                this.broken = true;
            }
            else {
                final int n2 = 0;
                this.stuck = n2;
                this.stage = n2;
                if ((this.debugMode.getValue() || this.speedMeter.getValue()) && ++this.nCrystal == 3) {
                    this.printTimeCrystals();
                }
            }
        }
        else if (++this.stuck >= this.stuckDetector.getValue()) {
            if (!this.checkPistonPlace()) {
                final BlockPos crystPos = this.getTargetPos(this.toPlace.supportBlock + 1);
                printChat(String.format("aim: %d %d", crystPos.getX(), crystPos.getZ()), false);
                Entity crystalF = null;
                for (final Entity t2 : PistonCrystal.mc.world.loadedEntityList) {
                    if (t2 instanceof EntityEnderCrystal && (int)(t2.posX - 0.5) == crystPos.getX() && (int)(t2.posZ - 0.5) == crystPos.getZ()) {
                        crystalF = t2;
                    }
                }
                if (this.confirmBreak.getValue() && this.brokenCrystalBug && crystalF == null) {
                    final int n3 = 0;
                    this.stuck = n3;
                    this.stage = n3;
                }
                if (crystalF != null) {
                    this.breakCrystalPiston(crystalF);
                    if (this.confirmBreak.getValue()) {
                        this.brokenCrystalBug = true;
                    }
                    else {
                        final int n4 = 0;
                        this.stuck = n4;
                        this.stage = n4;
                    }
                }
                printChat("Stuck detected: piston not placed", true);
                return;
            }
            boolean found = false;
            for (final Entity t3 : PistonCrystal.mc.world.loadedEntityList) {
                if (t3 instanceof EntityEnderCrystal && (int)t3.posX == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).xCoord && (int)t3.posZ == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).zCoord) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                final BlockPos offsetPosPist = new BlockPos((Vec3d)this.toPlace.to_place.get(this.toPlace.supportBlock + 2));
                final BlockPos pos = new BlockPos(this.aimTarget.getPositionVector()).add(offsetPosPist.getX(), offsetPosPist.getY(), offsetPosPist.getZ());
                if (this.confirmBreak.getValue() && this.brokenRedstoneTorch && BlockUtil.getBlock((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) instanceof BlockAir) {
                    this.stage = 1;
                    this.brokenRedstoneTorch = false;
                }
                else {
                    final EnumFacing side = BlockUtil.getPlaceableSide(pos);
                    if (side != null) {
                        this.breakRedstone();
                        if (this.confirmBreak.getValue()) {
                            this.brokenRedstoneTorch = true;
                        }
                        else {
                            this.stage = 1;
                            if ((this.debugMode.getValue() || this.speedMeter.getValue()) && ++this.nCrystal == 3) {
                                this.printTimeCrystals();
                            }
                        }
                        printChat("Stuck detected: crystal not placed", true);
                    }
                }
            }
            else {
                boolean ext = false;
                for (final Entity t2 : PistonCrystal.mc.world.loadedEntityList) {
                    if (t2 instanceof EntityEnderCrystal && (int)t2.posX == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).xCoord && (int)t2.posZ == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).zCoord) {
                        ext = true;
                        break;
                    }
                }
                if (this.confirmBreak.getValue() && this.brokenCrystalBug && !ext) {
                    final int n5 = 0;
                    this.stuck = n5;
                    this.stage = n5;
                    this.brokenCrystalBug = false;
                }
                if (ext) {
                    this.breakCrystalPiston(crystal);
                    if (this.confirmBreak.getValue()) {
                        this.brokenCrystalBug = true;
                    }
                    else {
                        final int n6 = 0;
                        this.stuck = n6;
                        this.stage = n6;
                    }
                    printChat("Stuck detected: crystal is stuck in the moving piston", true);
                }
            }
        }
    }
    
    private void printTimeCrystals() {
        this.endTime = System.currentTimeMillis();
        printChat("3 crystal, time took: " + (this.endTime - this.startTime), false);
        this.nCrystal = 0;
        this.startTime = System.currentTimeMillis();
    }
    
    private void breakCrystalPiston(final Entity crystal) {
        if (this.hitTryTick++ < this.midHitDelay.getValue()) {
            return;
        }
        this.hitTryTick = 0;
        if (this.antiWeakness.getValue()) {
            PistonCrystal.mc.player.inventory.currentItem = this.slot_mat[4];
        }
        if (this.rotate.getValue()) {
            RotationUtil.ROTATION_UTIL.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, (EntityPlayer)PistonCrystal.mc.player);
        }
        if (this.breakType.getValue().equals("Swing")) {
            CrystalUtil.breakCrystal(crystal);
        }
        else if (this.breakType.getValue().equals("Packet")) {
            try {
                PistonCrystal.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(crystal));
                PistonCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            catch (NullPointerException ex) {}
        }
        if (this.rotate.getValue()) {
            RotationUtil.ROTATION_UTIL.resetRotation();
        }
    }
    
    private boolean breakRedstone() {
        final BlockPos offsetPosPist = new BlockPos((Vec3d)this.toPlace.to_place.get(this.toPlace.supportBlock + 2));
        final BlockPos pos = new BlockPos(this.aimTarget.getPositionVector()).add(offsetPosPist.getX(), offsetPosPist.getY(), offsetPosPist.getZ());
        if (!(BlockUtil.getBlock((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) instanceof BlockAir)) {
            this.breakBlock(pos);
            return false;
        }
        return true;
    }
    
    private void breakBlock(final BlockPos pos) {
        if (this.redstoneBlockMode) {
            PistonCrystal.mc.player.inventory.currentItem = this.slot_mat[5];
        }
        final EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (side != null) {
            if (this.rotate.getValue()) {
                final BlockPos neighbour = pos.offset(side);
                final EnumFacing opposite = side.getOpposite();
                final Vec3d hitVec = new Vec3d((Vec3i)neighbour).addVector(0.5, 1.0, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
                BlockUtil.faceVectorPacketInstant(hitVec, Boolean.valueOf(true));
            }
            PistonCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
            PistonCrystal.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
            PistonCrystal.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
        }
    }
    
    private boolean checkPistonPlace() {
        final BlockPos targetPosPist = this.compactBlockPos(1);
        if (!(BlockUtil.getBlock((double)targetPosPist.getX(), (double)targetPosPist.getY(), (double)targetPosPist.getZ()) instanceof BlockPistonBase)) {
            if (this.stage != 4) {
                --this.stage;
            }
            return false;
        }
        return true;
    }
    
    private boolean checkCrystalPlace() {
        for (final Entity t : PistonCrystal.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && (int)t.posX == (int)(this.aimTarget.posX + this.toPlace.to_place.get(this.toPlace.supportBlock + 1).xCoord) && (int)t.posZ == (int)(this.aimTarget.posZ + this.toPlace.to_place.get(this.toPlace.supportBlock + 1).zCoord)) {
                return true;
            }
        }
        --this.stage;
        return false;
    }
    
    private boolean placeSupport() {
        int checksDone = 0;
        int blockDone = 0;
        if (this.toPlace.supportBlock > 0) {
            do {
                final BlockPos targetPos = this.getTargetPos(checksDone);
                if (this.placeBlock(targetPos, 0, 0.0, 0.0, 1.0, false) && ++blockDone == this.blocksPerTick.getValue()) {
                    return false;
                }
            } while (++checksDone != this.toPlace.supportBlock);
        }
        this.stage = ((this.stage == 0) ? 1 : this.stage);
        return true;
    }
    
    private boolean placeBlock(final BlockPos pos, final int step, final double offsetX, final double offsetZ, final double offsetY, final boolean redstone) {
        final Block block = PistonCrystal.mc.world.getBlockState(pos).getBlock();
        EnumFacing side;
        if (redstone && this.redstoneAbovePiston) {
            side = BlockUtil.getPlaceableSideExlude(pos, EnumFacing.DOWN);
        }
        else {
            side = BlockUtil.getPlaceableSide(pos);
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
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).addVector(0.5 + offsetX, offsetY, 0.5 + offsetZ).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = PistonCrystal.mc.world.getBlockState(neighbour).getBlock();
        try {
            if (this.slot_mat[step] != 11 && PistonCrystal.mc.player.inventory.getStackInSlot(this.slot_mat[step]) == ItemStack.field_190927_a) {
                this.noMaterials = true;
                return false;
            }
            if (PistonCrystal.mc.player.inventory.currentItem != this.slot_mat[step]) {
                PistonCrystal.mc.player.inventory.currentItem = ((this.slot_mat[step] == 11) ? PistonCrystal.mc.player.inventory.currentItem : this.slot_mat[step]);
            }
        }
        catch (Exception e) {
            printChat("Fatal Error during the creation of the structure. Please, report this bug in the discor's server", true);
            final Logger LOGGER = LogManager.getLogger("GameSense");
            LOGGER.error("[PistonCrystal] error during the creation of the structure.");
            if (e.getMessage() != null) {
                LOGGER.error("[PistonCrystal] error message: " + e.getClass().getName() + " " + e.getMessage());
            }
            else {
                LOGGER.error("[PistonCrystal] cannot find the cause");
            }
            final int i5 = 0;
            if (e.getStackTrace().length != 0) {
                LOGGER.error("[PistonCrystal] StackTrace Start");
                for (final StackTraceElement errorMess : e.getStackTrace()) {
                    LOGGER.error("[PistonCrystal] " + errorMess.toString());
                }
                LOGGER.error("[PistonCrystal] StackTrace End");
            }
            printChat(Integer.toString(step), true);
            this.disable();
        }
        if ((!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            PistonCrystal.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)PistonCrystal.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        if (this.rotate.getValue() || step == 1) {
            Vec3d positionHit = hitVec;
            if (!this.rotate.getValue() && step == 1) {
                positionHit = new Vec3d(PistonCrystal.mc.player.posX + offsetX, PistonCrystal.mc.player.posY + ((offsetY == -1.0) ? offsetY : 0.0), PistonCrystal.mc.player.posZ + offsetZ);
            }
            BlockUtil.faceVectorPacketInstant(positionHit, Boolean.valueOf(true));
        }
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (this.slot_mat[step] == 11) {
            handSwing = EnumHand.OFF_HAND;
        }
        PistonCrystal.mc.playerController.processRightClickBlock(PistonCrystal.mc.player, PistonCrystal.mc.world, neighbour, opposite, hitVec, handSwing);
        PistonCrystal.mc.player.swingArm(handSwing);
        return true;
    }
    
    public void placeBlockThings(final int step, final boolean redstone) {
        final BlockPos targetPos = this.compactBlockPos(step);
        this.placeBlock(targetPos, step, this.toPlace.offsetX, this.toPlace.offsetZ, this.toPlace.offsetY, redstone);
        ++this.stage;
    }
    
    public BlockPos compactBlockPos(final int step) {
        final BlockPos offsetPos = new BlockPos((Vec3d)this.toPlace.to_place.get(this.toPlace.supportBlock + step - 1));
        return new BlockPos(this.enemyCoordsDouble[0] + offsetPos.getX(), this.enemyCoordsDouble[1] + offsetPos.getY(), this.enemyCoordsDouble[2] + offsetPos.getZ());
    }
    
    private BlockPos getTargetPos(final int idx) {
        final BlockPos offsetPos = new BlockPos((Vec3d)this.toPlace.to_place.get(idx));
        return new BlockPos(this.enemyCoordsDouble[0] + offsetPos.getX(), this.enemyCoordsDouble[1] + offsetPos.getY(), this.enemyCoordsDouble[2] + offsetPos.getZ());
    }
    
    private boolean checkVariable() {
        if (this.noMaterials || !this.isHole || !this.enoughSpace || this.hasMoved || this.deadPl || this.rotationPlayerMoved) {
            this.disable();
            return true;
        }
        return false;
    }
    
    private boolean createStructure() {
        final structureTemp addedStructure = new structureTemp(Double.MAX_VALUE, 0, null);
        try {
            if (this.meCoordsInt[1] - this.enemyCoordsInt[1] > -1 && this.meCoordsInt[1] - this.enemyCoordsInt[1] <= this.maxYincr.getValue()) {
                for (int startH = 1; startH >= 0; --startH) {
                    if (addedStructure.to_place == null) {
                        int incr = 0;
                        final List<Vec3d> highSup = new ArrayList<Vec3d>();
                        while (this.meCoordsInt[1] > this.enemyCoordsInt[1] + incr) {
                            ++incr;
                            for (final int[] cordSupport : this.disp_surblock) {
                                highSup.add(new Vec3d((double)cordSupport[0], (double)incr, (double)cordSupport[2]));
                            }
                        }
                        incr += startH;
                        int i = -1;
                        for (final Double[] cord_b : this.sur_block) {
                            ++i;
                            final double[] crystalCordsAbs = { cord_b[0], cord_b[1] + incr, cord_b[2] };
                            final int[] crystalCordsRel = { this.disp_surblock[i][0], this.disp_surblock[i][1] + incr, this.disp_surblock[i][2] };
                            Label_3130: {
                                final double distanceNowCrystal;
                                if ((distanceNowCrystal = PistonCrystal.mc.player.getDistance(crystalCordsAbs[0], crystalCordsAbs[1], crystalCordsAbs[2])) < addedStructure.distance) {
                                    if (BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1], crystalCordsAbs[2]) instanceof BlockAir) {
                                        if (BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] + 1.0, crystalCordsAbs[2]) instanceof BlockAir) {
                                            if (!someoneInCoords(crystalCordsAbs[0], crystalCordsAbs[2])) {
                                                if (BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] - 1.0, crystalCordsAbs[2]) instanceof BlockObsidian || BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] - 1.0, crystalCordsAbs[2]).getRegistryName().getResourcePath().equals("bedrock")) {
                                                    double[] pistonCordAbs = new double[3];
                                                    int[] pistonCordRel = new int[3];
                                                    if (this.rotate.getValue() || !this.betterPlacement.getValue()) {
                                                        pistonCordAbs = new double[] { crystalCordsAbs[0] + this.disp_surblock[i][0], crystalCordsAbs[1], crystalCordsAbs[2] + this.disp_surblock[i][2] };
                                                        final Block tempBlock;
                                                        if ((tempBlock = BlockUtil.getBlock(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2])) instanceof BlockPistonBase == tempBlock instanceof BlockAir) {
                                                            break Label_3130;
                                                        }
                                                        if (someoneInCoords(pistonCordAbs[0], pistonCordAbs[2])) {
                                                            break Label_3130;
                                                        }
                                                        pistonCordRel = new int[] { crystalCordsRel[0] * 2, crystalCordsRel[1], crystalCordsRel[2] * 2 };
                                                    }
                                                    else {
                                                        double distancePist = Double.MAX_VALUE;
                                                        for (final int[] disp : this.disp_surblock) {
                                                            final BlockPos blockPiston = new BlockPos(crystalCordsAbs[0] + disp[0], crystalCordsAbs[1], crystalCordsAbs[2] + disp[2]);
                                                            final double distanceNowPiston;
                                                            if ((distanceNowPiston = PistonCrystal.mc.player.getDistanceSqToCenter(blockPiston)) <= distancePist) {
                                                                if (BlockUtil.getBlock((double)blockPiston.getX(), (double)blockPiston.getY(), (double)blockPiston.getZ()) instanceof BlockPistonBase || BlockUtil.getBlock((double)blockPiston.getX(), (double)blockPiston.getY(), (double)blockPiston.getZ()) instanceof BlockAir) {
                                                                    if (!someoneInCoords(crystalCordsAbs[0] + disp[0], crystalCordsAbs[2] + disp[2])) {
                                                                        if (BlockUtil.getBlock((double)(blockPiston.getX() - crystalCordsRel[0]), (double)blockPiston.getY(), (double)(blockPiston.getZ() - crystalCordsRel[2])) instanceof BlockAir) {
                                                                            distancePist = distanceNowPiston;
                                                                            pistonCordAbs = new double[] { crystalCordsAbs[0] + disp[0], crystalCordsAbs[1], crystalCordsAbs[2] + disp[2] };
                                                                            pistonCordRel = new int[] { crystalCordsRel[0] + disp[0], crystalCordsRel[1], crystalCordsRel[2] + disp[2] };
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (distancePist == Double.MAX_VALUE) {
                                                            break Label_3130;
                                                        }
                                                    }
                                                    if (this.rotate.getValue()) {
                                                        final int[] pistonCordInt = { (int)pistonCordAbs[0], (int)pistonCordAbs[1], (int)pistonCordAbs[2] };
                                                        boolean behindBol = false;
                                                        for (final int checkBehind : new int[] { 0, 2 }) {
                                                            if (this.meCoordsInt[checkBehind] == pistonCordInt[checkBehind]) {
                                                                final int idx = (checkBehind == 2) ? 0 : 2;
                                                                if (pistonCordInt[idx] >= this.enemyCoordsInt[idx] == this.meCoordsInt[idx] >= this.enemyCoordsInt[idx]) {
                                                                    behindBol = true;
                                                                }
                                                            }
                                                        }
                                                        if (!behindBol && Math.abs(this.meCoordsInt[0] - this.enemyCoordsInt[0]) == 2 && Math.abs(this.meCoordsInt[2] - this.enemyCoordsInt[2]) == 2 && ((this.meCoordsInt[0] == pistonCordInt[0] && Math.abs(this.meCoordsInt[2] - pistonCordInt[2]) >= 2) || (this.meCoordsInt[2] == pistonCordInt[2] && Math.abs(this.meCoordsInt[0] - pistonCordInt[0]) >= 2))) {
                                                            behindBol = true;
                                                        }
                                                        if ((!behindBol && Math.abs(this.meCoordsInt[0] - this.enemyCoordsInt[0]) > 2 && this.meCoordsInt[2] != this.enemyCoordsInt[2]) || (Math.abs(this.meCoordsInt[2] - this.enemyCoordsInt[2]) > 2 && this.meCoordsInt[0] != this.enemyCoordsInt[0])) {
                                                            behindBol = true;
                                                        }
                                                        if (behindBol) {
                                                            break Label_3130;
                                                        }
                                                    }
                                                    double[] redstoneCoordsAbs = new double[3];
                                                    int[] redstoneCoordsRel = new int[3];
                                                    double minFound = Double.MAX_VALUE;
                                                    double minNow = -1.0;
                                                    boolean foundOne = true;
                                                    for (final int[] pos : this.disp_surblock) {
                                                        final double[] torchCoords = { pistonCordAbs[0] + pos[0], pistonCordAbs[1], pistonCordAbs[2] + pos[2] };
                                                        if ((minNow = PistonCrystal.mc.player.getDistance(torchCoords[0], torchCoords[1], torchCoords[2])) < minFound) {
                                                            if (!this.redstoneBlockMode || pos[0] == crystalCordsRel[0]) {
                                                                if (!someoneInCoords(torchCoords[0], torchCoords[2]) && (BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockRedstoneTorch || BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockAir)) {
                                                                    if ((int)torchCoords[0] != (int)crystalCordsAbs[0] || (int)torchCoords[2] != (int)crystalCordsAbs[2]) {
                                                                        boolean torchFront = false;
                                                                        for (final int part : new int[] { 0, 2 }) {
                                                                            final int contPart = (part == 0) ? 2 : 0;
                                                                            if ((int)torchCoords[contPart] == (int)pistonCordAbs[contPart] && (int)torchCoords[part] == this.enemyCoordsInt[part]) {
                                                                                torchFront = true;
                                                                            }
                                                                        }
                                                                        if (!torchFront) {
                                                                            redstoneCoordsAbs = new double[] { torchCoords[0], torchCoords[1], torchCoords[2] };
                                                                            redstoneCoordsRel = new int[] { pistonCordRel[0] + pos[0], pistonCordRel[1], pistonCordRel[2] + pos[2] };
                                                                            foundOne = false;
                                                                            minFound = minNow;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    this.redstoneAbovePiston = false;
                                                    if (foundOne) {
                                                        if (this.redstoneBlockMode || !(BlockUtil.getBlock(pistonCordAbs[0], pistonCordAbs[1] + 1.0, pistonCordAbs[2]) instanceof BlockAir)) {
                                                            break Label_3130;
                                                        }
                                                        redstoneCoordsAbs = new double[] { pistonCordAbs[0], pistonCordAbs[1] + 1.0, pistonCordAbs[2] };
                                                        redstoneCoordsRel = new int[] { pistonCordRel[0], pistonCordRel[1] + 1, pistonCordRel[2] };
                                                        this.redstoneAbovePiston = true;
                                                    }
                                                    if (this.redstoneBlockMode && this.allowCheapMode.getValue() && (BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]) instanceof BlockAir || BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]).unlocalizedName.equals("blockRedstone"))) {
                                                        pistonCordAbs = new double[] { redstoneCoordsAbs[0], redstoneCoordsAbs[1], redstoneCoordsAbs[2] };
                                                        pistonCordRel = new int[] { redstoneCoordsRel[0], redstoneCoordsRel[1], redstoneCoordsRel[2] };
                                                        redstoneCoordsAbs = new double[] { redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsRel[2] };
                                                        redstoneCoordsRel = new int[] { redstoneCoordsRel[0], redstoneCoordsRel[1] - 1, redstoneCoordsRel[2] };
                                                        this.fastModeActive = true;
                                                    }
                                                    final List<Vec3d> toPlaceTemp = new ArrayList<Vec3d>();
                                                    int supportBlock = 0;
                                                    if (BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] - 1.0, crystalCordsAbs[2]) instanceof BlockAir) {
                                                        toPlaceTemp.add(new Vec3d((double)crystalCordsRel[0], (double)(crystalCordsRel[1] - 1), (double)crystalCordsRel[2]));
                                                        ++supportBlock;
                                                    }
                                                    if (!this.fastModeActive && BlockUtil.getBlock(pistonCordAbs[0], pistonCordAbs[1] - 1.0, pistonCordAbs[2]) instanceof BlockAir) {
                                                        toPlaceTemp.add(new Vec3d((double)pistonCordRel[0], (double)(pistonCordRel[1] - 1), (double)pistonCordRel[2]));
                                                        ++supportBlock;
                                                    }
                                                    if (!this.fastModeActive) {
                                                        if (this.redstoneAbovePiston) {
                                                            int[] toAdd;
                                                            if (this.enemyCoordsInt[0] == (int)pistonCordAbs[0] && this.enemyCoordsInt[2] == (int)pistonCordAbs[2]) {
                                                                toAdd = new int[] { crystalCordsRel[0], 0, 0 };
                                                            }
                                                            else {
                                                                toAdd = new int[] { crystalCordsRel[0], 0, crystalCordsRel[2] };
                                                            }
                                                            for (int hight = 0; hight < 2; ++hight) {
                                                                if (BlockUtil.getBlock(pistonCordAbs[0] + toAdd[0], pistonCordAbs[1] + hight, pistonCordAbs[2] + toAdd[2]) instanceof BlockAir) {
                                                                    toPlaceTemp.add(new Vec3d((double)(pistonCordRel[0] + toAdd[0]), (double)(pistonCordRel[1] + hight), (double)(pistonCordRel[2] + toAdd[2])));
                                                                    ++supportBlock;
                                                                }
                                                            }
                                                        }
                                                        else if (!this.redstoneBlockMode && BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]) instanceof BlockAir) {
                                                            toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)(redstoneCoordsRel[1] - 1), (double)redstoneCoordsRel[2]));
                                                            ++supportBlock;
                                                        }
                                                    }
                                                    else if (BlockUtil.getBlock(redstoneCoordsAbs[0] - crystalCordsRel[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2] - crystalCordsRel[2]) instanceof BlockAir) {
                                                        toPlaceTemp.add(new Vec3d((double)(redstoneCoordsRel[0] - crystalCordsRel[0]), (double)redstoneCoordsRel[1], (double)(redstoneCoordsRel[2] - crystalCordsRel[2])));
                                                        ++supportBlock;
                                                    }
                                                    toPlaceTemp.add(new Vec3d((double)pistonCordRel[0], (double)pistonCordRel[1], (double)pistonCordRel[2]));
                                                    toPlaceTemp.add(new Vec3d((double)crystalCordsRel[0], (double)crystalCordsRel[1], (double)crystalCordsRel[2]));
                                                    toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)redstoneCoordsRel[1], (double)redstoneCoordsRel[2]));
                                                    if (incr > 1) {
                                                        for (int i2 = 0; i2 < highSup.size(); ++i2) {
                                                            toPlaceTemp.add(0, highSup.get(i2));
                                                            ++supportBlock;
                                                        }
                                                    }
                                                    float offsetX;
                                                    float offsetZ;
                                                    if (this.disp_surblock[i][0] != 0) {
                                                        offsetX = (this.rotate.getValue() ? (this.disp_surblock[i][0] / 2.0f) : ((float)this.disp_surblock[i][0]));
                                                        if (this.rotate.getValue()) {
                                                            if (PistonCrystal.mc.player.getDistanceSq(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2] + 0.5) > PistonCrystal.mc.player.getDistanceSq(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2] - 0.5)) {
                                                                offsetZ = -0.5f;
                                                            }
                                                            else {
                                                                offsetZ = 0.5f;
                                                            }
                                                        }
                                                        else {
                                                            offsetZ = (float)this.disp_surblock[i][2];
                                                        }
                                                    }
                                                    else {
                                                        offsetZ = (this.rotate.getValue() ? (this.disp_surblock[i][2] / 2.0f) : ((float)this.disp_surblock[i][2]));
                                                        if (this.rotate.getValue()) {
                                                            if (PistonCrystal.mc.player.getDistanceSq(pistonCordAbs[0] + 0.5, pistonCordAbs[1], pistonCordAbs[2]) > PistonCrystal.mc.player.getDistanceSq(pistonCordAbs[0] - 0.5, pistonCordAbs[1], pistonCordAbs[2])) {
                                                                offsetX = -0.5f;
                                                            }
                                                            else {
                                                                offsetX = 0.5f;
                                                            }
                                                        }
                                                        else {
                                                            offsetX = (float)this.disp_surblock[i][0];
                                                        }
                                                    }
                                                    final float offsetY = (this.meCoordsInt[1] - this.enemyCoordsInt[1] == -1) ? 0.0f : 1.0f;
                                                    addedStructure.replaceValues(distanceNowCrystal, supportBlock, toPlaceTemp, -1, offsetX, offsetZ, offsetY);
                                                    if (this.blockPlayer.getValue()) {
                                                        final Vec3d valuesStart = addedStructure.to_place.get(addedStructure.supportBlock + 1);
                                                        final int[] valueBegin = { (int)(-valuesStart.xCoord), (int)valuesStart.yCoord, (int)(-valuesStart.zCoord) };
                                                        if (!this.bypassObsidian.getValue() || (int)PistonCrystal.mc.player.posY == this.enemyCoordsInt[1]) {
                                                            addedStructure.to_place.add(0, new Vec3d(0.0, (double)(incr + 1), 0.0));
                                                            addedStructure.to_place.add(0, new Vec3d((double)valueBegin[0], (double)(incr + 1), (double)valueBegin[2]));
                                                            addedStructure.to_place.add(0, new Vec3d((double)valueBegin[0], (double)incr, (double)valueBegin[2]));
                                                            final structureTemp structureTemp = addedStructure;
                                                            structureTemp.supportBlock += 3;
                                                        }
                                                        else {
                                                            addedStructure.to_place.add(0, new Vec3d(0.0, (double)incr, 0.0));
                                                            addedStructure.to_place.add(0, new Vec3d((double)valueBegin[0], (double)incr, (double)valueBegin[2]));
                                                            final structureTemp structureTemp2 = addedStructure;
                                                            structureTemp2.supportBlock += 2;
                                                        }
                                                    }
                                                    this.toPlace = addedStructure;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                this.yUnder = true;
            }
        }
        catch (Exception e) {
            printChat("Fatal Error during the creation of the structure. Please, report this bug in the discor's server", true);
            final Logger LOGGER = LogManager.getLogger("GameSense");
            LOGGER.error("[PistonCrystal] error during the creation of the structure.");
            if (e.getMessage() != null) {
                LOGGER.error("[PistonCrystal] error message: " + e.getClass().getName() + " " + e.getMessage());
            }
            else {
                LOGGER.error("[PistonCrystal] cannot find the cause");
            }
            int i3 = 0;
            if (e.getStackTrace().length != 0) {
                LOGGER.error("[PistonCrystal] StackTrace Start");
                for (final StackTraceElement errorMess : e.getStackTrace()) {
                    LOGGER.error("[PistonCrystal] " + errorMess.toString());
                }
                LOGGER.error("[PistonCrystal] StackTrace End");
            }
            if (this.aimTarget != null) {
                LOGGER.error("[PistonCrystal] closest target is not null");
            }
            else {
                LOGGER.error("[PistonCrystal] closest target is null somehow");
            }
            for (final Double[] cord_b2 : this.sur_block) {
                if (cord_b2 != null) {
                    LOGGER.error("[PistonCrystal] " + i3 + " is not null");
                }
                else {
                    LOGGER.error("[PistonCrystal] " + i3 + " is null");
                }
                ++i3;
            }
        }
        if (this.debugMode.getValue() && addedStructure.to_place != null) {
            printChat("Skeleton structure:", false);
            for (final Vec3d parte : addedStructure.to_place) {
                printChat(String.format("%f %f %f", parte.xCoord, parte.yCoord, parte.zCoord), false);
            }
        }
        return addedStructure.to_place != null;
    }
    
    public static boolean someoneInCoords(final double x, final double z) {
        final int xCheck = (int)x;
        final int zCheck = (int)z;
        final List<EntityPlayer> playerList = (List<EntityPlayer>)PistonCrystal.mc.world.playerEntities;
        for (final EntityPlayer player : playerList) {
            if ((int)player.posX == xCheck && (int)player.posZ == zCheck) {
                return true;
            }
        }
        return false;
    }
    
    private boolean getMaterialsSlot() {
        if (PistonCrystal.mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            this.slot_mat[2] = 11;
        }
        if (this.placeMode.getValue().equals("Block")) {
            this.redstoneBlockMode = true;
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = PistonCrystal.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.field_190927_a) {
                if (this.slot_mat[2] == -1 && stack.getItem() instanceof ItemEndCrystal) {
                    this.slot_mat[2] = i;
                }
                else if (this.antiWeakness.getValue() && stack.getItem() instanceof ItemSword) {
                    this.slot_mat[4] = i;
                }
                else if (stack.getItem() instanceof ItemPickaxe) {
                    this.slot_mat[5] = i;
                }
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockObsidian) {
                        this.slot_mat[0] = i;
                    }
                    else if (block instanceof BlockPistonBase) {
                        this.slot_mat[1] = i;
                    }
                    else if (!this.placeMode.getValue().equals("Block") && block instanceof BlockRedstoneTorch) {
                        this.slot_mat[3] = i;
                        this.redstoneBlockMode = false;
                    }
                    else if (!this.placeMode.getValue().equals("Torch") && block.unlocalizedName.equals("blockRedstone")) {
                        this.slot_mat[3] = i;
                        this.redstoneBlockMode = true;
                    }
                }
            }
        }
        if (!this.redstoneBlockMode) {
            this.slot_mat[5] = -1;
        }
        int count = 0;
        for (final int val : this.slot_mat) {
            if (val != -1) {
                ++count;
            }
        }
        if (this.debugMode.getValue()) {
            printChat(String.format("%d %d %d %d %d %d", this.slot_mat[0], this.slot_mat[1], this.slot_mat[2], this.slot_mat[3], this.slot_mat[4], this.slot_mat[5]), false);
        }
        return count >= 4 + (this.antiWeakness.getValue() ? 1 : 0) + (this.redstoneBlockMode ? 1 : 0);
    }
    
    private boolean is_in_hole() {
        this.sur_block = new Double[][] { { this.aimTarget.posX + 1.0, this.aimTarget.posY, this.aimTarget.posZ }, { this.aimTarget.posX - 1.0, this.aimTarget.posY, this.aimTarget.posZ }, { this.aimTarget.posX, this.aimTarget.posY, this.aimTarget.posZ + 1.0 }, { this.aimTarget.posX, this.aimTarget.posY, this.aimTarget.posZ - 1.0 } };
        return HoleUtil.isHole(EntityUtil.getPosition((Entity)this.aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }
    
    public static void printChat(final String text, final Boolean error) {
        MessageBus.sendClientPrefixMessage((error ? ColorMain.getDisabledColor() : ColorMain.getEnabledColor()) + text);
    }
    
    private static class structureTemp
    {
        public double distance;
        public int supportBlock;
        public List<Vec3d> to_place;
        public int direction;
        public float offsetX;
        public float offsetY;
        public float offsetZ;
        
        public structureTemp(final double distance, final int supportBlock, final List<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }
        
        public void replaceValues(final double distance, final int supportBlock, final List<Vec3d> to_place, final int direction, final float offsetX, final float offsetZ, final float offsetY) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = direction;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
            this.offsetY = offsetY;
        }
    }
}
