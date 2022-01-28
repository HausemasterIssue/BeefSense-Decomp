



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import java.util.*;

public class HoosiersDupe extends Module
{
    public HoosiersDupe() {
        super("popbobSexDupe", Module.Category.Misc);
    }
    
    public void onEnable() {
        if (HoosiersDupe.mc.player != null) {
            HoosiersDupe.mc.player.sendChatMessage("I just used the popbob sex dupe and got " + (new Random().nextInt(31) + 1) + " shulkers!");
            this.disable();
        }
    }
}
