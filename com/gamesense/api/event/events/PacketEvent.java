



package com.gamesense.api.event.events;

import com.gamesense.api.event.*;
import net.minecraft.network.*;

public class PacketEvent extends GameSenseEvent
{
    private final Packet packet;
    
    public PacketEvent(final Packet packet) {
        this.packet = packet;
    }
    
    public Packet getPacket() {
        return this.packet;
    }
    
    public static class Receive extends PacketEvent
    {
        public Receive(final Packet packet) {
            super(packet);
        }
    }
    
    public static class Send extends PacketEvent
    {
        public Send(final Packet packet) {
            super(packet);
        }
    }
    
    public static class PostReceive extends PacketEvent
    {
        public PostReceive(final Packet packet) {
            super(packet);
        }
    }
    
    public static class PostSend extends PacketEvent
    {
        public PostSend(final Packet packet) {
            super(packet);
        }
    }
}
