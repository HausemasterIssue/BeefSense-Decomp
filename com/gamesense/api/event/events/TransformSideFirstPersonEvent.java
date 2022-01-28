



package com.gamesense.api.event.events;

import com.gamesense.api.event.*;
import net.minecraft.util.*;

public class TransformSideFirstPersonEvent extends GameSenseEvent
{
    private final EnumHandSide enumHandSide;
    
    public TransformSideFirstPersonEvent(final EnumHandSide enumHandSide) {
        this.enumHandSide = enumHandSide;
    }
    
    public EnumHandSide getEnumHandSide() {
        return this.enumHandSide;
    }
}
