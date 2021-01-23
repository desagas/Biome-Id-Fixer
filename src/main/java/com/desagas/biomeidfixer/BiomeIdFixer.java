package com.desagas.biomeidfixer;

import java.io.File;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
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

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        File thisString = event.getServer().getWorldIconFile();
        LOGGER.info("Desagas: from server stopping:" + thisString);
        Write.writeTemp(String.valueOf(thisString), true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        File thisString = event.getServer().getWorldIconFile();
        LOGGER.info("Desagas: from server starting:" + thisString);
        Write.writeTemp(String.valueOf(thisString), false);
    }
}