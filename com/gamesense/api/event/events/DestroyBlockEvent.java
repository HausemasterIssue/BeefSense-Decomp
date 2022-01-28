



package com.gamesense.api.event.events;

import com.gamesense.api.event.*;
import net.minecraft.util.math.*;

public class DestroyBlockEvent extends GameSenseEvent
{
    BlockPos blockPos;
    
    public DestroyBlockEvent(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    
    public BlockPos getBlockPos() {
        return this.blockPos;
    }
    
    public void setBlockPos(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}
