



package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.hud.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.api.util.player.enemy.*;
import net.minecraft.client.*;
import com.gamesense.client.module.modules.gui.*;
import net.minecraft.util.text.*;
import java.awt.*;

public class TextRadar extends HUDModule
{
    private Setting.Boolean sortUp;
    private Setting.Boolean sortRight;
    private Setting.Integer range;
    private Setting.Mode display;
    private PlayerList list;
    
    public TextRadar() {
        super("TextRadar", new Point(0, 50));
        this.list = new PlayerList();
    }
    
    public void setup() {
        final ArrayList<String> displayModes = new ArrayList<String>();
        displayModes.add("All");
        displayModes.add("Friend");
        displayModes.add("Enemy");
        this.display = this.registerMode("Display", (List)displayModes, "All");
        this.sortUp = this.registerBoolean("Sort Up", false);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.range = this.registerInteger("Range", 100, 1, 260);
    }
    
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, this.list);
    }
    
    public void onRender() {
        this.list.players.clear();
        TextRadar.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).filter(e -> e != TextRadar.mc.player).forEach(e -> {
            if (TextRadar.mc.player.getDistanceToEntity((Entity)e) <= this.range.getValue()) {
                if (!this.display.getValue().equalsIgnoreCase("Friend") || Friends.isFriend(((Entity)e).getName())) {
                    if (!this.display.getValue().equalsIgnoreCase("Enemy") || Enemies.isEnemy(((Entity)e).getName())) {
                        this.list.players.add(e);
                    }
                }
            }
        });
    }
    
    private class PlayerList implements HUDList
    {
        public List<EntityPlayer> players;
        
        private PlayerList() {
            this.players = new ArrayList<EntityPlayer>();
        }
        
        @Override
        public int getSize() {
            return this.players.size();
        }
        
        @Override
        public String getItem(final int index) {
            final EntityPlayer e = this.players.get(index);
            TextFormatting friendcolor;
            if (Friends.isFriend(e.getName())) {
                friendcolor = ColorMain.getFriendColor();
            }
            else if (Enemies.isEnemy(e.getName())) {
                friendcolor = ColorMain.getEnemyColor();
            }
            else {
                friendcolor = TextFormatting.GRAY;
            }
            final float health = e.getHealth() + e.getAbsorptionAmount();
            TextFormatting healthcolor;
            if (health <= 5.0f) {
                healthcolor = TextFormatting.RED;
            }
            else if (health > 5.0f && health < 15.0f) {
                healthcolor = TextFormatting.YELLOW;
            }
            else {
                healthcolor = TextFormatting.GREEN;
            }
            final float distance = TextRadar.mc.player.getDistanceToEntity((Entity)e);
            TextFormatting distancecolor;
            if (distance < 20.0f) {
                distancecolor = TextFormatting.RED;
            }
            else if (distance >= 20.0f && distance < 50.0f) {
                distancecolor = TextFormatting.YELLOW;
            }
            else {
                distancecolor = TextFormatting.GREEN;
            }
            return TextFormatting.GRAY + "[" + healthcolor + (int)health + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int)distance + TextFormatting.GRAY + "]";
        }
        
        @Override
        public Color getItemColor(final int index) {
            return new Color(255, 255, 255);
        }
        
        @Override
        public boolean sortUp() {
            return TextRadar.this.sortUp.isOn();
        }
        
        @Override
        public boolean sortRight() {
            return TextRadar.this.sortRight.isOn();
        }
    }
}
