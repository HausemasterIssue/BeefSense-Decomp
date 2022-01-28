



package com.gamesense.client.clickgui;

import com.gamesense.client.module.modules.gui.*;
import java.awt.*;
import com.lukflug.panelstudio.mc12.*;
import com.gamesense.api.util.render.*;
import com.gamesense.api.util.font.*;
import java.util.*;
import com.gamesense.client.module.*;
import com.lukflug.panelstudio.hud.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.*;
import com.gamesense.client.*;
import com.gamesense.api.setting.*;
import com.lukflug.panelstudio.settings.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;
import net.minecraft.client.gui.inventory.*;

public class GameSenseGUI extends MinecraftHUDGUI
{
    public static final int WIDTH = 100;
    public static final int HEIGHT = 12;
    public static final int DISTANCE = 10;
    public static final int HUD_BORDER = 2;
    private final Toggleable colorToggle;
    public final GUIInterface guiInterface;
    public final HUDClickGUI gui;
    private final Theme theme;
    private final Theme gamesenseTheme;
    private final Theme clearTheme;
    private final Theme cleargradientTheme;
    
    public GameSenseGUI() {
        final ColorScheme scheme = new SettingsColorScheme((ColorSetting)ClickGuiModule.enabledColor, (ColorSetting)ClickGuiModule.backgroundColor, (ColorSetting)ClickGuiModule.settingBackgroundColor, (ColorSetting)ClickGuiModule.outlineColor, (ColorSetting)ClickGuiModule.fontColor, (NumberSetting)ClickGuiModule.opacity);
        this.gamesenseTheme = new GameSenseTheme(scheme, 12, 2, 5);
        this.clearTheme = new ClearTheme(scheme, false, 12, 1);
        this.cleargradientTheme = new ClearTheme(scheme, true, 12, 1);
        this.theme = new ThemeMultiplexer() {
            @Override
            protected Theme getTheme() {
                if (ClickGuiModule.theme.getValue().equals("2.0")) {
                    return GameSenseGUI.this.clearTheme;
                }
                if (ClickGuiModule.theme.getValue().equals("2.1.2")) {
                    return GameSenseGUI.this.cleargradientTheme;
                }
                return GameSenseGUI.this.gamesenseTheme;
            }
        };
        this.colorToggle = new Toggleable() {
            @Override
            public void toggle() {
                ColorMain.colorModel.increment();
            }
            
            @Override
            public boolean isOn() {
                return ColorMain.colorModel.getValue().equals("HSB");
            }
        };
        this.guiInterface = new GUIInterface(true) {
            @Override
            public void drawString(final Point pos, final String s, final Color c) {
                GLInterface.end();
                int x = pos.x + 2;
                int y = pos.y + 1;
                if (!ColorMain.customFont.getValue()) {
                    ++x;
                    ++y;
                }
                FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), s, x, y, new GSColor(c));
                GLInterface.begin();
            }
            
            @Override
            public int getFontWidth(final String s) {
                return Math.round((float)FontUtil.getStringWidth(ColorMain.customFont.getValue(), s)) + 4;
            }
            
            @Override
            public int getFontHeight() {
                return Math.round((float)FontUtil.getFontHeight(ColorMain.customFont.getValue())) + 2;
            }
            
