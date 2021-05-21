package com.desagas.biomeidfixer.mixin;

import net.minecraft.client.gui.screen.WorldSelectionList;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.Entry.class)
public final class MixinWorldSelectionList {

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/gui/screen/WorldSelectionList$Entry;joinWorld()V", cancellable = true)
    public void func_214444_c(CallbackInfo ci) {
        // Used to get path to existing world folders, without changing function of method, sent to Write.
        // I use this method because when calling the init() for entry, it does not send the folder name all the time.

        org.apache.logging.log4j.LogManager.getLogger().info("Desagas: sending path to '" + this.summary.getLevelName() + "' at '" + this.summary.getIcon().getParent() + "' to be stored in the temporary config file.");
        new com.desagas.biomeidfixer.Write().writeTemp(String.valueOf(this.summary.getIcon().getParent()));
    }

    @Mutable @Final @Shadow private final WorldSummary summary;

    public MixinWorldSelectionList(WorldSummary field_214451_d) {
        this.summary = field_214451_d;
    }

}
