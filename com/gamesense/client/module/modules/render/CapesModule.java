



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;

public class CapesModule extends Module
{
    public Setting.Mode capeMode;
    
    public CapesModule() {
        super("Capes", Module.Category.Render);
        this.setDrawn(false);
    }
    
    public void setup() {
        final ArrayList<String> CapeType = new ArrayList<String>();
        CapeType.add("Black");
        CapeType.add("White");
        this.capeMode = this.registerMode("Type", (List)CapeType, "Black");
    }
}
