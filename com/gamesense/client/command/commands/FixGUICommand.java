



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.client.*;
import com.gamesense.client.clickgui.*;
import com.gamesense.api.util.misc.*;

public class FixGUICommand extends Command
{
    public FixGUICommand() {
        super("FixGUI");
        this.setCommandSyntax(Command.getCommandPrefix() + "fixgui");
        this.setCommandAlias(new String[] { "fixgui", "gui", "resetgui" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        GameSense.getInstance().gameSenseGUI = new GameSenseGUI();
        MessageBus.sendCommandMessage("ClickGUI positions reset!", true);
    }
}
