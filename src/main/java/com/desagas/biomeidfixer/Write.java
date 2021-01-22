package com.desagas.biomeidfixer;

import com.google.gson.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import javafx.stage.DirectoryChooser;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.MinecraftVersion;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.server.ServerLifecycleEvent;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;

// TODO Save config in world folder on both Server and Client worlds.

public class Write {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected int biomeId;
    protected String biomeLocation;
    protected static final Map<Integer, String> biomes = new HashMap<Integer, String>();
    protected static final String masterFile = "BiomeIdFixer.json";
    protected File pathToMaster;
    protected boolean worldLoaded;

    private static final FolderName FOLDER = new FolderName(BiomeIdFixer.MOD_ID);

    public int getOrTryBiomeAssignment (int biomeId, String biomeLocation) {
        this.biomeId = biomeId;
        this.biomeLocation = biomeLocation;

        boolean shoudlProceed = false;

        // TODO Fix this, I need to get the name of the world and its folder, after it is clicked to open, but before it registers the biomes.
        if (isServer()) {
            // TODO, need to create function similar to isOrCreateFolder, below, for the server. I need to find out how to know if the world is loaded or not on the server.
            shoudlProceed = true;
        } else {
//            if (isLocalWorld()) { // Works, but doing this pushes off until the world is loaded.
                shoudlProceed = true;
////                if (isISLoaded()) { // Works, but doing this pushes off until the world is laoded, and this is where I can make an actual file within the world.
////                    if (isOrCreateFolder()) { // Works, this creates the folders and file in the world save.
////                    }
////                }
//            }
        }

        if (shoudlProceed) {

            StringBuilder feedback = new StringBuilder();
            feedback.append("Desagas: request for biomeId ").append(this.biomeId).append(" at ").append(this.biomeLocation);

            if (masterExists()) {
                if (biomes.isEmpty()) {
                    new Read().importBiomeMap();
                }
            } else {
                if (!createMaster()) {
                    return -1;
                }
            }

            int returnId = -1;

            switch (canAssignIdtoBiome()) {
                case 0: // Already assigned;
                    returnId = this.biomeId;
                    feedback.append(" - already assigned.");
                    break;
                case 1: // Not assigned;
                    returnId = assignId();
                    feedback.append(" - now assigned.");
                    break;
                case 2: // Biome assigned, not to the id;
                    returnId = findId();
                    feedback.append(" - not assigned: stored biomeId ").append(returnId).append(" used instead.");
                    break;
                case 3: // Id assigned, not to the biome;
                    returnId = getOrTryBiomeAssignment(this.biomeId + 1, this.biomeLocation);
                    feedback.append(" - not assigned: newly registered biome assigned biomeId ").append(returnId);
                    break;
                default:
                    throw new IllegalStateException("Desagas: unexpected canAssignIdtoBiome() logic value assigned: " + canAssignIdtoBiome());
            }

            LOGGER.info(feedback);

            writeJson(getPrettyJsonString());

            return returnId;
        }

        return this.biomeId;
    }

    // TODO make it world specific.
    private boolean isServer () {
        boolean isServerMachine = FMLEnvironment.dist.isDedicatedServer();;
        LOGGER.debug(!isServerMachine ? "Desagas is on Local Machine" : "Desagas is on Server Machine");
        return isServerMachine;
    }

    private boolean isLocalWorld () {
        boolean isLocal = Minecraft.getInstance().isSingleplayer();
        LOGGER.debug(isLocal ? "Desagas is in Local World" : "Desagas is in Remote World");
        return isLocal;
    }

    private boolean isISLoaded () {
        boolean isISLoaded = Minecraft.getInstance().getIntegratedServer() != null;
        LOGGER.debug("Desagas: Integrated Server is Loaded.");
        return isISLoaded;
    }

    private boolean isOrCreateFolder () {
        this.pathToMaster = ServerLifecycleHooks.getCurrentServer().func_240776_a_(FOLDER).toFile();
        return this.pathToMaster.exists() || this.pathToMaster.mkdirs();
    }

    private int assignId () {
        biomes.put(this.biomeId, this.biomeLocation);
        return this.biomeId;
    }

    private int findId () {
        Iterator huntId = biomes.entrySet().iterator();
        while (huntId.hasNext()) {
            Map.Entry pair = (Map.Entry)huntId.next();
            LOGGER.debug("Desagas: checked " + this.biomeLocation + " against " + pair.getValue());
            if (pair.getValue().equals(this.biomeLocation)) { LOGGER.debug("       : found a match!"); return (int)pair.getKey(); }
        }

        return -1;
    }

    private String getPrettyJsonString () {
        Gson uglyGson = new Gson();
        JsonParser parseJson = new JsonParser();
        Gson betterGson = new GsonBuilder().setPrettyPrinting().create();

        // Map is loaded in order OUTSIDE of the dev environment, but random within.
        String uglyJson = uglyGson.toJson(biomes);
        JsonElement parsedJson = parseJson.parse(uglyJson);

        return betterGson.toJson(parsedJson);
    }

    private boolean masterExists () {
        File f = new File(masterFile);
        return f.exists();
    }

    private boolean createMaster () {
        try { FileWriter master = new FileWriter(masterFile);
            LOGGER.info("Desagas: created master list of mapped biomes at " + masterFile);
            return true;
        } catch (IOException e) { e.printStackTrace();
            LOGGER.error("Desagas: could not create master list of mapped biomes.");
            return false;
        }
    }

    private int canAssignIdtoBiome () {
        boolean a, b, c = false;
        a = biomes.containsKey(this.biomeId);
        b = biomes.containsValue(this.biomeLocation);
        if (a && b) { c = biomes.get(this.biomeId).equals(this.biomeLocation); }

        return  (a && b) && c ? 0 : // Both already assigned and matching; !!! IMPORTANT <-- Must go first.
                !a && !b ? 1 : // Neither assigned; !!! IMPORTANT <-- Can go anywhere.
                b && !c ? 2 : // Biome assigned but does not match key; !!! IMPORTANT <-- Do before assigning new key to add.
                !b ? 3 : 9; // Biome not assigned but key is assigned; !!! <-- Do last.
    }

    private void writeJson (String prettyString) {
        try {
            FileWriter jsonWriter = new FileWriter(masterFile);
            jsonWriter.write(prettyString);
            jsonWriter.flush();
            LOGGER.debug("Desagas: updated master list of mapped biomes.");
        } catch (IOException e) {
            LOGGER.error("Desagas: could not update master list of mapped biomes.");
            e.printStackTrace();
        }
    }
}