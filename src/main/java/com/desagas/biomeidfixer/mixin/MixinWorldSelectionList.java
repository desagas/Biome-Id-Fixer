package com.desagas.biomeidfixer.mixin;

import net.minecraft.client.gui.screen.WorldSelectionList;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.Entry.class)
public final class MixinWorldSelectionList {
    @Shadow private final WorldSummary field_214451_d;

    public MixinWorldSelectionList(WorldSummary field_214451_d) {
        this.field_214451_d = field_214451_d;
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/gui/screen/WorldSelectionList$Entry;func_241653_f_()V", cancellable = true)
    public void func_214444_c(CallbackInfo ci) {
        // Used to get path to world folder, without changing function of method, sent to Write.
        // I use this method because when calling the init() for entry, it does not send the folder name all the time.
        new com.desagas.biomeidfixer.Write().writeTemp(String.valueOf(this.field_214451_d.getFileName()), false);
    }
}
