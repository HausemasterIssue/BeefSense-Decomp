



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.client.*;
import java.awt.*;
import com.gamesense.api.util.font.*;
import com.gamesense.api.util.misc.*;

public class FontCommand extends Command
{
    public FontCommand() {
        super("Font");
        this.setCommandSyntax(Command.getCommandPrefix() + "font [name] size (use _ for spaces)");
        this.setCommandAlias(new String[] { "font", "setfont", "customfont", "fonts", "chatfont" });
    }
    
    public void onCommand(final String command, final String[] message) {
        final String main = message[0].replace("_", " ");
        int value = Integer.parseInt(message[1]);
        if (value >= 21 || value <= 15) {
            value = 18;
        }
        (GameSense.getInstance().cFontRenderer = new CFontRenderer(new Font(main, 0, value), true, true)).setFontName(main);
        GameSense.getInstance().cFontRenderer.setFontSize(value);
        MessageBus.sendCommandMessage("Font set to: " + main.toUpperCase() + ", size " + value + "!", true);
    }
}
