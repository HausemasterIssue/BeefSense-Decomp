



package com.gamesense.mixin;

import net.minecraftforge.fml.relauncher.*;
import com.gamesense.client.*;
import org.spongepowered.asm.launch.*;
import org.spongepowered.asm.mixin.*;
import javax.annotation.*;
import java.util.*;

@IFMLLoadingPlugin.Name("KiefSense")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class GameSenseMixinLoader implements IFMLLoadingPlugin
{
    private static boolean isObfuscatedEnvironment;
    
    public GameSenseMixinLoader() {
        GameSense.LOGGER.info("Mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.gamesense.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        GameSense.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }
    
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    public String getModContainerClass() {
        return null;
    }
    
    @Nullable
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
        GameSenseMixinLoader.isObfuscatedEnvironment = data.get("runtimeDeobfuscationEnabled");
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
    
    static {
        GameSenseMixinLoader.isObfuscatedEnvironment = false;
    }
}
