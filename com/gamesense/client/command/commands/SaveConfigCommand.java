



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.api.config.*;
import com.gamesense.api.util.misc.*;

public class SaveConfigCommand extends Command
{
    public SaveConfigCommand() {
        super("SaveConfig");
        this.setCommandSyntax(Command.getCommandPrefix() + "saveconfig");
        this.setCommandAlias(new String[] { "saveconfig", "reloadconfig", "config", "saveconfiguration" });
    }
    
    public void onCommand(final String command, final String[] message) {
        ConfigStopper.saveConfig();
        MessageBus.sendCommandMessage("Config saved!", true);
    }
}
