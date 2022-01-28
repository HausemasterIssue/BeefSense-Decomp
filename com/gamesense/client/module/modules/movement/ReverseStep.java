



package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.*;
import com.gamesense.client.module.*;
import net.minecraft.entity.*;

public class ReverseStep extends Module
{
    Setting.Double height;
    
    public ReverseStep() {
        super("ReverseStep", Module.Category.Movement);
    }
    
    public void setup() {
        this.height = this.registerDouble("Height", 2.5, 0.5, 2.5);
    }
    
    public void onUpdate() {
        if (ReverseStep.mc.world == null || ReverseStep.mc.player == null || ReverseStep.mc.player.isInWater() || ReverseStep.mc.player.isInLava() || ReverseStep.mc.player.isOnLadder() || ReverseStep.mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (ModuleManager.isModuleEnabled((Class)Speed.class)) {
            return;
        }
        if (ReverseStep.mc.player != null && ReverseStep.mc.player.onGround && !ReverseStep.mc.player.isInWater() && !ReverseStep.mc.player.isOnLadder()) {
            for (double y = 0.0; y < this.height.getValue() + 0.5; y += 0.01) {
                if (!ReverseStep.mc.world.getCollisionBoxes((Entity)ReverseStep.mc.player, ReverseStep.mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                    ReverseStep.mc.player.motionY = -10.0;
                    break;
                }
            }
        }
    }
}
