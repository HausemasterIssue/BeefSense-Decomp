



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.util.misc.*;

public class DiscordRPCModule extends Module
{
    public DiscordRPCModule() {
        super("DiscordRPC", Module.Category.Misc);
        this.setDrawn(false);
    }
    
    public void onEnable() {
        Discord.startRPC();
        if (DiscordRPCModule.mc.player != null || DiscordRPCModule.mc.world != null) {
            MessageBus.sendClientPrefixMessage("Discord RPC started!");
        }
    }
    
    public void onDisable() {
        Discord.stopRPC();
        if (DiscordRPCModule.mc.player != null || DiscordRPCModule.mc.world != null) {
            MessageBus.sendClientPrefixMessage("Discord RPC stopped!");
        }
    }
}
