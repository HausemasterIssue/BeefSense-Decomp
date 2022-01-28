



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import net.minecraftforge.client.event.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import com.gamesense.client.*;
import com.gamesense.api.util.misc.*;

public class AutoReply extends Module
{
    private static String reply;
    @EventHandler
    private final Listener<ClientChatReceivedEvent> listener;
    
    public AutoReply() {
        super("AutoReply", Module.Category.Misc);
        this.listener = (Listener<ClientChatReceivedEvent>)new Listener(event -> {
            if (event.getMessage().getUnformattedText().contains("whispers: ") && !event.getMessage().getUnformattedText().startsWith(AutoReply.mc.player.getName())) {
                if (event.getMessage().getUnformattedText().contains("I don't speak to newfags!")) {
                    return;
                }
                MessageBus.sendServerMessage("/r " + AutoReply.reply);
            }
        }, new Predicate[0]);
    }
    
    public static String getReply() {
        return AutoReply.reply;
    }
    
    public static void setReply(final String r) {
        AutoReply.reply = r;
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
    
    static {
        AutoReply.reply = "I don't speak to newfags!";
    }
}
