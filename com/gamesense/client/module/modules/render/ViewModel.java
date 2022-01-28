



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import java.util.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import com.gamesense.client.*;
import net.minecraftforge.common.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.*;

public class ViewModel extends Module
{
    public Setting.Boolean cancelEating;
    Setting.Mode type;
    Setting.Double xRight;
    Setting.Double yRight;
    Setting.Double zRight;
    Setting.Double xLeft;
    Setting.Double yLeft;
    Setting.Double zLeft;
    Setting.Double fov;
    @EventHandler
    private final Listener<TransformSideFirstPersonEvent> eventListener;
    
    public ViewModel() {
        super("ViewModel", Module.Category.Render);
        this.eventListener = (Listener<TransformSideFirstPersonEvent>)new Listener(event -> {
            if (this.type.getValue().equalsIgnoreCase("Value") || this.type.getValue().equalsIgnoreCase("Both")) {
                if (event.getEnumHandSide() == EnumHandSide.RIGHT) {
                    GlStateManager.translate(this.xRight.getValue(), this.yRight.getValue(), this.zRight.getValue());
                }
                else if (event.getEnumHandSide() == EnumHandSide.LEFT) {
                    GlStateManager.translate(this.xLeft.getValue(), this.yLeft.getValue(), this.zLeft.getValue());
                }
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        final ArrayList<String> types = new ArrayList<String>();
        types.add("Value");
        types.add("FOV");
        types.add("Both");
        this.type = this.registerMode("Type", (List)types, "Value");
        this.cancelEating = this.registerBoolean("No Eat", false);
        this.xLeft = this.registerDouble("Left X", 0.0, -2.0, 2.0);
        this.yLeft = this.registerDouble("Left Y", 0.2, -2.0, 2.0);
        this.zLeft = this.registerDouble("Left Z", -1.2, -2.0, 2.0);
        this.xRight = this.registerDouble("Right X", 0.0, -2.0, 2.0);
        this.yRight = this.registerDouble("Right Y", 0.2, -2.0, 2.0);
        this.zRight = this.registerDouble("Right Z", -1.2, -2.0, 2.0);
        this.fov = this.registerDouble("Item FOV", 130.0, 70.0, 200.0);
    }
    
    @SubscribeEvent
    public void onFov(final EntityViewRenderEvent.FOVModifier event) {
        if (this.type.getValue().equalsIgnoreCase("FOV") || this.type.getValue().equalsIgnoreCase("Both")) {
            event.setFOV((float)this.fov.getValue());
        }
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }
}
