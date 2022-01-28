



package com.gamesense.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.entity.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ EntityPlayerSP.class })
public interface AccessorEntityPlayerSP
{
    @Accessor("handActive")
    void gsSetHandActive(final boolean p0);
}
