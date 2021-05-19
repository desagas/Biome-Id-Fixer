package com.desagas.biomeidfixer;

import java.io.File;
import java.nio.file.Path;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
            new Write().writeTemp(thisString, false);
        }
        LOGGER.info("Desagas: ended 'FMLServerStartingEvent' processes.");
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        LOGGER.info("Desagas: started 'FMLServerStartedEvent' processes.");
        new Write().transferTemp(false); // Transfer and remove temporary new biome map.
        LOGGER.info("Desagas: ended 'FMLServerStartedEvent' processes.");
    }

    @SubscribeEvent // Create master mapping file for new server worlds.
    public void onClientJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide()) { // If not called, servers call error for next line.
            if (event.getEntity().getClass() == ClientPlayerEntity.class) { // Only client entity, as we only care about if we made it into the world.
                LOGGER.info("Desagas: started 'EntityJoinWorldEvent' processes.");
                new Write().transferTemp(true); // Do not update or create anything, simply delete the temp master list created, as it does nothing, but can inject when loading another world.
                LOGGER.info("Desagas: ended 'EntityJoinWorldEvent' processes.");
            }
        }
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