



package com.gamesense.client.module.modules.gui;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;
import net.minecraft.util.text.*;
import java.awt.*;
import com.gamesense.api.util.render.*;

public class ColorMain extends Module
{
    public static Setting.Mode colorModel;
    public static Setting.Mode friendColor;
    public static Setting.Mode enemyColor;
    public static Setting.Mode chatEnableColor;
    public static Setting.Mode chatDisableColor;
    public static Setting.Boolean customFont;
    public static Setting.Boolean textFont;
    
    public ColorMain() {
        super("Colors", Module.Category.GUI);
        this.setDrawn(false);
    }
    
    public void setup() {
        final ArrayList<String> tab = new ArrayList<String>();
        tab.add("Black");
        tab.add("Dark Green");
        tab.add("Dark Red");
        tab.add("Gold");
        tab.add("Dark Gray");
        tab.add("Green");
        tab.add("Red");
        tab.add("Yellow");
        tab.add("Dark Blue");
        tab.add("Dark Aqua");
        tab.add("Dark Purple");
        tab.add("Gray");
        tab.add("Blue");
        tab.add("Aqua");
        tab.add("Light Purple");
        tab.add("White");
        final ArrayList<String> models = new ArrayList<String>();
        models.add("RGB");
        models.add("HSB");
        ColorMain.customFont = this.registerBoolean("Custom Font", true);
        ColorMain.textFont = this.registerBoolean("Custom Text", false);
        ColorMain.friendColor = this.registerMode("Friend Color", (List)tab, "Blue");
        ColorMain.enemyColor = this.registerMode("Enemy Color", (List)tab, "Red");
        ColorMain.chatEnableColor = this.registerMode("Msg Enbl", (List)tab, "Green");
        ColorMain.chatDisableColor = this.registerMode("Msg Dsbl", (List)tab, "Red");
        ColorMain.colorModel = this.registerMode("Color Model", (List)models, "HSB");
    }
    
    public void onEnable() {
        this.disable();
    }
    
    private static TextFormatting settingToFormatting(final Setting.Mode setting) {
        if (setting.getValue().equalsIgnoreCase("Black")) {
            return TextFormatting.BLACK;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Green")) {
            return TextFormatting.DARK_GREEN;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Red")) {
            return TextFormatting.DARK_RED;
        }
        if (setting.getValue().equalsIgnoreCase("Gold")) {
            return TextFormatting.GOLD;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Gray")) {
            return TextFormatting.DARK_GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Green")) {
            return TextFormatting.GREEN;
        }
        if (setting.getValue().equalsIgnoreCase("Red")) {
            return TextFormatting.RED;
        }
        if (setting.getValue().equalsIgnoreCase("Yellow")) {
            return TextFormatting.YELLOW;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Blue")) {
            return TextFormatting.DARK_BLUE;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Aqua")) {
            return TextFormatting.DARK_AQUA;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Purple")) {
            return TextFormatting.DARK_PURPLE;
        }
        if (setting.getValue().equalsIgnoreCase("Gray")) {
            return TextFormatting.GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Blue")) {
            return TextFormatting.BLUE;
        }
        if (setting.getValue().equalsIgnoreCase("Light Purple")) {
            return TextFormatting.LIGHT_PURPLE;
        }
        if (setting.getValue().equalsIgnoreCase("White")) {
            return TextFormatting.WHITE;
        }
        if (setting.getValue().equalsIgnoreCase("Aqua")) {
            return TextFormatting.AQUA;
        }
        return null;
    }
    
    public static TextFormatting getFriendColor() {
        return settingToFormatting(ColorMain.friendColor);
    }
    
    public static TextFormatting getEnemyColor() {
        return settingToFormatting(ColorMain.enemyColor);
    }
    
    public static TextFormatting getEnabledColor() {
        return settingToFormatting(ColorMain.chatEnableColor);
    }
    
    public static TextFormatting getDisabledColor() {
        return settingToFormatting(ColorMain.chatDisableColor);
    }
    
    private static Color settingToColor(final Setting.Mode setting) {
        if (setting.getValue().equalsIgnoreCase("Black")) {
            return Color.BLACK;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Green")) {
            return Color.GREEN.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Dark Red")) {
            return Color.RED.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Gold")) {
            return Color.yellow.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Dark Gray")) {
            return Color.DARK_GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Green")) {
            return Color.green;
        }
        if (setting.getValue().equalsIgnoreCase("Red")) {
            return Color.red;
        }
        if (setting.getValue().equalsIgnoreCase("Yellow")) {
            return Color.yellow;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Blue")) {
            return Color.blue.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Dark Aqua")) {
            return Color.CYAN.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Dark Purple")) {
            return Color.MAGENTA.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Gray")) {
            return Color.GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Blue")) {
            return Color.blue;
        }
        if (setting.getValue().equalsIgnoreCase("Light Purple")) {
            return Color.magenta;
        }
        if (setting.getValue().equalsIgnoreCase("White")) {
            return Color.WHITE;
        }
        if (setting.getValue().equalsIgnoreCase("Aqua")) {
            return Color.cyan;
        }
        return Color.WHITE;
    }
    
    public static GSColor getFriendGSColor() {
        return new GSColor(settingToColor(ColorMain.friendColor));
    }
    
    public static GSColor getEnemyGSColor() {
        return new GSColor(settingToColor(ColorMain.enemyColor));
    }
}
