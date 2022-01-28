



package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.util.render.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.hud.*;
import net.minecraft.client.*;
import java.awt.*;

public class Welcomer extends HUDModule
{
    private Setting.ColorSetting color;
    
    public Welcomer() {
        super("Welcomer", new Point(450, 0));
    }
    
    public void setup() {
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    }
    
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, new WelcomerList());
    }
    
    private class WelcomerList implements HUDList
    {
        @Override
        public int getSize() {
            return 1;
        }
        
        @Override
        public String getItem(final int index) {
            return "Hello " + Welcomer.mc.player.getName() + " :^)";
        }
        
        @Override
        public Color getItemColor(final int index) {
            return (Color)Welcomer.this.color.getValue();
        }
        
        @Override
        public boolean sortUp() {
            return false;
        }
        
        @Override
        public boolean sortRight() {
            return false;
        }
    }
}
