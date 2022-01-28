



package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.util.render.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.hud.*;
import java.awt.*;

public class Watermark extends HUDModule
{
    private Setting.ColorSetting color;
    
    public Watermark() {
        super("Watermark", new Point(0, 0));
    }
    
    public void setup() {
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    }
    
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, new WatermarkList());
    }
    
    private class WatermarkList implements HUDList
    {
        @Override
        public int getSize() {
            return 1;
        }
        
        @Override
        public String getItem(final int index) {
            return "KiefSense v0.0.1";
        }
        
        @Override
        public Color getItemColor(final int index) {
            return (Color)Watermark.this.color.getValue();
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
