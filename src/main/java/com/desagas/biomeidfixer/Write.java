package com.desagas.biomeidfixer;

import com.google.gson.*;

import java.io.*;
import java.util.*;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;

public class Write {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected static boolean isTemp;

    protected int biomeId;

    protected static final Map<Integer, String> biomes = new HashMap<Integer, String>();

    protected String regType;
    protected String biomeLocation;
    protected static String pathToMaster;
    private static final String sep = File.separator;
    protected static final String tempFileName = "biomeidfixer.temp";
    protected static final String masterFileName = "BiomeIdFixer.json";
    protected static final String serverProperties = "server.properties";
    protected static final String tempMasterFile = "NewWorld" + masterFileName;

    protected static StringBuilder idFB; // Feedback for Biome Id Assignments
    protected static StringBuilder flFB; // Feedback for File Management

    public int getOrTryBiomeAssignment(int biomeId, String biomeLocation, String regType) {

        this.biomeId = biomeId;
        this.regType = regType;
        this.biomeLocation = biomeLocation;

        flFB = new StringBuilder();

        if (buildPathToMaster(new Read().getServerWorldFolder(isServer() ? serverProperties : tempFileName))) {
            if (masterExists()) {
                if (biomes.isEmpty()) {
                    new Read().importBiomeMap(false);
                }
            } else if (!createMaster()) {
                return -1;
            }

            int returnId = -1; // Fail-Safe, Prevents an NPE.

            idFB = new StringBuilder()
                    .append("Desagas: id ").append(this.biomeId)
                    .append(" for ").append("'").append(this.biomeLocation).append("'");

            switch (canAssignIdtoBiome()) {
                case 0: // Already assigned;
                    returnId = this.biomeId;
                    idFB.append(" was already assigned;");
                    break;
                case 1: // Not assigned;
                    returnId = assignId();
                    idFB.append(" is now assigned;");
                    break;
                case 2: // Biome assigned, not to the id;
                    returnId = findId();
                    idFB.append(" was not assigned (wrong id); previously assigned id ").append(returnId).append(" used instead;");
                    break;
                case 3: // Id assigned, not to the biome;
                    returnId = getOrTryBiomeAssignment(this.biomeId + 1, this.biomeLocation, this.regType);
                    idFB.append(" was not assigned (wrong biome); previously assigned id ").append(returnId).append(" used instead;");
                    break;
                default:
                    throw new IllegalStateException("Desagas: unexpected canAssignIdtoBiome() logic value assigned: " + canAssignIdtoBiome());
            }

            if (flFB.length() != 0) { LOGGER.debug(flFB); }

            idFB.append(" ").append(writeJson(getPrettyJsonString()));
            LOGGER.debug(idFB);

            return returnId;
        }

        return this.biomeId;
    }

    // If folder does not exist in world directory, create it.
    private boolean buildPathToMaster(String worldFolder) {
        if (worldFolder.equals("isTemp")) {
            isTemp = true;
            pathToMaster = tempMasterFile;
            return true;
        } else {
            File masterFolder = new File(worldFolder + sep + BiomeIdFixer.MOD_ID);
            if (isOrCreateFolder(masterFolder)) {
                pathToMaster = masterFolder + sep + masterFileName;
                return true;
            } else {
                return false;
            }
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

    // TODO - Clean Up this Bit, Temporarily removed logging until more streamlined log created.
    private int findId() {
        // StringBuilder mpFB = new StringBuilder().append("Desagas: checking biome '").append(this.biomeLocation).append("' against id: ");
        Iterator huntId = biomes.entrySet().iterator();
        while (huntId.hasNext()) {
            Map.Entry pair = (Map.Entry) huntId.next();
            // mpFB.append(pair.getValue());
            if (pair.getValue().equals(this.biomeLocation)) {
                // mpFB.append(" - match found!");
                return (int) pair.getKey();
            } else {
                // mpFB.append(", ");
            }
            // LOGGER.debug(mpFB);
        }

        return -1; // Fail-Safe, Prevents an NPE.
    }

    private String getPrettyJsonString() {
        Gson uglyGson = new Gson();
        JsonParser parseJson = new JsonParser();
        Gson betterGson = new GsonBuilder().setPrettyPrinting().create();

        // All Maps loaded in order OUTSIDE of the dev environment, but SimpleRegistries loaded randomly within.
        String uglyJson = uglyGson.toJson(biomes);
        JsonElement parsedJson = parseJson.parse(uglyJson);

        return betterGson.toJson(parsedJson);
    }

    private boolean masterExists() {
        File f = new File(String.valueOf(pathToMaster));

        if (!f.exists()) {
            flFB.append("Desagas: ").append(isTemp ? "temporary " : "").append("master biomemap '").append(pathToMaster).append("'").append(" does not exist, ");
        }
        return f.exists();
    }

    private boolean createMaster() {
        try {
            FileWriter master = new FileWriter(pathToMaster);
            flFB.append("but was created.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            flFB.append("but could not be created.");
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

    private String writeJson(String prettyString) {
        try {
            FileWriter jsonWriter = new FileWriter(pathToMaster);
            jsonWriter.write(prettyString);
            jsonWriter.flush();
            return regType + "Registry updated '" + pathToMaster + "'.";
        } catch (IOException e) {
            e.printStackTrace();
            return regType + "Registry could not update '" + pathToMaster + "'.";
        }
    }

    // Obtain logo file parent folder for existing world, and save to file for calling from Write, before Server is loaded.
    public void writeTemp(String worldFolder) {
        biomes.clear();

        String thisPath = "level-name=" + worldFolder;
        LOGGER.info("Desagas: added '" + thisPath + "' to the temporary config file.");

        try {
            FileWriter tempWriter = new FileWriter(tempFileName);
            tempWriter.write(thisPath);
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
                        LOGGER.error("Desagas: cannot create '" + pathToMaster + "' for transfering '" + tempMasterFile + "' into.");
                    }
                } else {
                    LOGGER.error("Desagas: cannot access world folder.");
                }
            }
            LOGGER.info(removeTemp(tempMasterFile) ? "Desagas: successfully removed '" + tempMasterFile + "' after transferring to '" + pathToMaster + "'." : "Desagas: could not remove '" + tempMasterFile + "' after transfering to '" + pathToMaster + "'.");
        }
    }

    protected void stopServer () {
        LOGGER.info(removeTemp(tempFileName) ? "Desagas: successfully removed '" + tempFileName + "' while stopping server." : "Desagas: could not remove '" + tempFileName + "' while stopping server.");
    }

    private boolean removeTemp (String thisFiletoDelete) { return new File(String.valueOf(thisFiletoDelete)).delete(); }

}