



package com.gamesense.api.util.player;

import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.world.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.util.math.*;

public class PlayerUtil
{
    private static final Minecraft mc;
    
    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(PlayerUtil.mc.player.posX), Math.floor(PlayerUtil.mc.player.posY), Math.floor(PlayerUtil.mc.player.posZ));
    }
    
    public static EntityPlayer findClosestTarget(double rangeMax, final EntityPlayer aimTarget) {
        rangeMax *= rangeMax;
        final List<EntityPlayer> playerList = (List<EntityPlayer>)PlayerUtil.mc.world.playerEntities;
        EntityPlayer closestTarget = null;
        for (final EntityPlayer entityPlayer : playerList) {
            if (EntityUtil.basicChecksEntity((Entity)entityPlayer)) {
                continue;
            }
            if (aimTarget == null && PlayerUtil.mc.player.getDistanceSqToEntity((Entity)entityPlayer) <= rangeMax) {
                closestTarget = entityPlayer;
            }
            else {
                if (aimTarget == null || PlayerUtil.mc.player.getDistanceSqToEntity((Entity)entityPlayer) > rangeMax || PlayerUtil.mc.player.getDistanceSqToEntity((Entity)entityPlayer) >= PlayerUtil.mc.player.getDistanceSqToEntity((Entity)aimTarget)) {
                    continue;
                }
                closestTarget = entityPlayer;
            }
        }
        return closestTarget;
    }
    
    public static EntityPlayer findClosestTarget() {
        final List<EntityPlayer> playerList = (List<EntityPlayer>)PlayerUtil.mc.world.playerEntities;
        EntityPlayer closestTarget = null;
        for (final EntityPlayer entityPlayer : playerList) {
            if (EntityUtil.basicChecksEntity((Entity)entityPlayer)) {
                continue;
            }
            if (closestTarget == null) {
                closestTarget = entityPlayer;
            }
            else {
                if (PlayerUtil.mc.player.getDistanceSqToEntity((Entity)entityPlayer) >= PlayerUtil.mc.player.getDistanceSqToEntity((Entity)closestTarget)) {
                    continue;
                }
                closestTarget = entityPlayer;
            }
        }
        return closestTarget;
    }
    
    public static EntityPlayer findLookingPlayer(final double rangeMax) {
        final ArrayList<EntityPlayer> listPlayer = new ArrayList<EntityPlayer>();
        for (final EntityPlayer playerSin : PlayerUtil.mc.world.playerEntities) {
            if (EntityUtil.basicChecksEntity((Entity)playerSin)) {
                continue;
            }
            if (PlayerUtil.mc.player.getDistanceToEntity((Entity)playerSin) > rangeMax) {
                continue;
            }
            listPlayer.add(playerSin);
        }
        EntityPlayer target = null;
        final Vec3d positionEyes = PlayerUtil.mc.player.getPositionEyes(PlayerUtil.mc.getRenderPartialTicks());
        final Vec3d rotationEyes = PlayerUtil.mc.player.getLook(PlayerUtil.mc.getRenderPartialTicks());
        final int precision = 2;
        for (int i = 0; i < (int)rangeMax; ++i) {
            for (int j = precision; j > 0; --j) {
                for (final EntityPlayer targetTemp : listPlayer) {
                    final AxisAlignedBB playerBox = targetTemp.getEntityBoundingBox();
                    final double xArray = positionEyes.xCoord + rotationEyes.xCoord * i + rotationEyes.xCoord / j;
                    final double yArray = positionEyes.yCoord + rotationEyes.yCoord * i + rotationEyes.yCoord / j;
                    final double zArray = positionEyes.zCoord + rotationEyes.zCoord * i + rotationEyes.zCoord / j;
                    if (playerBox.maxY >= yArray && playerBox.minY <= yArray && playerBox.maxX >= xArray && playerBox.minX <= xArray && playerBox.maxZ >= zArray && playerBox.minZ <= zArray) {
                        target = targetTemp;
                    }
                }
            }
        }
        return target;
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
