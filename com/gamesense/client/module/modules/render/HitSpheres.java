



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.event.events.*;
import net.minecraft.entity.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.api.util.render.*;
import java.util.*;

public class HitSpheres extends Module
{
    public HitSpheres() {
        super("HitSpheres", Module.Category.Render);
    }
    
    public void onWorldRender(final RenderEvent event) {
        for (final Entity entity : HitSpheres.mc.world.loadedEntityList) {
            if (entity instanceof EntityPlayerSP) {
                continue;
            }
            if (!(entity instanceof EntityPlayer)) {
                continue;
            }
            final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * HitSpheres.mc.timer.field_194147_b;
            final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * HitSpheres.mc.timer.field_194147_b;
            final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * HitSpheres.mc.timer.field_194147_b;
            if (Friends.isFriend(entity.getName())) {
                new GSColor(38, 38, 255).glColor();
            }
            else if (HitSpheres.mc.player.getDistanceSqToEntity(entity) >= 64.0) {
                new GSColor(0, 255, 0).glColor();
            }
            else {
                new GSColor(255, (int)(HitSpheres.mc.player.getDistanceToEntity(entity) * 255.0f / 150.0f), 0).glColor();
            }
            RenderUtil.drawSphere(posX, posY, posZ, 6.0f, 20, 15);
        }
    }
}
