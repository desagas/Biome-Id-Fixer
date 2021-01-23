package com.desagas.biomeidfixer.mixin;

import java.io.File;
import net.minecraft.client.gui.screen.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.Entry.class)
public final class MixinWorldSelectionList {
    @Shadow private File field_214453_f;

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/gui/screen/WorldSelectionList$Entry;func_241653_f_()V", cancellable = true)
    public void func_214444_c(CallbackInfo ci) {
        com.desagas.biomeidfixer.Write.writeTemp(String.valueOf(field_214453_f), false);
    }
}
