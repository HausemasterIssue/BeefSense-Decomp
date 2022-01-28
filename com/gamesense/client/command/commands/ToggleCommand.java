



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.api.util.misc.*;
import com.gamesense.client.module.*;

public class ToggleCommand extends Command
{
    public ToggleCommand() {
        super("Toggle");
        this.setCommandSyntax(Command.getCommandPrefix() + "toggle [module]");
        this.setCommandAlias(new String[] { "toggle", "t", "enable", "disable" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0];
        final Module module = ModuleManager.getModule(main);
        if (module == null) {
            MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
            return;
        }
        module.toggle();
        if (module.isEnabled()) {
            MessageBus.sendCommandMessage("Module " + module.getName() + " set to: ENABLED!", true);
        }
        else {
            MessageBus.sendCommandMessage("Module " + module.getName() + " set to: DISABLED!", true);
        }
    }
}
