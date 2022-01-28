



package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.util.render.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.hud.*;
import net.minecraft.client.*;
import net.minecraft.item.*;
import com.gamesense.client.clickgui.*;
import java.awt.*;
import net.minecraft.util.*;
import com.lukflug.panelstudio.*;

public class InventoryViewer extends HUDModule
{
    private Setting.ColorSetting fillColor;
    private Setting.ColorSetting outlineColor;
    
    public InventoryViewer() {
        super("InventoryViewer", new Point(0, 10));
    }
    
    public void setup() {
        this.fillColor = this.registerColor("Fill", new GSColor(0, 0, 0, 100));
        this.outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
    }
    
    public void populate(final Theme theme) {
        this.component = new InventoryViewerComponent(theme);
    }
    
    private class InventoryViewerComponent extends HUDComponent
    {
        public InventoryViewerComponent(final Theme theme) {
            super(InventoryViewer.this.getName(), theme.getPanelRenderer(), InventoryViewer.this.position);
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            final Color bgcolor = (Color)new GSColor(InventoryViewer.this.fillColor.getValue(), 100);
            context.getInterface().fillRect(context.getRect(), bgcolor, bgcolor, bgcolor, bgcolor);
            final Color color = (Color)InventoryViewer.this.outlineColor.getValue();
            context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), color, color, color, color);
            final NonNullList<ItemStack> items = (NonNullList<ItemStack>)Minecraft.getMinecraft().player.inventory.mainInventory;
            for (int size = items.size(), item = 9; item < size; ++item) {
                final int slotX = context.getPos().x + item % 9 * 18;
                final int slotY = context.getPos().y + 2 + (item / 9 - 1) * 18;
                GameSenseGUI.renderItem((ItemStack)items.get(item), new Point(slotX, slotY));
            }
        }
        
        @Override
        public int getWidth(final Interface inter) {
            return 162;
        }
        
        @Override
        public void getHeight(final Context context) {
            context.setHeight(56);
        }
    }
}
