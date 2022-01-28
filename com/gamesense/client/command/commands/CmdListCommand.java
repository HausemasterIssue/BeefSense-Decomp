



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.api.util.misc.*;
import java.util.*;

public class CmdListCommand extends Command
{
    public CmdListCommand() {
        super("Commands");
        this.setCommandSyntax(Command.getCommandPrefix() + "commands");
        this.setCommandAlias(new String[] { "commands", "cmd", "command", "commandlist", "help" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        for (final Command command2 : CommandManager.getCommands()) {
            MessageBus.sendCommandMessage(command2.getCommandName() + ": \"" + command2.getCommandSyntax() + "\"!", true);
        }
    }
}
