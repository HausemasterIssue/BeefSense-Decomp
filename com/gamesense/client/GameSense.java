



package com.gamesense.client;

import net.minecraftforge.fml.common.*;
import com.gamesense.api.util.misc.*;
import com.gamesense.api.event.*;
import com.gamesense.api.util.font.*;
import com.gamesense.api.setting.*;
import com.gamesense.api.util.player.friend.*;
import com.gamesense.api.util.player.enemy.*;
import com.gamesense.client.clickgui.*;
import com.gamesense.api.util.render.*;
import net.minecraftforge.fml.common.event.*;
import org.lwjgl.opengl.*;
import java.awt.*;
import com.gamesense.client.module.*;
import com.gamesense.client.command.*;
import com.gamesense.api.config.*;
import org.apache.logging.log4j.*;
import me.zero.alpine.*;

@Mod(modid = "kiefsense", name = "KiefSense", version = "v0.0.1")
public class GameSense
{
    public static final String MODNAME = "KiefSense";
    public static final String MODID = "kiefsense";
    public static final String MODVER = "v0.0.1";
    public static final Logger LOGGER;
    public static final EventBus EVENT_BUS;
    @Mod.Instance
    private static GameSense INSTANCE;
    public VersionChecker versionChecker;
    public EventProcessor eventProcessor;
    public CFontRenderer cFontRenderer;
    public SettingsManager settingsManager;
    public Friends friends;
    public Enemies enemies;
    public GameSenseGUI gameSenseGUI;
    public SaveConfig saveConfig;
    public LoadConfig loadConfig;
    public CapeUtil capeUtil;
    
    public GameSense() {
        GameSense.INSTANCE = this;
    }
    
    public static GameSense getInstance() {
        return GameSense.INSTANCE;
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        Display.setTitle("KiefSense v0.0.1");
        GameSense.LOGGER.info("Starting up KiefSense v0.0.1!");
        this.startClient();
        GameSense.LOGGER.info("Finished initialization for KiefSense v0.0.1!");
    }
    
    private void startClient() {
        GameSense.LOGGER.info("Version checked!");
        (this.eventProcessor = new EventProcessor()).init();
        GameSense.LOGGER.info("Events initialized!");
        this.cFontRenderer = new CFontRenderer(new Font("Verdana", 0, 18), true, true);
        GameSense.LOGGER.info("Custom font initialized!");
        this.settingsManager = new SettingsManager();
        GameSense.LOGGER.info("Settings initialized!");
        this.friends = new Friends();
        this.enemies = new Enemies();
        GameSense.LOGGER.info("Friends and enemies initialized!");
        ModuleManager.init();
        GameSense.LOGGER.info("Modules initialized!");
        this.gameSenseGUI = new GameSenseGUI();
        GameSense.LOGGER.info("GameSenseGUI initialized!");
        CommandManager.registerCommands();
        GameSense.LOGGER.info("Commands initialized!");
        this.saveConfig = new SaveConfig();
        this.loadConfig = new LoadConfig();
        Runtime.getRuntime().addShutdownHook((Thread)new ConfigStopper());
        GameSense.LOGGER.info("Config initialized!");
        this.capeUtil = new CapeUtil();
        GameSense.LOGGER.info("Capes initialized!");
    }
    
    static {
        LOGGER = LogManager.getLogger("KiefSense");
        EVENT_BUS = (EventBus)new EventManager();
    }
}
