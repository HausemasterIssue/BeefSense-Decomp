



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import java.awt.*;
import java.io.*;
import com.gamesense.api.util.misc.*;

public class OpenFolderCommand extends Command
{
    public OpenFolderCommand() {
        super("OpenFolder");
        this.setCommandSyntax(Command.getCommandPrefix() + "openfolder");
        this.setCommandAlias(new String[] { "openfolder", "config", "open", "folder" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        Desktop.getDesktop().open(new File("KiefSense/".replace("/", "")));
        MessageBus.sendCommandMessage("Opened config folder!", true);
    }
}
