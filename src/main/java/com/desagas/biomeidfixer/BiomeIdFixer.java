package com.desagas.biomeidfixer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(BiomeIdFixer.MOD_ID)
public class BiomeIdFixer {
    public static final String MOD_ID = "biomeidfixer";

    public BiomeIdFixer() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}