



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.init.*;

public class FastPlace extends Module
{
    Setting.Boolean exp;
    Setting.Boolean crystals;
    Setting.Boolean offhandCrystal;
    Setting.Boolean everything;
    
    public FastPlace() {
        super("FastPlace", Module.Category.Misc);
    }
    
    public void setup() {
        this.exp = this.registerBoolean("Exp", false);
        this.crystals = this.registerBoolean("Crystals", false);
        this.offhandCrystal = this.registerBoolean("Offhand Crystal", false);
        this.everything = this.registerBoolean("Everything", false);
    }
    
    public void onUpdate() {
        if ((this.exp.getValue() && FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE) || FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.crystals.getValue() && FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.offhandCrystal.getValue() && FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.everything.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        FastPlace.mc.playerController.blockHitDelay = 0;
    }
}
