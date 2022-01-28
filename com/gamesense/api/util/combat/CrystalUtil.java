



package com.gamesense.api.util.combat;

import net.minecraft.client.*;
import net.minecraft.init.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import com.gamesense.api.util.player.*;
import com.gamesense.api.util.world.*;
import java.util.stream.*;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

public class CrystalUtil
{
    private static final Minecraft mc;
    
    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean mode) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        if (!mode) {
            return (CrystalUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || CrystalUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && CrystalUtil.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && CrystalUtil.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && CrystalUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost)).isEmpty() && CrystalUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        }
        return (CrystalUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || CrystalUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && CrystalUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost)).isEmpty() && CrystalUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }
    
    public static List<BlockPos> findCrystalBlocks(final float placeRange, final boolean mode) {
        final NonNullList<BlockPos> positions = (NonNullList<BlockPos>)NonNullList.func_191196_a();
        positions.addAll((Collection)EntityUtil.getSphere(PlayerUtil.getPlayerPos(), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, mode)).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        return (List<BlockPos>)positions;
    }
    
    public static void breakCrystal(final Entity crystal) {
        CrystalUtil.mc.playerController.attackEntity((EntityPlayer)CrystalUtil.mc.player, crystal);
        CrystalUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
