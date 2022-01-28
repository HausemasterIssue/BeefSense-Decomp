



package com.gamesense.api.event.events;

import com.gamesense.api.event.*;

public class PlayerJoinEvent extends GameSenseEvent
{
    private final String name;
    
    public PlayerJoinEvent(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
