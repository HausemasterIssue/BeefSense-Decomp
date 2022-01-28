



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.api.util.misc.*;

public class AutoGGCommand extends Command
{
    public AutoGGCommand() {
        super("AutoGG");
        this.setCommandSyntax(Command.getCommandPrefix() + "autogg add/del [message] (use _ for spaces)");
        this.setCommandAlias(new String[] { "autogg", "gg" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0];
        final String value = message[1].replace("_", " ");
        if (main.equalsIgnoreCase("add") && !AutoGG.getAutoGgMessages().contains(value)) {
            AutoGG.addAutoGgMessage(value);
            MessageBus.sendCommandMessage("Added AutoGG message: " + value + "!", true);
        }
        else if (main.equalsIgnoreCase("del") && AutoGG.getAutoGgMessages().contains(value)) {
            AutoGG.getAutoGgMessages().remove(value);
            MessageBus.sendCommandMessage("Deleted AutoGG message: " + value + "!", true);
        }
    }
}
