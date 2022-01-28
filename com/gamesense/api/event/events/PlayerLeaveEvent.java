



package com.gamesense.api.event.events;

import com.gamesense.api.event.*;

public class PlayerLeaveEvent extends GameSenseEvent
{
    private final String name;
    
    public PlayerLeaveEvent(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
