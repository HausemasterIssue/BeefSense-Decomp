



package com.gamesense.client.module;

import com.gamesense.client.module.modules.combat.*;
import com.gamesense.client.module.modules.exploits.*;
import com.gamesense.client.module.modules.movement.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.client.module.modules.render.*;
import com.gamesense.client.module.modules.hud.*;
import com.gamesense.client.module.modules.gui.*;
import com.gamesense.client.*;
import net.minecraftforge.client.event.*;
import net.minecraft.client.*;
import com.gamesense.api.util.render.*;
import com.gamesense.api.event.events.*;
import java.util.*;

public class ModuleManager
{
    private static LinkedHashMap<Class<? extends Module>, Module> modulesClassMap;
    private static LinkedHashMap<String, Module> modulesNameMap;
    
    public static void init() {
        ModuleManager.modulesClassMap = new LinkedHashMap<Class<? extends Module>, Module>();
        ModuleManager.modulesNameMap = new LinkedHashMap<String, Module>();
        addMod(new AntiCrystal());
        addMod(new AutoAnvil());
        addMod(new AutoArmor());
        addMod(new AutoCrystalGS());
        addMod(new AutoTrap());
        addMod(new AutoWeb());
        addMod(new BedAura());
        addMod(new Blocker());
        addMod(new FastBow());
        addMod(new HoleFill());
        addMod(new KillAura());
        addMod(new OffHand());
        addMod(new PistonCrystal());
        addMod(new SelfTrap());
        addMod(new SelfWeb());
        addMod(new Surround());
        addMod(new FastBreak());
        addMod(new LiquidInteract());
        addMod(new NoInteract());
        addMod(new NoSwing());
        addMod(new Reach());
        addMod(new PacketUse());
        addMod(new PacketXP());
        addMod(new PortalGodmode());
        addMod(new PlaceBypass());
        addMod(new Burrow());
        addMod(new Anchor());
        addMod(new Blink());
        addMod(new HoleTP());
        addMod(new PlayerTweaks());
        addMod(new ReverseStep());
        addMod(new Speed());
        addMod(new Sprint());
        addMod(new Step());
        addMod(new Announcer());
        addMod(new AutoGear());
        addMod(new AutoGG());
        addMod(new AutoReply());
        addMod(new AutoRespawn());
        addMod(new AutoTool());
        addMod(new ChatModifier());
        addMod(new ChatSuffix());
        addMod(new DiscordRPCModule());
        addMod(new FastPlace());
        addMod(new FakePlayer());
        addMod(new HoosiersDupe());
        addMod(new HotbarRefill());
        addMod(new MCF());
        addMod(new MultiTask());
        addMod(new NoEntityTrace());
        addMod(new NoKick());
        addMod(new PhysicsSpammer());
        addMod(new PvPInfo());
        addMod(new SortInventory());
        addMod(new BlockHighlight());
        addMod(new BreakESP());
        addMod(new CapesModule());
        addMod(new Chams());
        addMod(new CityESP());
        addMod(new ESP());
        addMod(new Freecam());
        addMod(new Fullbright());
        addMod(new HitSpheres());
        addMod(new HoleESP());
        addMod(new LogoutSpots());
        addMod(new Nametags());
        addMod(new NoRender());
        addMod(new RenderTweaks());
        addMod(new ShulkerViewer());
        addMod(new SkyColor());
        addMod(new Tracers());
        addMod(new ViewModel());
        addMod(new VoidESP());
        addMod(new ArmorHUD());
        addMod((Module)new ArrayListModule());
        addMod((Module)new CombatInfo());
        addMod((Module)new InventoryViewer());
        addMod((Module)new Notifications());
        addMod((Module)new PotionEffects());
        addMod((Module)new Radar());
        addMod((Module)new TabGUIModule());
        addMod((Module)new TargetHUD());
        addMod((Module)new TargetInfo());
        addMod((Module)new TextRadar());
        addMod((Module)new Watermark());
        addMod((Module)new Welcomer());
        addMod(new ClickGuiModule());
        addMod(new ColorMain());
        addMod(new HUDEditor());
    }
    
    public static void addMod(final Module module) {
        ModuleManager.modulesClassMap.put(module.getClass(), module);
        ModuleManager.modulesNameMap.put(module.getName().toLowerCase(Locale.ROOT), module);
    }
    
    public static void onBind(final int key) {
        if (key == 0) {
            return;
        }
        for (final Module module : getModules()) {
            if (module.getBind() != key) {
                continue;
            }
            module.toggle();
        }
    }
    
    public static void onUpdate() {
        for (final Module module : getModules()) {
            if (!module.isEnabled()) {
                continue;
            }
            module.onUpdate();
        }
    }
    
    public static void onRender() {
        for (final Module module : getModules()) {
            if (!module.isEnabled()) {
                continue;
            }
            module.onRender();
        }
        GameSense.getInstance().gameSenseGUI.render();
    }
    
    public static void onWorldRender(final RenderWorldLastEvent event) {
        Minecraft.getMinecraft().mcProfiler.startSection("kiefsense");
        Minecraft.getMinecraft().mcProfiler.startSection("setup");
        RenderUtil.prepare();
        final RenderEvent e = new RenderEvent(event.getPartialTicks());
        Minecraft.getMinecraft().mcProfiler.endSection();
        for (final Module module : getModules()) {
            if (!module.isEnabled()) {
                continue;
            }
            Minecraft.getMinecraft().mcProfiler.startSection(module.getName());
            module.onWorldRender(e);
            Minecraft.getMinecraft().mcProfiler.endSection();
        }
        Minecraft.getMinecraft().mcProfiler.startSection("release");
        RenderUtil.release();
        Minecraft.getMinecraft().mcProfiler.endSection();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }
    
    public static Collection<Module> getModules() {
        return ModuleManager.modulesClassMap.values();
    }
    
    public static ArrayList<Module> getModulesInCategory(final Module.Category category) {
        final ArrayList<Module> list = new ArrayList<Module>();
        for (final Module module : ModuleManager.modulesClassMap.values()) {
            if (!module.getCategory().equals((Object)category)) {
                continue;
            }
            list.add(module);
        }
        return list;
    }
    
    public static <T extends Module> T getModule(final Class<T> clazz) {
        return (T)ModuleManager.modulesClassMap.get(clazz);
    }
    
    public static Module getModule(final String name) {
        if (name == null) {
            return null;
        }
        return ModuleManager.modulesNameMap.get(name.toLowerCase(Locale.ROOT));
    }
    
    public static boolean isModuleEnabled(final Class<? extends Module> clazz) {
        final Module module = getModule(clazz);
        return module != null && module.isEnabled();
    }
    
    public static boolean isModuleEnabled(final String name) {
        final Module module = getModule(name);
        return module != null && module.isEnabled();
    }
}
