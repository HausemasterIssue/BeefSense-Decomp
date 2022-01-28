



package com.gamesense.client.command;

import net.minecraft.client.*;

public abstract class Command
{
    protected static final Minecraft mc;
    public static String commandPrefix;
    String commandName;
    String[] commandAlias;
    String commandSyntax;
    
    public Command(final String commandName) {
        this.commandName = commandName;
    }
    
    public static String getCommandPrefix() {
        return Command.commandPrefix;
    }
    
    public String getCommandName() {
        return this.commandName;
    }
    
    public String getCommandSyntax() {
        return this.commandSyntax;
    }
    
    public String[] getCommandAlias() {
        return this.commandAlias;
    }
    
    public static void setCommandPrefix(final String prefix) {
        Command.commandPrefix = prefix;
    }
    
    public void setCommandSyntax(final String syntax) {
        this.commandSyntax = syntax;
    }
    
    public void setCommandAlias(final String[] alias) {
        this.commandAlias = alias;
    }
    
    public abstract void onCommand(final String p0, final String[] p1) throws Exception;
    
    static {
        mc = Minecraft.getMinecraft();
        Command.commandPrefix = "-";
    }
}
