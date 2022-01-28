



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import java.util.*;
import com.gamesense.client.*;
import net.minecraft.network.play.client.*;
import com.gamesense.client.command.*;

public class ChatSuffix extends Module
{
    Setting.Mode Separator;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public ChatSuffix() {
        super("ChatSuffix", Module.Category.Misc);
        this.listener = (Listener<PacketEvent.Send>)new Listener(event -> {
            if (event.getPacket() instanceof CPacketChatMessage) {
                if (((CPacketChatMessage)event.getPacket()).getMessage().startsWith("/") || ((CPacketChatMessage)event.getPacket()).getMessage().startsWith(Command.getCommandPrefix())) {
                    return;
                }
                String Separator2 = null;
                if (this.Separator.getValue().equalsIgnoreCase(">>")) {
                    Separator2 = " \u300b";
                }
                if (this.Separator.getValue().equalsIgnoreCase("<<")) {
                    Separator2 = " \u300a";
                }
                else if (this.Separator.getValue().equalsIgnoreCase("|")) {
                    Separator2 = " \u23d0 ";
                }
                final String old = ((CPacketChatMessage)event.getPacket()).getMessage();
                final String suffix = Separator2 + this.toUnicode("KiefSense");
                final String s = old + suffix;
                if (s.length() > 255) {
                    return;
                }
                ((CPacketChatMessage)event.getPacket()).message = s;
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        final ArrayList<String> Separators = new ArrayList<String>();
        Separators.add(">>");
        Separators.add("<<");
        Separators.add("|");
        this.Separator = this.registerMode("Separator", (List)Separators, "|");
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
    
    public String toUnicode(final String s) {
        return s.toLowerCase().replace("a", "\u1d00").replace("b", "\u0299").replace("c", "\u1d04").replace("d", "\u1d05").replace("e", "\u1d07").replace("f", "\ua730").replace("g", "\u0262").replace("h", "\u029c").replace("i", "\u026a").replace("j", "\u1d0a").replace("k", "\u1d0b").replace("l", "\u029f").replace("m", "\u1d0d").replace("n", "\u0274").replace("o", "\u1d0f").replace("p", "\u1d18").replace("q", "\u01eb").replace("r", "\u0280").replace("s", "\ua731").replace("t", "\u1d1b").replace("u", "\u1d1c").replace("v", "\u1d20").replace("w", "\u1d21").replace("x", "\u02e3").replace("y", "\u028f").replace("z", "\u1d22");
    }
}
