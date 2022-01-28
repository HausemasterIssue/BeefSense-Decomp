



package com.gamesense.api.event;

import net.minecraft.network.*;

public class EventReceivePacket extends EventCancellable
{
    private Packet packet;
    
    public EventReceivePacket(final EventStageable.EventStage stage, final Packet packet) {
        super(stage);
        this.packet = packet;
    }
    
    public Packet getPacket() {
        return this.packet;
    }
    
    public void setPacket(final Packet packet) {
        this.packet = packet;
    }
}
