package com.desagas.biomeidfixer;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BiomeIdFixer.MOD_ID)
public class BiomeIdFixer {
    public static final String MOD_ID = "biomeidfixer";
    public static final String NAME = "Biome Id Fixer";

    protected static final Logger LOGGER = LogManager.getLogger();

    public BiomeIdFixer() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}