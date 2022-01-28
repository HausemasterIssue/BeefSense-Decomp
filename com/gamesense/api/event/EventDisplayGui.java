



package com.gamesense.api.event;

import net.minecraft.client.gui.*;

public class EventDisplayGui extends EventCancellable
{
    private GuiScreen screen;
    
    public EventDisplayGui(final GuiScreen screen) {
        this.screen = screen;
    }
    
    public GuiScreen getScreen() {
        return this.screen;
    }
    
    public void setScreen(final GuiScreen screen) {
        this.screen = screen;
    }
}
