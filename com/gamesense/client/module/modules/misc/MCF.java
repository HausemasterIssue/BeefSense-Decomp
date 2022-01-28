



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import net.minecraftforge.fml.common.gameevent.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import com.gamesense.client.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import org.lwjgl.input.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.misc.*;

public class MCF extends Module
{
    @EventHandler
    private final Listener<InputEvent.MouseInputEvent> listener;
    
    public MCF() {
        super("MCF", Module.Category.Misc);
        this.listener = (Listener<InputEvent.MouseInputEvent>)new Listener(event -> {
            if (MCF.mc.objectMouseOver.typeOfHit.equals((Object)RayTraceResult.Type.ENTITY) && MCF.mc.objectMouseOver.entityHit instanceof EntityPlayer && Mouse.isButtonDown(2)) {
                if (Friends.isFriend(MCF.mc.objectMouseOver.entityHit.getName())) {
                    Friends.delFriend(MCF.mc.objectMouseOver.entityHit.getName());
                    MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Removed " + MCF.mc.objectMouseOver.entityHit.getName() + " from friends list");
                }
                else {
                    Friends.addFriend(MCF.mc.objectMouseOver.entityHit.getName());
                    MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "Added " + MCF.mc.objectMouseOver.entityHit.getName() + " to friends list");
                }
            }
        }, new Predicate[0]);
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
    }
}
