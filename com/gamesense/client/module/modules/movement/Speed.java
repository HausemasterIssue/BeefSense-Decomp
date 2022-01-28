



package com.gamesense.client.module.modules.movement;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.util.misc.*;
import com.gamesense.api.event.events.*;
import me.zero.alpine.listener.*;
import java.util.function.*;
import java.util.*;
import com.gamesense.client.*;
import com.gamesense.api.util.world.*;
import net.minecraft.entity.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.init.*;
import net.minecraft.block.*;

public class Speed extends Module
{
    Setting.Boolean timerBool;
    Setting.Double timerVal;
    Setting.Double jumpHeight;
    Setting.Double yPortSpeed;
    Setting.Mode mode;
    private boolean slowDown;
    private double playerSpeed;
    private Timer timer;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener;
    
    public Speed() {
        super("Speed", Module.Category.Movement);
        this.timer = new Timer();
        this.playerMoveEventListener = (Listener<PlayerMoveEvent>)new Listener(event -> {
            if (Speed.mc.player.isInLava() || Speed.mc.player.isInWater() || Speed.mc.player.isOnLadder() || Speed.mc.player.isInWeb) {
                return;
            }
            if (this.mode.getValue().equalsIgnoreCase("Strafe")) {
                double speedY = this.jumpHeight.getValue();
                if (Speed.mc.player.onGround && MotionUtil.isMoving((EntityLivingBase)Speed.mc.player) && this.timer.hasReached(300L)) {
                    EntityUtil.setTimer((float)this.timerVal.getValue());
                    if (Speed.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                        speedY += (Speed.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                    }
                    event.setY(Speed.mc.player.motionY = speedY);
                    this.playerSpeed = MotionUtil.getBaseMoveSpeed() * ((EntityUtil.isColliding(0.0, -0.5, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.9 : 1.901);
                    this.slowDown = true;
                    this.timer.reset();
                }
                else {
                    EntityUtil.resetTimer();
                    if (this.slowDown || Speed.mc.player.isCollidedHorizontally) {
                        final double playerSpeed = this.playerSpeed;
                        double n;
                        if (EntityUtil.isColliding(0.0, -0.8, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) {
                            n = 0.4;
                        }
                        else {
                            final double n2 = 0.7;
                            final double baseMoveSpeed = MotionUtil.getBaseMoveSpeed();
                            this.playerSpeed = baseMoveSpeed;
                            n = n2 * baseMoveSpeed;
                        }
                        this.playerSpeed = playerSpeed - n;
                        this.slowDown = false;
                    }
                    else {
                        this.playerSpeed -= this.playerSpeed / 159.0;
                    }
                }
                this.playerSpeed = Math.max(this.playerSpeed, MotionUtil.getBaseMoveSpeed());
                final double[] dir = MotionUtil.forward(this.playerSpeed);
                event.setX(dir[0]);
                event.setZ(dir[1]);
            }
        }, new Predicate[0]);
    }
    
    public void setup() {
        final ArrayList<String> modes = new ArrayList<String>();
        modes.add("Strafe");
        modes.add("Fake");
        modes.add("YPort");
        this.mode = this.registerMode("Mode", (List)modes, "Strafe");
        this.yPortSpeed = this.registerDouble("Y Port Speed", 0.06, 0.01, 0.15);
        this.jumpHeight = this.registerDouble("Jump Speed", 0.41, 0.0, 1.0);
        this.timerBool = this.registerBoolean("Timer", true);
        this.timerVal = this.registerDouble("Timer Speed", 1.15, 1.0, 1.5);
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe((Object)this);
        this.playerSpeed = MotionUtil.getBaseMoveSpeed();
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe((Object)this);
        this.timer.reset();
        EntityUtil.resetTimer();
    }
    
    public void onUpdate() {
        if (Speed.mc.player == null || Speed.mc.world == null) {
            this.disable();
            return;
        }
        if (this.mode.getValue().equalsIgnoreCase("YPort")) {
            this.handleYPortSpeed();
        }
    }
    
    private void handleYPortSpeed() {
        if (!MotionUtil.isMoving((EntityLivingBase)Speed.mc.player) || (Speed.mc.player.isInWater() && Speed.mc.player.isInLava()) || Speed.mc.player.isCollidedHorizontally) {
            return;
        }
        if (Speed.mc.player.onGround) {
            EntityUtil.setTimer(1.15f);
            Speed.mc.player.jump();
            MotionUtil.setSpeed((EntityLivingBase)Speed.mc.player, MotionUtil.getBaseMoveSpeed() + this.yPortSpeed.getValue());
        }
        else {
            Speed.mc.player.motionY = -1.0;
            EntityUtil.resetTimer();
        }
    }
    
    public String getHudInfo() {
        String t = "";
        if (this.mode.getValue().equalsIgnoreCase("Strafe")) {
            t = "[" + ChatFormatting.WHITE + "Strafe" + ChatFormatting.GRAY + "]";
        }
        else if (this.mode.getValue().equalsIgnoreCase("YPort")) {
            t = "[" + ChatFormatting.WHITE + "YPort" + ChatFormatting.GRAY + "]";
        }
        else if (this.mode.getValue().equalsIgnoreCase("Fake")) {
            t = "[" + ChatFormatting.WHITE + "Fake" + ChatFormatting.GRAY + "]";
        }
        return t;
    }
}
