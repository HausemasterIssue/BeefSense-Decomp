



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.api.util.misc.*;

public class AutoRespawnCommand extends Command
{
    public AutoRespawnCommand() {
        super("AutoRespawn");
        this.setCommandSyntax(Command.getCommandPrefix() + "autorespawn get/set [message] (do NOT use _ for spaces)");
        this.setCommandAlias(new String[] { "autorespawn", "respawn" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0];
        if (main.equalsIgnoreCase("get")) {
            MessageBus.sendCommandMessage("AutoRespawn message is: " + AutoRespawn.getAutoRespawnMessages() + "!", true);
            return;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < message.length; ++i) {
            stringBuilder.append(message[i]);
            stringBuilder.append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        final String value = stringBuilder.toString();
        if (main.equalsIgnoreCase("set") && !AutoRespawn.getAutoRespawnMessages().equals(value)) {
            AutoRespawn.setAutoRespawnMessage(value);
            MessageBus.sendCommandMessage("Set AutoRespawn message to: " + value + "!", true);
        }
    }
}
