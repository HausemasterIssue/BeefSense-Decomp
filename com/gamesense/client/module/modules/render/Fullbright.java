



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;
import net.minecraft.potion.*;

public class Fullbright extends Module
{
    Setting.Mode mode;
    float oldGamma;
    
    public Fullbright() {
        super("Fullbright", Module.Category.Render);
    }
    
    public void setup() {
        final ArrayList<String> modes = new ArrayList<String>();
        modes.add("Gamma");
        modes.add("Potion");
        this.mode = this.registerMode("Mode", (List)modes, "Gamma");
    }
    
    public void onEnable() {
        this.oldGamma = Fullbright.mc.gameSettings.gammaSetting;
    }
    
    public void onUpdate() {
        if (this.mode.getValue().equalsIgnoreCase("Gamma")) {
            Fullbright.mc.gameSettings.gammaSetting = 666.0f;
            Fullbright.mc.player.removePotionEffect(Potion.getPotionById(16));
        }
        else if (this.mode.getValue().equalsIgnoreCase("Potion")) {
            final PotionEffect potionEffect = new PotionEffect(Potion.getPotionById(16), 123456789, 5);
            potionEffect.setPotionDurationMax(true);
            Fullbright.mc.player.addPotionEffect(potionEffect);
        }
    }
    
    public void onDisable() {
        Fullbright.mc.gameSettings.gammaSetting = this.oldGamma;
        Fullbright.mc.player.removePotionEffect(Potion.getPotionById(16));
    }
}
