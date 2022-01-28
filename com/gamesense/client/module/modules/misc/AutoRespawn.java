



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraftforge.client.event.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import net.minecraft.network.*;
import com.gamesense.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.network.play.client.*;

public class AutoRespawn extends Module
{
    private static String AutoRespawnMessage;
    Setting.Boolean respawnMessage;
    Setting.Integer respawnMessageDelay;
    private boolean isDead;
    private boolean sentRespawnMessage;
    long timeSinceRespawn;
    @EventHandler
    private final Listener<GuiOpenEvent> livingDeathEventListener;
    
    public AutoRespawn() {
        super("AutoRespawn", Module.Category.Misc);
        this.sentRespawnMessage = true;
        this.livingDeathEventListener = (Listener<GuiOpenEvent>)new Listener(event -> {
            if (event.getGui() instanceof GuiGameOver) {
                event.setCanceled(true);
                this.isDead = true;
                this.sentRespawnMessage = true;
                AutoRespawn.mc.player.connection.sendPacket((Packet)new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        this.respawnMessage = this.registerBoolean("Respawn Message", false);
        this.respawnMessageDelay = this.registerInteger("Msg Delay(ms)", 0, 0, 5000);
    }
    
    public void onUpdate() {
        if (AutoRespawn.mc.player == null) {
            return;
        }
        if (this.isDead && AutoRespawn.mc.player.isEntityAlive()) {
            if (this.respawnMessage.getValue()) {
                this.sentRespawnMessage = false;
                this.timeSinceRespawn = System.currentTimeMillis();
            }
            this.isDead = false;
        }
        if (!this.sentRespawnMessage && System.currentTimeMillis() - this.timeSinceRespawn > this.respawnMessageDelay.getValue()) {
            AutoRespawn.mc.player.connection.sendPacket((Packet)new CPacketChatMessage(AutoRespawn.AutoRespawnMessage));
            this.sentRespawnMessage = true;
        }
    }
    
    protected void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    protected void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
    
    public static void setAutoRespawnMessage(final String string) {
        AutoRespawn.AutoRespawnMessage = string;
    }
    
    public static String getAutoRespawnMessages() {
        return AutoRespawn.AutoRespawnMessage;
    }
    
    static {
        AutoRespawn.AutoRespawnMessage = "/kit";
    }
}
