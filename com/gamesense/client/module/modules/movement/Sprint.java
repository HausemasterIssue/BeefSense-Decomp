



package com.gamesense.client.module.modules.movement;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import com.gamesense.api.util.world.*;

public class Sprint extends Module
{
    Setting.Boolean reverseSprint;
    @EventHandler
    private final Listener<JumpEvent> jumpEventListener;
    
    public Sprint() {
        super("Sprint", Module.Category.Movement);
        this.jumpEventListener = (Listener<JumpEvent>)new Listener(event -> {
            if (this.reverseSprint.getValue()) {
                final double[] direction = MotionUtil.forward(0.01745329238474369);
                event.getLocation().setX(direction[0] * 0.20000000298023224);
                event.getLocation().setZ(direction[1] * 0.20000000298023224);
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        this.reverseSprint = this.registerBoolean("Reverse", false);
    }
    
    public void onUpdate() {
        if (Sprint.mc.player == null) {
            return;
        }
        if (Sprint.mc.gameSettings.keyBindSneak.isKeyDown()) {
            Sprint.mc.player.setSprinting(false);
        }
        else {
            if (Sprint.mc.player.getFoodStats().getFoodLevel() > 6 && this.reverseSprint.getValue()) {
                if (Sprint.mc.player.field_191988_bg == 0.0f) {
                    if (Sprint.mc.player.moveStrafing == 0.0f) {
                        return;
                    }
                }
            }
            else if (Sprint.mc.player.field_191988_bg <= 0.0f) {
                return;
            }
            Sprint.mc.player.setSprinting(true);
        }
    }
}
