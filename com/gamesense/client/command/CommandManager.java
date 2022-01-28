



package com.gamesense.client.command;

import com.gamesense.client.command.commands.*;
import java.util.*;
import com.gamesense.api.util.misc.*;

public class CommandManager
{
    public static ArrayList<Command> commands;
    boolean isValidCommand;
    
    public CommandManager() {
        this.isValidCommand = false;
    }
    
    public static void registerCommands() {
        addCommand(new AutoGearCommand());
        addCommand(new AutoGGCommand());
        addCommand(new AutoReplyCommand());
        addCommand(new AutoRespawnCommand());
        addCommand(new BindCommand());
        addCommand(new CmdListCommand());
        addCommand(new DisableAllCommand());
        addCommand(new DrawnCommand());
        addCommand(new EnemyCommand());
        addCommand(new FixGUICommand());
        addCommand(new FixHUDCommand());
        addCommand(new FontCommand());
        addCommand(new FriendCommand());
        addCommand(new ModulesCommand());
        addCommand(new OpenFolderCommand());
        addCommand(new PrefixCommand());
        addCommand(new SaveConfigCommand());
        addCommand(new SetCommand());
        addCommand(new ToggleCommand());
    }
    
    public static void addCommand(final Command command) {
        CommandManager.commands.add(command);
    }
    
    public static ArrayList<Command> getCommands() {
        return CommandManager.commands;
    }
    
    public static Command getCommandByName(final String name) {
        for (final Command command : CommandManager.commands) {
            if (command.getCommandName() == name) {
                return command;
            }
        }
        return null;
    }
    
    public void callCommand(final String input) {
        final String[] split = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        final String command2 = split[0];
        final String args = input.substring(command2.length()).trim();
        this.isValidCommand = false;
        final String[] array;
        int length;
        int i = 0;
        String string;
        final String s;
        final String s2;
        CommandManager.commands.forEach(command -> {
            command.getCommandAlias();
            for (length = array.length; i < length; ++i) {
                string = array[i];
                if (string.equalsIgnoreCase(s)) {
                    this.isValidCommand = true;
                    try {
                        command.onCommand(s2, s2.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                    }
                    catch (Exception e) {
                        MessageBus.sendCommandMessage(command.getCommandSyntax(), true);
                    }
                }
            }
            return;
        });
        if (!this.isValidCommand) {
            MessageBus.sendCommandMessage("Error! Invalid command!", true);
        }
    }
    
    static {
        CommandManager.commands = new ArrayList<Command>();
    }
}
