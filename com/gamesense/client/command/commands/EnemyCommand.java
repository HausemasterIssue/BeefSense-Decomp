



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.api.util.player.enemy.*;
import com.gamesense.api.util.misc.*;

public class EnemyCommand extends Command
{
    public EnemyCommand() {
        super("Enemy");
        this.setCommandSyntax(Command.getCommandPrefix() + "enemy list/add/del [player]");
        this.setCommandAlias(new String[] { "enemy", "enemies", "e" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0];
        if (main.equalsIgnoreCase("list")) {
            MessageBus.sendClientPrefixMessage("Enemies: " + Enemies.getEnemiesByName() + "!");
            return;
        }
        final String value = message[1];
        if (main.equalsIgnoreCase("add") && !Enemies.isEnemy(value)) {
            Enemies.addEnemy(value);
            MessageBus.sendCommandMessage("Added enemy: " + value.toUpperCase() + "!", true);
        }
        else if (main.equalsIgnoreCase("del") && Enemies.isEnemy(value)) {
            Enemies.delEnemy(value);
            MessageBus.sendCommandMessage("Deleted enemy: " + value.toUpperCase() + "!", true);
        }
    }
}
