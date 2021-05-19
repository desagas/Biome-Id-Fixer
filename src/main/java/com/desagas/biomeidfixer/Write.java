package com.desagas.biomeidfixer;

import com.google.gson.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;

public class Write {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected int biomeId;
    protected String biomeLocation;
    protected static String pathToMaster;
    protected static final String tempFileName = "biomeidfixer.temp";
    protected static final String masterFileName = "BiomeIdFixer.json";
    protected static final String tempMasterFile = "NewWorld" + masterFileName;
    protected static final String serverProperties = "server.properties";
    protected static final Map<Integer, String> biomes = new HashMap<Integer, String>();
    protected static String sep = "/";

    public int getOrTryBiomeAssignment(int biomeId, String biomeLocation) {
        this.biomeId = biomeId;
        this.biomeLocation = biomeLocation;

        if (buildPathToMaster(new Read().getServerWorldFolder(isServer() ? serverProperties : tempFileName))) {

            if (masterExists()) {
                if (biomes.isEmpty()) {
                    new Read().importBiomeMap(false);
                }
            } else if (!createMaster()) {
                return -1;
            }

            StringBuilder feedback = new StringBuilder();

            int returnId = -1;

            switch (canAssignIdtoBiome()) {
                case 0: // Already assigned;
                    returnId = this.biomeId;
                    feedback.append("Desagas: trying to assign biomeId ").append(this.biomeId).append(" to ").append(this.biomeLocation);
                    feedback.append(" - id already assigned to biome.");
                    LOGGER.debug(feedback);
                    break;
                case 1: // Not assigned;
                    returnId = assignId();
                    feedback.append("Desagas: trying to assign biomeId ").append(this.biomeId).append(" to ").append(this.biomeLocation);
                    feedback.append(" - id now assigned to biome.");
                    LOGGER.info(feedback);
                    break;
                case 2: // Biome assigned, not to the id;
                    returnId = findId();
                    feedback.append("Desagas: trying to assign biomeId ").append(this.biomeId).append(" to ").append(this.biomeLocation);
                    feedback.append(" - not assigned: biome already assigned; previously assigned id ").append(returnId).append(" used instead.");
                    LOGGER.info(feedback);
                    break;
                case 3: // Id assigned, not to the biome;
                    returnId = getOrTryBiomeAssignment(this.biomeId + 1, this.biomeLocation);
                    feedback.append("Desagas: trying to assign biomeId ").append(this.biomeId).append(" to ").append(this.biomeLocation);
                    feedback.append(" - not assigned: id already assigned; previously assigned id ").append(returnId).append(" used instead");
                    LOGGER.info(feedback);
                    break;
                default:
                    throw new IllegalStateException("Desagas: unexpected canAssignIdtoBiome() logic value assigned: " + canAssignIdtoBiome());
            }

            writeJson(getPrettyJsonString());

            return returnId;
        }

        return this.biomeId;
    }

    // If folder does not exist in world directory, create it.
    private boolean buildPathToMaster(String worldFolder) {
        if (!worldFolder.equals("isTemp")) {
            File masterFolder = new File(worldFolder + File.separator + BiomeIdFixer.MOD_ID);
            if (isOrCreateFolder(masterFolder)) {
                pathToMaster = masterFolder + File.separator + masterFileName;
                return true;
            } else {
                return false;
            }
        } else {
            pathToMaster = tempMasterFile;
            LOGGER.debug("Desagas: using temporary master biomemap at '" + pathToMaster + "'");
            return true;
        }
    }

    private boolean isServer() {
        return FMLEnvironment.dist.isDedicatedServer();
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
        LOGGER.debug("Desagas: master biomemap exists at '" + pathToMaster + "'");
        return f.exists();
    }

    private boolean createMaster() {
        try {
            FileWriter master = new FileWriter(pathToMaster);
            LOGGER.info("Desagas: created master biomemap at '" + pathToMaster + "'");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Desagas: could not create master biomemap.");
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
            LOGGER.debug("Desagas: updated master biomemap.");
        } catch (IOException e) {
            LOGGER.error("Desagas: could not update master biomemap.");
            e.printStackTrace();
        }
    }

    // Obtain logo file for world selected, to extrapolate world save folder and save to file for calling from Write, before Integrated or Server loaded.
    public void writeTemp(String worldFolder, boolean clear) {
        biomes.clear();

        String thisPath = "level-name=" + worldFolder;
        LOGGER.debug(!clear ? "Desagas: added '" + thisPath + "' to temporary config file ." : "Desagas: removed '" + thisPath + "' from temporary config file.");

        try {
            FileWriter tempWriter = new FileWriter(tempFileName);
            tempWriter.write(!clear ? thisPath : "");
            tempWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void transferTemp (boolean isPlayeronRemote) {
        if (new File(String.valueOf(tempMasterFile)).exists()) {
            if (!isPlayeronRemote) {
                if (buildPathToMaster(new Read().getServerWorldFolder(isServer() ? serverProperties : tempFileName))) {
                    if (masterExists() || createMaster()) {
                        new Read().importBiomeMap(true);
                        writeJson(getPrettyJsonString());
                    } else {
                        LOGGER.error("Desagas: cannot access biomemap file in world folder for transfering master biomemap.");
                    }
                } else {
                    LOGGER.error("Desagas: cannot access world folder.");
                }
            }
            LOGGER.info(removeTemp(tempMasterFile) ? "Desagas: removed temporary biomemap file after transfering it to world folder." : "Desagas: could not remove temporary biomemap file.");
        }
    }

    protected void stopServer () {
        LOGGER.info(removeTemp(tempFileName) ? "Desagas: removed temporary config file after transfering temporary biomemap file to world folder." : "Desagas: could not remove temporary config file.");
    }

    private boolean removeTemp (String thisFiletoDelete) { return new File(String.valueOf(thisFiletoDelete)).delete(); }

}