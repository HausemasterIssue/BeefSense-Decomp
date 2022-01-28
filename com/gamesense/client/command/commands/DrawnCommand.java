



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.api.util.misc.*;
import com.gamesense.client.module.*;

public class DrawnCommand extends Command
{
    public DrawnCommand() {
        super("Drawn");
        this.setCommandSyntax(Command.getCommandPrefix() + "drawn [module]");
        this.setCommandAlias(new String[] { "drawn", "shown" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0];
        final Module module = ModuleManager.getModule(main);
        if (module == null) {
            MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
            return;
        }
        if (module.isDrawn()) {
            module.setDrawn(false);
            MessageBus.sendCommandMessage("Module " + module.getName() + " drawn set to: FALSE!", true);
        }
        else if (!module.isDrawn()) {
            module.setDrawn(true);
            MessageBus.sendCommandMessage("Module " + module.getName() + " drawn set to: TRUE!", true);
        }
    }
}
