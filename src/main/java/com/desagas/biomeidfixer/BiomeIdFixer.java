package com.desagas.biomeidfixer;

import java.io.File;
import net.minecraft.client.entity.player.ClientPlayerEntity;
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

    @SubscribeEvent // Clear loaded mapping file for next map loaded.
    public void onServerStopping(FMLServerStoppingEvent event) {
        if (event.getServer().isSinglePlayer()) {
            File thisString = event.getServer().getWorldIconFile();
            new Write().writeTemp(String.valueOf(thisString), true);
        }
    }

    @SubscribeEvent // Create master mapping file instnace for new world creation. If multiple worlds are opened at once from same player machine, it may break.
    public void onServerStarting(FMLServerStartingEvent event) {
        if (event.getServer().isSinglePlayer()) {
            File thisString = event.getServer().getWorldIconFile();
            new Write().writeTemp(String.valueOf(thisString), false);
        }
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        new Write().transferTemp(false); // Transfer and remove temporary new biome map.
    }

    @SubscribeEvent // Create master mapping file instnace for new world creation.
    public void onClient(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote()) { // If not called, servers call error for next line.
            if (event.getEntity().getClass() == ClientPlayerEntity.class) { // Only client entity, as we only care about if we made it into the world.
                new Write().transferTemp(true); // Do not update or create anything, simply delete the temp master list created, as it does nothing, but can inject whenloading another world.
            }
        }
    }
}