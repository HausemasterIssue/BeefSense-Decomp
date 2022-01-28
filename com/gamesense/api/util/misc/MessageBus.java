



package com.gamesense.api.util.misc;

import com.mojang.realmsclient.gui.*;
import net.minecraft.client.*;
import com.gamesense.client.module.modules.hud.*;
import com.gamesense.client.module.*;
import net.minecraft.util.text.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;

public class MessageBus
{
    public static String watermark;
    public static ChatFormatting messageFormatting;
    protected static final Minecraft mc;
    
    public static void sendClientPrefixMessage(final String message) {
        final TextComponentString string1 = new TextComponentString(MessageBus.watermark + MessageBus.messageFormatting + message);
        final TextComponentString string2 = new TextComponentString(MessageBus.messageFormatting + message);
        Notifications.addMessage(string2);
        if (ModuleManager.isModuleEnabled(Notifications.class) && Notifications.disableChat.getValue()) {
            return;
        }
        MessageBus.mc.player.addChatMessage((ITextComponent)string1);
    }
    
    public static void sendCommandMessage(final String message, final boolean prefix) {
        final String watermark1 = prefix ? MessageBus.watermark : "";
        final TextComponentString string = new TextComponentString(watermark1 + MessageBus.messageFormatting + message);
        MessageBus.mc.player.addChatMessage((ITextComponent)string);
    }
    
    public static void sendClientRawMessage(final String message) {
        final TextComponentString string = new TextComponentString(MessageBus.messageFormatting + message);
        Notifications.addMessage(string);
        if (ModuleManager.isModuleEnabled(Notifications.class) && Notifications.disableChat.getValue()) {
            return;
        }
        MessageBus.mc.player.addChatMessage((ITextComponent)string);
    }
    
    public static void sendServerMessage(final String message) {
        MessageBus.mc.player.connection.sendPacket((Packet)new CPacketChatMessage(message));
    }
    
    static {
        MessageBus.watermark = ChatFormatting.GRAY + "[" + ChatFormatting.BLUE + "Kief" + ChatFormatting.GRAY + "Sense" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET;
        MessageBus.messageFormatting = ChatFormatting.GRAY;
        mc = Minecraft.getMinecraft();
    }
}
