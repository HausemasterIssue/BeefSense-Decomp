



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;
import com.gamesense.api.event.events.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.api.util.player.enemy.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.world.*;
import com.gamesense.api.util.render.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.item.*;

public class ESP extends Module
{
    Setting.Mode playerESPMode;
    Setting.Mode mobESPMode;
    Setting.Boolean containerRender;
    Setting.Boolean itemRender;
    Setting.Boolean entityRender;
    Setting.Boolean glowCrystals;
    Setting.Integer width;
    Setting.Integer range;
    Setting.ColorSetting mainColor;
    GSColor playerColor;
    GSColor mobColor;
    GSColor mainIntColor;
    GSColor containerColor;
    int opacityGradient;
    
    public ESP() {
        super("ESP", Module.Category.Render);
    }
    
    public void setup() {
        final ArrayList<String> playerEsp = new ArrayList<String>();
        playerEsp.add("None");
        playerEsp.add("Glowing");
        playerEsp.add("Box");
        playerEsp.add("Direction");
        final ArrayList<String> mobEsp = new ArrayList<String>();
        mobEsp.add("None");
        mobEsp.add("Glowing");
        mobEsp.add("Box");
        mobEsp.add("Direction");
        this.mainColor = this.registerColor("Color");
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.width = this.registerInteger("Line Width", 2, 1, 5);
        this.playerESPMode = this.registerMode("Player", (List)playerEsp, "Box");
        this.mobESPMode = this.registerMode("Mob", (List)mobEsp, "Box");
        this.entityRender = this.registerBoolean("Entity", false);
        this.itemRender = this.registerBoolean("Item", true);
        this.containerRender = this.registerBoolean("Container", false);
        this.glowCrystals = this.registerBoolean("Glow Crystal", false);
    }
    
