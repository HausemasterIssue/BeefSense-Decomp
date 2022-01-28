



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.api.util.misc.*;

public class FriendCommand extends Command
{
    public FriendCommand() {
        super("Friend");
        this.setCommandSyntax(Command.getCommandPrefix() + "friend list/add/del [player]");
        this.setCommandAlias(new String[] { "friend", "friends", "f" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0];
        if (main.equalsIgnoreCase("list")) {
            MessageBus.sendClientPrefixMessage("Friends: " + Friends.getFriendsByName() + "!");
            return;
        }
        final String value = message[1];
        if (main.equalsIgnoreCase("add") && !Friends.isFriend(value)) {
            Friends.addFriend(value);
            MessageBus.sendCommandMessage("Added friend: " + value.toUpperCase() + "!", true);
        }
        else if (main.equalsIgnoreCase("del") && Friends.isFriend(value)) {
            Friends.delFriend(value);
            MessageBus.sendCommandMessage("Deleted friend: " + value.toUpperCase() + "!", true);
        }
    }
}
