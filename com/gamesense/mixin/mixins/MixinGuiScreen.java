



package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.gamesense.client.module.modules.render.*;
import com.gamesense.client.module.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ GuiScreen.class })
public class MixinGuiScreen
{
    @Inject(method = { "renderToolTip" }, at = { @At("HEAD") }, cancellable = true)
    public void renderToolTip(final ItemStack stack, final int x, final int y, final CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled((Class)ShulkerViewer.class) && stack.getItem() instanceof ItemShulkerBox && stack.getTagCompound() != null && stack.getTagCompound().hasKey("BlockEntityTag", 10) && stack.getTagCompound().getCompoundTag("BlockEntityTag").hasKey("Items", 9)) {
            callbackInfo.cancel();
            ShulkerViewer.renderShulkerPreview(stack, x + 6, y - 33, 162, 66);
        }
    }
}
