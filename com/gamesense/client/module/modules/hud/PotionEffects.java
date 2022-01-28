



package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.util.render.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.hud.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.potion.*;
import java.awt.*;

public class PotionEffects extends HUDModule
{
    private Setting.Boolean sortUp;
    private Setting.Boolean sortRight;
    private Setting.ColorSetting color;
    private PotionList list;
    
    public PotionEffects() {
        super("PotionEffects", new Point(0, 300));
        this.list = new PotionList();
    }
    
    public void setup() {
        this.sortUp = this.registerBoolean("Sort Up", false);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
    }
    
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, this.list);
    }
    
    private class PotionList implements HUDList
    {
        @Override
        public int getSize() {
            return PotionEffects.mc.player.getActivePotionEffects().size();
        }
        
        @Override
        public String getItem(final int index) {
            final PotionEffect effect = (PotionEffect)PotionEffects.mc.player.getActivePotionEffects().toArray()[index];
            final String name = I18n.format(effect.getPotion().getName(), new Object[0]);
            final int amplifier = effect.getAmplifier() + 1;
            return name + " " + amplifier + ChatFormatting.GRAY + " " + Potion.getPotionDurationString(effect, 1.0f);
        }
        
        @Override
        public Color getItemColor(final int index) {
            return (Color)PotionEffects.this.color.getValue();
        }
        
        @Override
        public boolean sortUp() {
            return PotionEffects.this.sortUp.isOn();
        }
        
        @Override
        public boolean sortRight() {
            return PotionEffects.this.sortRight.isOn();
        }
    }
}
