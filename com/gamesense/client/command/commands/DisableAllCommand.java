



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.client.module.*;
import com.gamesense.api.util.misc.*;
import java.util.*;

public class DisableAllCommand extends Command
{
    public DisableAllCommand() {
        super("DisableAll");
        this.setCommandSyntax(Command.getCommandPrefix() + "disableall");
        this.setCommandAlias(new String[] { "disableall", "stop" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        int count = 0;
        for (final Module module : ModuleManager.getModules()) {
            if (module.isEnabled()) {
                module.disable();
                ++count;
            }
        }
        MessageBus.sendCommandMessage("Disabled " + count + " modules!", true);
    }
}
