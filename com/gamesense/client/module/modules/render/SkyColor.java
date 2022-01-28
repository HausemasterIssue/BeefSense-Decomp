



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.util.render.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.common.*;

public class SkyColor extends Module
{
    public static Setting.ColorSetting color;
    public static Setting.Boolean fog;
    
    public SkyColor() {
        super("SkyColor", Module.Category.Render);
    }
    
    public void setup() {
        SkyColor.fog = this.registerBoolean("Fog", true);
        SkyColor.color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
    }
    
    @SubscribeEvent
    public void onFogColorRender(final EntityViewRenderEvent.FogColors event) {
        final GSColor color = SkyColor.color.getValue();
        event.setRed(color.getRed() / 255.0f);
        event.setGreen(color.getGreen() / 255.0f);
        event.setBlue(color.getBlue() / 255.0f);
    }
    
    @SubscribeEvent
    public void fog(final EntityViewRenderEvent.FogDensity event) {
        if (!SkyColor.fog.getValue()) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }
    
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
    
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }
}