    public void onWorldRender(final RenderEvent event) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: getfield        net/minecraft/client/Minecraft.world:Lnet/minecraft/client/multiplayer/WorldClient;
        //     6: getfield        net/minecraft/client/multiplayer/WorldClient.loadedEntityList:Ljava/util/List;
        //     9: invokeinterface java/util/List.stream:()Ljava/util/stream/Stream;
        //    14: invokedynamic   BootstrapMethod #0, test:()Ljava/util/function/Predicate;
        //    19: invokeinterface java/util/stream/Stream.filter:(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;
        //    24: aload_0         /* this */
        //    25: invokedynamic   BootstrapMethod #1, test:(Lcom/gamesense/client/module/modules/render/ESP;)Ljava/util/function/Predicate;
        //    30: invokeinterface java/util/stream/Stream.filter:(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;
        //    35: aload_0         /* this */
        //    36: invokedynamic   BootstrapMethod #2, accept:(Lcom/gamesense/client/module/modules/render/ESP;)Ljava/util/function/Consumer;
        //    41: invokeinterface java/util/stream/Stream.forEach:(Ljava/util/function/Consumer;)V
        //    46: aload_0         /* this */
        //    47: getfield        com/gamesense/client/module/modules/render/ESP.containerRender:Lcom/gamesense/api/setting/Setting$Boolean;
        //    50: invokevirtual   com/gamesense/api/setting/Setting$Boolean.getValue:()Z
        //    53: ifeq            92
        //    56: getstatic       com/gamesense/client/module/modules/render/ESP.mc:Lnet/minecraft/client/Minecraft;
        //    59: getfield        net/minecraft/client/Minecraft.world:Lnet/minecraft/client/multiplayer/WorldClient;
        //    62: getfield        net/minecraft/client/multiplayer/WorldClient.loadedTileEntityList:Ljava/util/List;
        //    65: invokeinterface java/util/List.stream:()Ljava/util/stream/Stream;
        //    70: aload_0         /* this */
        //    71: invokedynamic   BootstrapMethod #3, test:(Lcom/gamesense/client/module/modules/render/ESP;)Ljava/util/function/Predicate;
        //    76: invokeinterface java/util/stream/Stream.filter:(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;
        //    81: aload_0         /* this */
        //    82: invokedynamic   BootstrapMethod #4, accept:(Lcom/gamesense/client/module/modules/render/ESP;)Ljava/util/function/Consumer;
        //    87: invokeinterface java/util/stream/Stream.forEach:(Ljava/util/function/Consumer;)V
        //    92: return         
        //    StackMapTable: 00 01 FB 00 5C
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.Decompiler.decompile(Decompiler.java:70)
        //     at org.ugp.mc.deobfuscator.Deobfuscator3000.decompile(Deobfuscator3000.java:538)
        //     at org.ugp.mc.deobfuscator.Deobfuscator3000.decompileAndDeobfuscate(Deobfuscator3000.java:552)
        //     at org.ugp.mc.deobfuscator.Deobfuscator3000.processMod(Deobfuscator3000.java:510)
        //     at org.ugp.mc.deobfuscator.Deobfuscator3000.lambda$21(Deobfuscator3000.java:329)
        //     at java.lang.Thread.run(Unknown Source)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void onDisable() {
        ESP.mc.world.loadedEntityList.stream().forEach(entity -> {
            if ((entity instanceof EntityEnderCrystal || entity instanceof EntityPlayer || entity instanceof EntityCreature || entity instanceof EntitySlime || entity instanceof EntitySquid) && entity.isGlowing()) {
                entity.setGlowing(false);
            }
        });
    }
    
    private void defineEntityColors(final Entity entity) {
        if (entity instanceof EntityPlayer) {
            if (Friends.isFriend(entity.getName())) {
                this.playerColor = ColorMain.getFriendGSColor();
            }
            else if (Enemies.isEnemy(entity.getName())) {
                this.playerColor = ColorMain.getEnemyGSColor();
            }
            else {
                this.playerColor = new GSColor(this.mainColor.getValue(), this.opacityGradient);
            }
        }
        if (entity instanceof EntityMob) {
            this.mobColor = new GSColor(255, 0, 0, this.opacityGradient);
        }
        else if (entity instanceof EntityAnimal || entity instanceof EntitySquid) {
            this.mobColor = new GSColor(0, 255, 0, this.opacityGradient);
        }
        else {
            this.mobColor = new GSColor(255, 165, 0, this.opacityGradient);
        }
        if (entity instanceof EntitySlime) {
            this.mobColor = new GSColor(255, 0, 0, this.opacityGradient);
        }
        if (entity != null) {
            this.mainIntColor = new GSColor(this.mainColor.getValue(), this.opacityGradient);
        }
    }
    
    private boolean rangeEntityCheck(final Entity entity) {
        if (entity.getDistanceToEntity((Entity)ESP.mc.player) > this.range.getValue()) {
            return false;
        }
        if (entity.getDistanceToEntity((Entity)ESP.mc.player) >= 180.0f) {
            this.opacityGradient = 50;
        }
        else if (entity.getDistanceToEntity((Entity)ESP.mc.player) >= 130.0f && entity.getDistanceToEntity((Entity)ESP.mc.player) < 180.0f) {
            this.opacityGradient = 100;
        }
        else if (entity.getDistanceToEntity((Entity)ESP.mc.player) >= 80.0f && entity.getDistanceToEntity((Entity)ESP.mc.player) < 130.0f) {
            this.opacityGradient = 150;
        }
        else if (entity.getDistanceToEntity((Entity)ESP.mc.player) >= 30.0f && entity.getDistanceToEntity((Entity)ESP.mc.player) < 80.0f) {
            this.opacityGradient = 200;
        }
        else {
            this.opacityGradient = 255;
        }
        return true;
    }
    
    private boolean rangeTileCheck(final TileEntity tileEntity) {
        if (tileEntity.getDistanceSq(ESP.mc.player.posX, ESP.mc.player.posY, ESP.mc.player.posZ) > this.range.getValue() * this.range.getValue()) {
            return false;
        }
        if (tileEntity.getDistanceSq(ESP.mc.player.posX, ESP.mc.player.posY, ESP.mc.player.posZ) >= 32400.0) {
            this.opacityGradient = 50;
        }
        else if (tileEntity.getDistanceSq(ESP.mc.player.posX, ESP.mc.player.posY, ESP.mc.player.posZ) >= 16900.0 && tileEntity.getDistanceSq(ESP.mc.player.posX, ESP.mc.player.posY, ESP.mc.player.posZ) < 32400.0) {
            this.opacityGradient = 100;
        }
        else if (tileEntity.getDistanceSq(ESP.mc.player.posX, ESP.mc.player.posY, ESP.mc.player.posZ) >= 6400.0 && tileEntity.getDistanceSq(ESP.mc.player.posX, ESP.mc.player.posY, ESP.mc.player.posZ) < 16900.0) {
            this.opacityGradient = 150;
        }
        else if (tileEntity.getDistanceSq(ESP.mc.player.posX, ESP.mc.player.posY, ESP.mc.player.posZ) >= 900.0 && tileEntity.getDistanceSq(ESP.mc.player.posX, ESP.mc.player.posY, ESP.mc.player.posZ) < 6400.0) {
            this.opacityGradient = 200;
        }
        else {
            this.opacityGradient = 255;
        }
        return true;
    }
}
