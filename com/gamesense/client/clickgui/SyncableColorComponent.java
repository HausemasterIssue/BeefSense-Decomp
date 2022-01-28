



package com.gamesense.client.clickgui;

import com.gamesense.api.setting.*;
import net.minecraft.util.text.*;
import com.lukflug.panelstudio.settings.*;
import com.gamesense.client.module.modules.gui.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.*;
import java.awt.*;

public class SyncableColorComponent extends ColorComponent
{
    public SyncableColorComponent(final Theme theme, final Setting.ColorSetting setting, final Toggleable colorToggle, final Animation animation) {
        super(TextFormatting.BOLD + setting.getName(), null, theme.getContainerRenderer(), animation, theme.getComponentRenderer(), (ColorSetting)setting, false, true, colorToggle);
        if (setting != ClickGuiModule.enabledColor) {
            this.addComponent(new SyncButton(theme.getComponentRenderer()));
        }
    }
    
    private class SyncButton extends FocusableComponent
    {
        public SyncButton(final Renderer renderer) {
            super("Sync Color", null, renderer);
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            this.renderer.overrideColorScheme(SyncableColorComponent.this.overrideScheme);
            this.renderer.renderTitle(context, this.title, this.hasFocus(context), false);
            this.renderer.restoreColorScheme();
        }
        
        @Override
        public void handleButton(final Context context, final int button) {
            super.handleButton(context, button);
            if (button == 0 && context.isClicked()) {
                SyncableColorComponent.this.setting.setValue((Color)ClickGuiModule.enabledColor.getColor());
                SyncableColorComponent.this.setting.setRainbow(ClickGuiModule.enabledColor.getRainbow());
            }
        }
    }
}
