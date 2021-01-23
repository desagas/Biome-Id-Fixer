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
    protected static String pathToMaster;
    protected static final String tempFileName = "biomeidfixer.temp";
    protected static final String masterFileName = "BiomeIdFixer.json";
    protected static final String serverProperties = "server.properties";
    protected static final Map<Integer, String> biomes = new HashMap<Integer, String>();

    public int getOrTryBiomeAssignment(int biomeId, String biomeLocation) {
        this.biomeId = biomeId;
        this.biomeLocation = biomeLocation;

        if (buildPathToMaster(new Read().getServerWorldFolder(isServer() ? serverProperties : tempFileName))) {

            if (masterExists()) {
                if (biomes.isEmpty()) {
                    new Read().importBiomeMap();
                }
            } else if (!createMaster()) {
                return -1;
            }

            StringBuilder feedback = new StringBuilder();
            feedback.append("Desagas: request for biomeId ").append(this.biomeId).append(" at ").append(this.biomeLocation);

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

            LOGGER.info("FINAL INT: " + returnId);
            return returnId;
        }

        return this.biomeId;
    }

    private boolean buildPathToMaster(String worldFolder) {
        if (worldFolder != null) {
            File masterFolder = new File(worldFolder + "/" + BiomeIdFixer.MOD_ID);
            if (isOrCreateFolder(masterFolder)) {
                pathToMaster = masterFolder + "/" + masterFileName;
                return true;
            } else {
                return false;
            }
        } else {
            pathToMaster = "temp" + masterFileName;
        }
        return false;
    }

    // TODO make it world specific.
    private boolean isServer() {
        boolean isServerMachine = FMLEnvironment.dist.isDedicatedServer();
        ;
        LOGGER.info(!isServerMachine ? "Desagas is on Local Machine" : "Desagas is on Server Machine");
        return isServerMachine;
    }

    private boolean isOrCreateFolder(File containingFolder) {
        return containingFolder.exists() || containingFolder.mkdirs();
    }

    private int assignId() {
        biomes.put(this.biomeId, this.biomeLocation);
        return this.biomeId;
    }

    private int findId() {
        Iterator huntId = biomes.entrySet().iterator();
        while (huntId.hasNext()) {
            Map.Entry pair = (Map.Entry) huntId.next();
            LOGGER.debug("Desagas: checked " + this.biomeLocation + " against " + pair.getValue());
            if (pair.getValue().equals(this.biomeLocation)) {
                LOGGER.debug("       : found a match!");
                return (int) pair.getKey();
            }
        }

        return -1;
    }

    private String getPrettyJsonString() {
        Gson uglyGson = new Gson();
        JsonParser parseJson = new JsonParser();
        Gson betterGson = new GsonBuilder().setPrettyPrinting().create();

        // Map is loaded in order OUTSIDE of the dev environment, but random within.
        String uglyJson = uglyGson.toJson(biomes);
        JsonElement parsedJson = parseJson.parse(uglyJson);

        return betterGson.toJson(parsedJson);
    }

    private boolean masterExists() {
        File f = new File(String.valueOf(pathToMaster));
        LOGGER.info("Desagas: master exists at " + pathToMaster);
        return f.exists();
    }

    private boolean createMaster() {
        try {
            FileWriter master = new FileWriter(pathToMaster);
            LOGGER.info("Desagas: created master list of mapped biomes at " + pathToMaster);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Desagas: could not create master list of mapped biomes.");
            return false;
        }
    }

    private int canAssignIdtoBiome() {
        boolean a, b, c = false;
        a = biomes.containsKey(this.biomeId);
        b = biomes.containsValue(this.biomeLocation);
        if (a && b) {
            c = biomes.get(this.biomeId).equals(this.biomeLocation);
        }

        return (a && b) && c ? 0 : // Both already assigned and matching; !!! IMPORTANT <-- Must go first.
                !a && !b ? 1 : // Neither assigned; !!! IMPORTANT <-- Can go anywhere.
                        b && !c ? 2 : // Biome assigned but does not match key; !!! IMPORTANT <-- Do before assigning new key to add.
                                !b ? 3 : 9; // Biome not assigned but key is assigned; !!! <-- Do last.
    }

    private void writeJson(String prettyString) {
        try {
            FileWriter jsonWriter = new FileWriter(pathToMaster);
            jsonWriter.write(prettyString);
            jsonWriter.flush();
            LOGGER.debug("Desagas: updated master list of mapped biomes.");
        } catch (IOException e) {
            LOGGER.error("Desagas: could not update master list of mapped biomes.");
            e.printStackTrace();
        }
    }

    public static void writeTemp(String filePath, boolean clear) {
        biomes.clear();
        LOGGER.info("Desagas filePath:" + filePath);
        filePath = filePath.split("saves/")[1];
        String[] thisString = filePath.split("/");
        String thisPath = "level-name=" + "saves/" + thisString[0];
        try {
            FileWriter tempWriter = new FileWriter(tempFileName);
            tempWriter.write(!clear ? thisPath : "");
            tempWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}