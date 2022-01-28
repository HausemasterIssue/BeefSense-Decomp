



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.api.util.misc.*;
import com.gamesense.client.*;
import com.gamesense.api.setting.*;
import com.gamesense.client.module.*;

public class SetCommand extends Command
{
    public SetCommand() {
        super("Set");
        this.setCommandSyntax(Command.getCommandPrefix() + "set [module] [setting] value (no color support)");
        this.setCommandAlias(new String[] { "set", "setmodule", "changesetting", "setting" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final String main = message[0];
        final Module module = ModuleManager.getModule(main);
        if (module == null) {
            MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
            return;
        }
        final Module module2;
        GameSense.getInstance().settingsManager.getSettingsForMod(module).stream().filter(setting -> setting.getConfigName().equalsIgnoreCase(message[1])).forEach(setting -> {
            if (((Setting)setting).getType().equals((Object)Setting.Type.BOOLEAN)) {
                if (message[2].equalsIgnoreCase("true") || message[2].equalsIgnoreCase("false")) {
                    ((Setting.Boolean)setting).setValue(Boolean.parseBoolean(message[2]));
                    MessageBus.sendCommandMessage(module2.getName() + " " + ((Setting)setting).getConfigName() + " set to: " + ((Setting.Boolean)setting).getValue() + "!", true);
                }
                else {
                    MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
                }
            }
            else if (((Setting)setting).getType().equals((Object)Setting.Type.INTEGER)) {
                if (Integer.parseInt(message[2]) > ((Setting.Integer)setting).getMax()) {
                    ((Setting.Integer)setting).setValue(((Setting.Integer)setting).getMax());
                }
                if (Integer.parseInt(message[2]) < ((Setting.Integer)setting).getMin()) {
                    ((Setting.Integer)setting).setValue(((Setting.Integer)setting).getMin());
                }
                if (Integer.parseInt(message[2]) < ((Setting.Integer)setting).getMax() && Integer.parseInt(message[2]) > ((Setting.Integer)setting).getMin()) {
                    ((Setting.Integer)setting).setValue(Integer.parseInt(message[2]));
                }
                MessageBus.sendCommandMessage(module2.getName() + " " + ((Setting)setting).getConfigName() + " set to: " + ((Setting.Integer)setting).getValue() + "!", true);
            }
            else if (((Setting)setting).getType().equals((Object)Setting.Type.DOUBLE)) {
                if (Double.parseDouble(message[2]) > ((Setting.Double)setting).getMax()) {
                    ((Setting.Double)setting).setValue(((Setting.Double)setting).getMax());
                }
                if (Double.parseDouble(message[2]) < ((Setting.Double)setting).getMin()) {
                    ((Setting.Double)setting).setValue(((Setting.Double)setting).getMin());
                }
                if (Double.parseDouble(message[2]) < ((Setting.Double)setting).getMax() && Double.parseDouble(message[2]) > ((Setting.Double)setting).getMin()) {
                    ((Setting.Double)setting).setValue(Double.parseDouble(message[2]));
                }
                MessageBus.sendCommandMessage(module2.getName() + " " + ((Setting)setting).getConfigName() + " set to: " + ((Setting.Double)setting).getValue() + "!", true);
            }
            else if (((Setting)setting).getType().equals((Object)Setting.Type.MODE)) {
                if (!setting.getModes().contains(message[2])) {
                    MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
                }
                else {
                    setting.setValue(message[2]);
                    MessageBus.sendCommandMessage(module2.getName() + " " + ((Setting)setting).getConfigName() + " set to: " + setting.getValue() + "!", true);
                }
            }
            else {
                MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
            }
        });
    }
}
