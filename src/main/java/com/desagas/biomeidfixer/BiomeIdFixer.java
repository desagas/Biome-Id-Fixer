package com.desagas.biomeidfixer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
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

    @SubscribeEvent // Create master mapping file instnace for new world creation. If multiple worlds are opened at once from same player machine, it may break.
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("Desagas: started 'FMLServerStartingEvent' processes.");
        if (event.getServer().isSingleplayer()) {
            String thisString = event.getServer().getWorldScreenshotFile().getParent();
            new Write().writeTemp(thisString);
        }
        LOGGER.info("Desagas: ended 'FMLServerStartingEvent' processes.");
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        LOGGER.info("Desagas: started 'FMLServerStartedEvent' processes.");
        new Write().transferTemp(false); // Transfer and remove temporary new biome map.
        if (!event.getServer().isSingleplayer()) {
            new Write().transferTemp(true); // Transfer and remove temporary new biome map.
        }
        LOGGER.info("Desagas: ended 'FMLServerStartedEvent' processes.");
    }

    @SubscribeEvent // Clear loaded mapping file for next map loaded.
    public void onServerStopping(FMLServerStoppingEvent event) {
        LOGGER.info("Desagas: started 'FMLServerStoppingEvent' processes.");
        if (event.getServer().isSingleplayer()) {
            new Write().stopServer(); // Transfer and remove temporary new biome map.
        }
        LOGGER.info("Desagas: ended 'FMLServerStoppingEvent' processes.");
    }
}