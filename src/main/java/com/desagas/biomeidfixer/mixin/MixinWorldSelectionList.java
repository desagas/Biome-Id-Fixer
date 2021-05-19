package com.desagas.biomeidfixer.mixin;

import net.minecraft.client.gui.screen.WorldSelectionList;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.Mixin;
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

//        org.apache.logging.log4j.LogManager.getLogger().info("Desagas Says: Icon: " + this.summary.getIcon());
//        org.apache.logging.log4j.LogManager.getLogger().info("Desagas Says: LevelName: " + this.summary.getLevelName());
//        org.apache.logging.log4j.LogManager.getLogger().info("Desagas Says: Path: " + FMLPaths.GAMEDIR.get());

        org.apache.logging.log4j.LogManager.getLogger().info("Desagas: sending world '" + this.summary.getLevelName() + "' with folder '" + this.summary.getLevelId() + "' to have its biomes mapped.");
        new com.desagas.biomeidfixer.Write().writeTemp(String.valueOf(this.summary.getIcon().getParent()), false);
    }

    @Shadow private final WorldSummary summary;

    public MixinWorldSelectionList(WorldSummary field_214451_d) {
        this.summary = field_214451_d;
    }

}
