



package com.gamesense.client.module.modules.hud;

import java.awt.*;
import com.gamesense.client.module.modules.gui.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.*;
import com.gamesense.client.module.*;
import com.lukflug.panelstudio.settings.*;
import com.lukflug.panelstudio.tabgui.*;
import java.util.*;

public class TabGUIModule extends HUDModule
{
    public TabGUIModule() {
        super("TabGUI", new Point(10, 10));
    }
    
    public void populate(final Theme theme) {
        final TabGUIRenderer renderer = new DefaultRenderer(new SettingsColorScheme((ColorSetting)ClickGuiModule.enabledColor, (ColorSetting)ClickGuiModule.backgroundColor, (ColorSetting)ClickGuiModule.settingBackgroundColor, (ColorSetting)ClickGuiModule.backgroundColor, (ColorSetting)ClickGuiModule.fontColor, (NumberSetting)ClickGuiModule.opacity), 12, 5, 200, 208, 203, 205, 28);
        final TabGUI component = new TabGUI("TabGUI", renderer, new Animation() {
            @Override
            protected int getSpeed() {
                return ClickGuiModule.animationSpeed.getValue();
            }
        }, this.position, 75);
        for (final Module.Category category : Module.Category.values()) {
            final TabGUIContainer tab = new TabGUIContainer(category.name(), renderer, new SettingsAnimation((NumberSetting)ClickGuiModule.animationSpeed));
            component.addComponent(tab);
            for (final Module module : ModuleManager.getModulesInCategory(category)) {
                tab.addComponent(new TabGUIItem(module.getName(), (Toggleable)module));
            }
        }
        this.component = component;
    }
}
