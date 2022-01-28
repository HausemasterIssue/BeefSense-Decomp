



package com.gamesense.api.util.misc;

import club.minnced.discord.rpc.*;

public class Discord
{
    private static String discordID;
    private static DiscordRichPresence discordRichPresence;
    private static DiscordRPC discordRPC;
    private static String clientVersion;
    
    public static void startRPC() {
        final DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        eventHandlers.disconnected = ((var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2));
        Discord.discordRPC.Discord_Initialize(Discord.discordID, eventHandlers, true, (String)null);
        Discord.discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        Discord.discordRichPresence.details = Discord.clientVersion;
        Discord.discordRichPresence.largeImageKey = "logo";
        Discord.discordRichPresence.largeImageText = "discord.gg/xfgPw63";
        Discord.discordRichPresence.state = null;
        Discord.discordRPC.Discord_UpdatePresence(Discord.discordRichPresence);
    }
    
    public static void stopRPC() {
        Discord.discordRPC.Discord_Shutdown();
        Discord.discordRPC.Discord_ClearPresence();
    }
    
    static {
        Discord.discordID = "770790009397313596";
        Discord.discordRichPresence = new DiscordRichPresence();
        Discord.discordRPC = DiscordRPC.INSTANCE;
        Discord.clientVersion = "v0.0.1";
    }
}
