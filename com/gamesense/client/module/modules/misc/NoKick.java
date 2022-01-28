



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.*;
import com.gamesense.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.init.*;

public class NoKick extends Module
{
    public Setting.Boolean noPacketKick;
    Setting.Boolean noSlimeCrash;
    Setting.Boolean noOffhandCrash;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    public NoKick() {
        super("NoKick", Module.Category.Misc);
        this.receiveListener = (Listener<PacketEvent.Receive>)new Listener(event -> {
            if (this.noOffhandCrash.getValue() && event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect)event.getPacket()).getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        this.noPacketKick = this.registerBoolean("Packet", true);
        this.noSlimeCrash = this.registerBoolean("Slime", false);
        this.noOffhandCrash = this.registerBoolean("Offhand", false);
    }
    
    public void onUpdate() {
        if (NoKick.mc.world != null && this.noSlimeCrash.getValue()) {
            EntitySlime slime;
            NoKick.mc.world.loadedEntityList.forEach(entity -> {
                if (entity instanceof EntitySlime) {
                    slime = entity;
                    if (slime.getSlimeSize() > 4) {
                        NoKick.mc.world.removeEntity((Entity)entity);
                    }
                }
            });
        }
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
}
