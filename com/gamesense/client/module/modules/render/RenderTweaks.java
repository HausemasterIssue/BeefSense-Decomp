



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;

public class RenderTweaks extends Module
{
    public Setting.Boolean viewClip;
    Setting.Boolean nekoAnimation;
    Setting.Boolean lowOffhand;
    Setting.Boolean fovChanger;
    Setting.Double lowOffhandSlider;
    Setting.Integer fovChangerSlider;
    ItemRenderer itemRenderer;
    private float oldFOV;
    
    public RenderTweaks() {
        super("RenderTweaks", Module.Category.Render);
        this.itemRenderer = RenderTweaks.mc.entityRenderer.itemRenderer;
    }
    
    public void setup() {
        this.viewClip = this.registerBoolean("View Clip", false);
        this.nekoAnimation = this.registerBoolean("Neko Animation", false);
        this.lowOffhand = this.registerBoolean("Low Offhand", false);
        this.lowOffhandSlider = this.registerDouble("Offhand Height", 1.0, 0.1, 1.0);
        this.fovChanger = this.registerBoolean("FOV", false);
        this.fovChangerSlider = this.registerInteger("FOV Slider", 90, 70, 200);
    }
    
    public void onUpdate() {
        if (this.nekoAnimation.getValue() && RenderTweaks.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && RenderTweaks.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            RenderTweaks.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            RenderTweaks.mc.entityRenderer.itemRenderer.itemStackMainHand = RenderTweaks.mc.player.getHeldItemMainhand();
        }
        if (this.lowOffhand.getValue()) {
            this.itemRenderer.equippedProgressOffHand = (float)this.lowOffhandSlider.getValue();
        }
        if (this.fovChanger.getValue()) {
            RenderTweaks.mc.gameSettings.fovSetting = (float)this.fovChangerSlider.getValue();
        }
        if (!this.fovChanger.getValue()) {
            RenderTweaks.mc.gameSettings.fovSetting = this.oldFOV;
        }
    }
    
    public void onEnable() {
        this.oldFOV = RenderTweaks.mc.gameSettings.fovSetting;
    }
    
    public void onDisable() {
        RenderTweaks.mc.gameSettings.fovSetting = this.oldFOV;
    }
}
