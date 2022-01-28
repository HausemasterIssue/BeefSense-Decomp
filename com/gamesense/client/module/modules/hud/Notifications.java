



package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.hud.*;
import net.minecraft.util.text.*;
import java.util.*;
import java.awt.*;

public class Notifications extends HUDModule
{
    private static Setting.Boolean sortUp;
    private static Setting.Boolean sortRight;
    public static Setting.Boolean disableChat;
    private static NotificationsList list;
    private static int waitCounter;
    
    public Notifications() {
        super("Notifications", new Point(0, 50));
    }
    
    public void setup() {
        Notifications.sortUp = this.registerBoolean("Sort Up", false);
        Notifications.sortRight = this.registerBoolean("Sort Right", false);
        Notifications.disableChat = this.registerBoolean("No Chat Msg", true);
    }
    
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, Notifications.list);
    }
    
    public void onUpdate() {
        if (Notifications.waitCounter < 500) {
            ++Notifications.waitCounter;
            return;
        }
        Notifications.waitCounter = 0;
        if (Notifications.list.list.size() > 0) {
            Notifications.list.list.remove(0);
        }
    }
    
    public static void addMessage(final TextComponentString m) {
        if (Notifications.list.list.size() < 3) {
            Notifications.list.list.remove(m);
            Notifications.list.list.add(m);
        }
        else {
            Notifications.list.list.remove(0);
            Notifications.list.list.remove(m);
            Notifications.list.list.add(m);
        }
    }
    
    static {
        Notifications.list = new NotificationsList();
    }
    
    private static class NotificationsList implements HUDList
    {
        public List<TextComponentString> list;
        
        private NotificationsList() {
            this.list = new ArrayList<TextComponentString>();
        }
        
        @Override
        public int getSize() {
            return this.list.size();
        }
        
        @Override
        public String getItem(final int index) {
            return this.list.get(index).getText();
        }
        
        @Override
        public Color getItemColor(final int index) {
            return new Color(255, 255, 255);
        }
        
        @Override
        public boolean sortUp() {
            return Notifications.sortUp.isOn();
        }
        
        @Override
        public boolean sortRight() {
            return Notifications.sortRight.isOn();
        }
    }
}
