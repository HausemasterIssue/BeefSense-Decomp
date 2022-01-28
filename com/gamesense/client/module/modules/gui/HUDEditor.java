



package com.gamesense.client.module.modules.gui;

import com.gamesense.client.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.client.module.*;
import com.gamesense.api.util.misc.*;

public class HUDEditor extends Module
{
    public HUDEditor() {
        super("HUDEditor", Module.Category.GUI);
        this.setBind(25);
        this.setDrawn(false);
    }
    
    public void onEnable() {
        GameSense.getInstance().gameSenseGUI.enterHUDEditor();
        final Announcer announcer = (Announcer)ModuleManager.getModule((Class)Announcer.class);
        if (announcer.clickGui.getValue() && announcer.isEnabled() && HUDEditor.mc.player != null) {
            if (announcer.clientSide.getValue()) {
                MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
            }
            else {
                MessageBus.sendServerMessage(Announcer.guiMessage);
            }
        }
        this.disable();
    }
}
