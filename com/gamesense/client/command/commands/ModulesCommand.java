



package com.gamesense.client.command.commands;

import com.gamesense.client.command.*;
import com.gamesense.client.module.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.*;
import java.util.*;

public class ModulesCommand extends Command
{
    public ModulesCommand() {
        super("Modules");
        this.setCommandSyntax(Command.getCommandPrefix() + "modules (click to toggle)");
        this.setCommandAlias(new String[] { "modules", "module", "modulelist", "mod", "mods" });
    }
    
    public void onCommand(final String command, final String[] message) throws Exception {
        final TextComponentString msg = new TextComponentString("§7Modules: §f ");
        final Collection<Module> modules = ModuleManager.getModules();
        final int size = modules.size();
        int index = 0;
        for (final Module module : modules) {
            msg.appendSibling(new TextComponentString((module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED) + module.getName() + "§7" + ((index == size - 1) ? "" : ", ")).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString(module.getCategory().name()))).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Command.getCommandPrefix() + "toggle " + module.getName()))));
            ++index;
        }
        msg.appendSibling((ITextComponent)new TextComponentString(ChatFormatting.GRAY + "!"));
        ModulesCommand.mc.ingameGUI.getChatGUI().printChatMessage((ITextComponent)msg);
    }
}
