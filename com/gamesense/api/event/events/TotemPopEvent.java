



package com.gamesense.api.event.events;

import com.gamesense.api.event.*;
import net.minecraft.entity.*;

public class TotemPopEvent extends GameSenseEvent
{
    private final Entity entity;
    
    public TotemPopEvent(final Entity entity) {
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
}
