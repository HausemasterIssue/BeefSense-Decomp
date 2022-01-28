



package com.gamesense.client.module.modules.combat;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;

public class FastBow extends Module
{
    Setting.Integer drawLength;
    
    public FastBow() {
        super("FastBow", Module.Category.Combat);
    }
    
    public void setup() {
        this.drawLength = this.registerInteger("Draw Length", 3, 3, 21);
    }
    
    public void onUpdate() {
        if (FastBow.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && FastBow.mc.player.isHandActive() && FastBow.mc.player.getItemInUseMaxCount() >= this.drawLength.getValue()) {
            FastBow.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, FastBow.mc.player.getHorizontalFacing()));
            FastBow.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(FastBow.mc.player.getActiveHand()));
            FastBow.mc.player.stopActiveHand();
        }
    }
}
