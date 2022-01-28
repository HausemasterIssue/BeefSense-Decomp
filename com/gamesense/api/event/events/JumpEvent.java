



package com.gamesense.api.event.events;

import com.gamesense.api.event.*;
import com.gamesense.api.util.world.*;

public class JumpEvent extends GameSenseEvent
{
    private Location location;
    
    public JumpEvent(final Location location) {
        this.location = location;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public void setLocation(final Location location) {
        this.location = location;
    }
}
