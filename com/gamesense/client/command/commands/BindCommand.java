



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.client.module.*;
import com.gamesense.api.util.misc.*;
import org.lwjgl.input.*;
import java.util.*;

public class BindCommand extends Command
{
    public BindCommand() {
        super("Bind");
        this.setCommandSyntax(Command.getCommandPrefix() + "bind [module] key");
        this.setCommandAlias(new String[] { "bind", "b", "setbind", "key" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0];
        final String value = message[1].toUpperCase();
        for (final Module module : ModuleManager.getModules()) {
            if (module.getName().equalsIgnoreCase(main)) {
                if (value.equalsIgnoreCase("none")) {
                    module.setBind(0);
                    MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                }
                else if (value.length() == 1) {
                    final int key = Keyboard.getKeyIndex(value);
                    module.setBind(key);
                    MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                }
                else {
                    if (value.length() <= 1) {
                        continue;
                    }
                    MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
                }
            }
        }
    }
}
