



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.api.util.misc.*;

public class AutoReplyCommand extends Command
{
    public AutoReplyCommand() {
        super("AutoReply");
        this.setCommandSyntax(Command.getCommandPrefix() + "autoreply set [message] (use _ for spaces)");
        this.setCommandAlias(new String[] { "autoreply", "reply" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0];
        final String value = message[1].replace("_", " ");
        if (main.equalsIgnoreCase("set")) {
            AutoReply.setReply(value);
            MessageBus.sendCommandMessage("Set AutoReply message: " + value + "!", true);
        }
    }
}
