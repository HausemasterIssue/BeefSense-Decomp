



package com.gamesense.api.event;

import net.minecraft.util.math.*;
import net.minecraft.util.*;

public class EventRightClickBlock extends EventCancellable
{
    private BlockPos pos;
    private EnumFacing facing;
    private Vec3d vec;
    private EnumHand hand;
    
    public EventRightClickBlock(final BlockPos pos, final EnumFacing facing, final Vec3d vec, final EnumHand hand) {
        this.pos = pos;
        this.facing = facing;
        this.vec = vec;
        this.hand = hand;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public void setPos(final BlockPos pos) {
        this.pos = pos;
    }
    
    public EnumFacing getFacing() {
        return this.facing;
    }
    
    public void setFacing(final EnumFacing facing) {
        this.facing = facing;
    }
    
    public Vec3d getVec() {
        return this.vec;
    }
    
    public void setVec(final Vec3d vec) {
        this.vec = vec;
    }
    
    public EnumHand getHand() {
        return this.hand;
    }
    
    public void setHand(final EnumHand hand) {
        this.hand = hand;
    }
}
