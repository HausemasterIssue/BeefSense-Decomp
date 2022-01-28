



package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.*;
import com.gamesense.api.util.render.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.hud.*;
import com.gamesense.client.module.*;
import com.gamesense.client.*;
import com.mojang.realmsclient.gui.*;
import java.util.*;
import java.awt.*;

public class ArrayListModule extends HUDModule
{
    private Setting.Boolean sortUp;
    private Setting.Boolean sortRight;
    private Setting.ColorSetting color;
    private ModuleList list;
    
    public ArrayListModule() {
        super("ArrayList", new Point(0, 200));
        this.list = new ModuleList();
    }
    
    public void setup() {
        this.sortUp = this.registerBoolean("Sort Up", true);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    }
    
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, this.list);
    }
    
    public void onRender() {
        this.list.activeModules.clear();
        for (final Module module2 : ModuleManager.getModules()) {
            if (module2.isEnabled() && module2.isDrawn()) {
                this.list.activeModules.add(module2);
            }
        }
        this.list.activeModules.sort(Comparator.comparing(module -> -GameSense.getInstance().gameSenseGUI.guiInterface.getFontWidth(module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo())));
    }
    
    private class ModuleList implements HUDList
    {
        public List<Module> activeModules;
        
        private ModuleList() {
            this.activeModules = new ArrayList<Module>();
        }
        
        @Override
        public int getSize() {
            return this.activeModules.size();
        }
        
        @Override
        public String getItem(final int index) {
            final Module module = this.activeModules.get(index);
            return module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo();
        }
        
        @Override
        public Color getItemColor(final int index) {
            final GSColor c = ArrayListModule.this.color.getValue();
            return Color.getHSBColor(c.getHue() + (ArrayListModule.this.color.getRainbow() ? (0.02f * index) : 0.0f), c.getSaturation(), c.getBrightness());
        }
        
        @Override
        public boolean sortUp() {
            return ArrayListModule.this.sortUp.isOn();
        }
        
        @Override
        public boolean sortRight() {
            return ArrayListModule.this.sortRight.isOn();
        }
    }
}