            public String getResourcePrefix() {
                return "gamesense:gui/";
            }
        };
        this.gui = new HUDClickGUI(this.guiInterface, null) {
            @Override
            public void handleScroll(final int diff) {
                super.handleScroll(diff);
                if (ClickGuiModule.scrolling.getValue().equals("Screen")) {
                    for (final FixedComponent component : this.components) {
                        if (!this.hudComponents.contains(component)) {
                            final Point p = component.getPosition(GameSenseGUI.this.guiInterface);
                            p.translate(0, -diff);
                            component.setPosition(GameSenseGUI.this.guiInterface, p);
                        }
                    }
                }
            }
        };
        final Toggleable hudToggle = new Toggleable() {
            @Override
            public void toggle() {
            }
            
            @Override
            public boolean isOn() {
                return (GameSenseGUI.this.gui.isOn() && ClickGuiModule.showHUD.isOn()) || GameSenseGUI.this.hudEditor;
            }
        };
        for (final Module module : ModuleManager.getModules()) {
            if (module instanceof HUDModule) {
                ((HUDModule)module).populate(this.theme);
                this.gui.addHUDComponent(new HUDPanel(((HUDModule)module).getComponent(), this.theme.getPanelRenderer(), module, new SettingsAnimation((NumberSetting)ClickGuiModule.animationSpeed), hudToggle, 2));
            }
        }
        final Point pos = new Point(10, 10);
        for (final Module.Category category : Module.Category.values()) {
            final DraggableContainer panel = new DraggableContainer(category.name(), null, this.theme.getPanelRenderer(), new SimpleToggleable(false), new SettingsAnimation((NumberSetting)ClickGuiModule.animationSpeed), null, new Point(pos), 100) {
                @Override
                protected int getScrollHeight(final int childHeight) {
                    if (ClickGuiModule.scrolling.getValue().equals("Screen")) {
                        return childHeight;
                    }
                    return Math.min(childHeight, Math.max(48, GameSenseGUI.this.height - this.getPosition(GameSenseGUI.this.guiInterface).y - this.renderer.getHeight(this.open.getValue() != 0.0) - 12));
                }
            };
            this.gui.addComponent(panel);
            pos.translate(110, 0);
            for (final Module module2 : ModuleManager.getModulesInCategory(category)) {
                this.addModule(panel, module2);
            }
        }
    }
    
    private void addModule(final CollapsibleContainer panel, final Module module) {
        final CollapsibleContainer container = new CollapsibleContainer(module.getName(), null, this.theme.getContainerRenderer(), new SimpleToggleable(false), new SettingsAnimation((NumberSetting)ClickGuiModule.animationSpeed), module);
        panel.addComponent(container);
        for (final Setting property : GameSense.getInstance().settingsManager.getSettingsForMod(module)) {
            if (property instanceof Setting.Boolean) {
                container.addComponent(new BooleanComponent(property.getName(), null, this.theme.getComponentRenderer(), (Toggleable)property));
            }
            else if (property instanceof Setting.Integer) {
                container.addComponent(new NumberComponent(property.getName(), null, this.theme.getComponentRenderer(), (NumberSetting)property, ((Setting.Integer)property).getMin(), ((Setting.Integer)property).getMax()));
            }
            else if (property instanceof Setting.Double) {
                container.addComponent(new NumberComponent(property.getName(), null, this.theme.getComponentRenderer(), (NumberSetting)property, ((Setting.Double)property).getMin(), ((Setting.Double)property).getMax()));
            }
            else if (property instanceof Setting.Mode) {
                container.addComponent(new EnumComponent(property.getName(), null, this.theme.getComponentRenderer(), (EnumSetting)property));
            }
            else {
                if (!(property instanceof Setting.ColorSetting)) {
                    continue;
                }
                container.addComponent(new SyncableColorComponent(this.theme, (Setting.ColorSetting)property, this.colorToggle, new SettingsAnimation((NumberSetting)ClickGuiModule.animationSpeed)));
            }
        }
        container.addComponent(new GameSenseKeybind(this.theme.getComponentRenderer(), module));
    }
    
    public static void renderItem(final ItemStack item, final Point pos) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.clear(256);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, pos.x, pos.y);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, item, pos.x, pos.y);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GLInterface.begin();
    }
    
    public static void renderEntity(final EntityLivingBase entity, final Point pos, final int scale) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.clear(256);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GuiInventory.drawEntityOnScreen(pos.x, pos.y, scale, 28.0f, 60.0f, entity);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GLInterface.begin();
    }
    
    @Override
    protected HUDClickGUI getHUDGUI() {
        return this.gui;
    }
    
    @Override
    protected GUIInterface getInterface() {
        return this.guiInterface;
    }
    
    @Override
    protected int getScrollSpeed() {
        return ClickGuiModule.scrollSpeed.getValue();
    }
}
