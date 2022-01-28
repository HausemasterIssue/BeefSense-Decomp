



package com.gamesense.api.util.player;

import net.minecraft.client.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import com.gamesense.api.util.world.*;
import net.minecraft.util.math.*;
import com.gamesense.client.module.modules.combat.*;
import com.gamesense.client.module.*;
import net.minecraft.util.*;

public class PlacementUtil
{
    private static final Minecraft mc;
    private static int placementConnections;
    private static boolean isSneaking;
    
    public static void onEnable() {
        ++PlacementUtil.placementConnections;
    }
    
    public static void onDisable() {
        --PlacementUtil.placementConnections;
        if (PlacementUtil.placementConnections == 0 && PlacementUtil.isSneaking) {
            PlacementUtil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)PlacementUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            PlacementUtil.isSneaking = false;
        }
    }
    
    public static boolean placeBlock(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final Class<? extends Block> blockToPlace) {
        final int oldSlot = PlacementUtil.mc.player.inventory.currentItem;
        final int newSlot = InventoryUtil.findFirstBlockSlot((Class)blockToPlace, 0, 8);
        if (newSlot == -1) {
            return false;
        }
        PlacementUtil.mc.player.inventory.currentItem = newSlot;
        final boolean output = place(blockPos, hand, rotate);
        PlacementUtil.mc.player.inventory.currentItem = oldSlot;
        return output;
    }
    
    public static boolean placeItem(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final Class<? extends Item> itemToPlace) {
        final int oldSlot = PlacementUtil.mc.player.inventory.currentItem;
        final int newSlot = InventoryUtil.findFirstItemSlot((Class)itemToPlace, 0, 8);
        if (newSlot == -1) {
            return false;
        }
        PlacementUtil.mc.player.inventory.currentItem = newSlot;
        final boolean output = place(blockPos, hand, rotate);
        PlacementUtil.mc.player.inventory.currentItem = oldSlot;
        return output;
    }
    
    public static boolean place(final BlockPos blockPos, final EnumHand hand, final boolean rotate) {
        final Block block = PlacementUtil.mc.world.getBlockState(blockPos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        final EnumFacing side = BlockUtil.getPlaceableSide(blockPos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = blockPos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = PlacementUtil.mc.world.getBlockState(neighbour).getBlock();
        if ((!PlacementUtil.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            PlacementUtil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)PlacementUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            PlacementUtil.isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled(AutoCrystalGS.class)) {
            AutoCrystalGS.stopAC = true;
            stoppedAC = true;
        }
        if (rotate) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        final EnumActionResult action = PlacementUtil.mc.playerController.processRightClickBlock(PlacementUtil.mc.player, PlacementUtil.mc.world, neighbour, opposite, hitVec, hand);
        if (action == EnumActionResult.SUCCESS) {
            PlacementUtil.mc.player.swingArm(hand);
            PlacementUtil.mc.rightClickDelayTimer = 4;
        }
        if (stoppedAC) {
            AutoCrystalGS.stopAC = false;
        }
        return action == EnumActionResult.SUCCESS;
    }
    
    static {
        mc = Minecraft.getMinecraft();
        PlacementUtil.placementConnections = 0;
        PlacementUtil.isSneaking = false;
    }
}
