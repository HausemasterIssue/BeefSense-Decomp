



package com.gamesense.client.clickgui;

import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.settings.*;
import com.lukflug.panelstudio.*;

public class GameSenseKeybind extends KeybindComponent
{
    public GameSenseKeybind(final Renderer renderer, final KeybindSetting keybind) {
        super(renderer, keybind);
    }
    
    @Override
    public void handleKey(final Context context, final int scancode) {
        context.setHeight(this.renderer.getHeight(false));
        if (this.hasFocus(context) && scancode == 211) {
            this.keybind.setKey(0);
            this.releaseFocus();
            return;
        }
        super.handleKey(context, scancode);
    }
}
