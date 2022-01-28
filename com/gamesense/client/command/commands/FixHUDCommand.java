



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.client.module.*;
import com.gamesense.api.util.misc.*;
import java.util.*;

public class FixHUDCommand extends Command
{
    public FixHUDCommand() {
        super("FixHUD");
        this.setCommandSyntax(Command.getCommandPrefix() + "fixhud");
        this.setCommandAlias(new String[] { "fixhud", "hud", "resethud" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        for (final Module module : ModuleManager.getModules()) {
            if (module instanceof HUDModule) {
                ((HUDModule)module).resetPosition();
            }
        }
        MessageBus.sendCommandMessage("HUD positions reset!", true);
    }
}
