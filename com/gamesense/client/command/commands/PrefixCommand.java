



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.api.util.misc.*;

public class PrefixCommand extends Command
{
    public PrefixCommand() {
        super("Prefix");
        this.setCommandSyntax(Command.getCommandPrefix() + "prefix value (no letters or numbers)");
        this.setCommandAlias(new String[] { "prefix", "setprefix", "cmdprefix", "commandprefix" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0].toUpperCase().replaceAll("[a-zA-Z0-9]", null);
        final int size = message[0].length();
        if (main != null && size == 1) {
            Command.setCommandPrefix(main);
            MessageBus.sendCommandMessage("Prefix set: \"" + main + "\"!", true);
        }
        else if (size != 1) {
            MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
        }
    }
}
