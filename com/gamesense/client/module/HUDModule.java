



package com.gamesense.client.module;

import java.awt.*;
import com.lukflug.panelstudio.theme.*;
import com.gamesense.client.*;
import com.lukflug.panelstudio.*;

public abstract class HUDModule extends Module
{
    protected FixedComponent component;
    protected Point position;
    
    public HUDModule(final String title, final Point defaultPos) {
        super(title, Category.HUD);
        this.position = defaultPos;
    }
    
    public abstract void populate(final Theme p0);
    
    public FixedComponent getComponent() {
        return this.component;
    }
    
    public void resetPosition() {
        this.component.setPosition(GameSense.getInstance().gameSenseGUI.guiInterface, this.position);
    }
}
