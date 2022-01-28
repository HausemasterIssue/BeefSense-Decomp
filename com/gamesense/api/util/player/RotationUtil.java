



package com.gamesense.api.util.player;

import net.minecraft.client.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import com.gamesense.client.*;
import net.minecraft.entity.player.*;
import com.gamesense.api.util.world.*;
import net.minecraft.entity.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;

public class RotationUtil
{
    private static final Minecraft mc;
    public static final RotationUtil ROTATION_UTIL;
    private int rotationConnections;
    private boolean shouldSpoofAngles;
    private boolean isSpoofingAngles;
    private double yaw;
    private double pitch;
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener;
    
    private RotationUtil() {
        this.rotationConnections = 0;
        this.packetSendListener = (Listener<PacketEvent.Send>)new Listener(event -> {
            final Packet packet = event.getPacket();
            if (packet instanceof CPacketPlayer && this.shouldSpoofAngles && this.isSpoofingAngles) {
                ((CPacketPlayer)packet).yaw = (float)this.yaw;
                ((CPacketPlayer)packet).pitch = (float)this.pitch;
            }
        }, new Predicate[0]);
    }
    
    public void onEnable() {
        ++this.rotationConnections;
        if (this.rotationConnections == 1) {
            GameSense.EVENT_BUS.subscribe((Object)this);
        }
    }
    
    public void onDisable() {
        --this.rotationConnections;
        if (this.rotationConnections == 0) {
            GameSense.EVENT_BUS.unsubscribe((Object)this);
        }
    }
    
    public void lookAtPacket(final double px, final double py, final double pz, final EntityPlayer me) {
        final double[] v = EntityUtil.calculateLookAt(px, py, pz, (Entity)me);
        this.setYawAndPitch((float)v[0], (float)v[1]);
    }
    
    public void setYawAndPitch(final float yaw1, final float pitch1) {
        this.yaw = yaw1;
        this.pitch = pitch1;
        this.isSpoofingAngles = true;
    }
    
    public void resetRotation() {
        if (this.isSpoofingAngles) {
            this.yaw = RotationUtil.mc.player.rotationYaw;
            this.pitch = RotationUtil.mc.player.rotationPitch;
            this.isSpoofingAngles = false;
        }
    }
    
    public void shouldSpoofAngles(final boolean e) {
        this.shouldSpoofAngles = e;
    }
    
    public boolean isSpoofingAngles() {
        return this.isSpoofingAngles;
    }
    
    static {
        mc = Minecraft.getMinecraft();
        ROTATION_UTIL = new RotationUtil();
    }
}
