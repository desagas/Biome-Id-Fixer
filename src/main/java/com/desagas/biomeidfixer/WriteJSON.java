package com.desagas.biomeidfixer;

import com.google.gson.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;

// TODO If Server, Send list of Biomes and Ids to client to register/store/use, NOT to map. This will avoid the deletion during backs.
// TODO Save config in world folder on both Server and Client worlds.

public class WriteJSON {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected int biomeId;
    protected String biomeLocation;
    protected static final Map<Integer, String> biomes = new HashMap<Integer, String>();
    protected static final String masterFile = "biomeidfixer(doNOTedit).json";

    public int getOrTryBiomeAssignment (int biomeId, String biomeLocation) {
        this.biomeId = biomeId;
        this.biomeLocation = biomeLocation;

        StringBuilder feedback = new StringBuilder();
        feedback.append("Desagas: request for biomeId ").append(biomeId).append(" at ").append(biomeLocation);

        if (masterExists() && biomes.isEmpty()) { new ReadJSON().importBiomeMap(); } // !!! IMPORTANT <-- Importing MUST be done before creating master,or invalid int formation exceptions.
        if (!masterExists() && !createMaster()) { return -1; } // !!! IMPORTANT <-- Will create empty file which CANNOT be parsed to Map<Integer,String>

        int returnId = -1;
        boolean updateMaster = false;

        switch (canAssignIdtoBiome()) {
            case 0: // Already assigned;
                updateMaster = true;
                returnId = this.biomeId;
                feedback.append(" - already assigned.");
                break;
            case 1: // Not assigned;
                returnId = assignId();
                feedback.append(" - now assigned.");
                updateMaster = true;
                break;
            case 2: // Biome assigned, not to the id;
                returnId = findId();
                feedback.append(" - not assigned: stored biomeId ").append(returnId).append(" used instead.");
                updateMaster = true;
                break;
            case 3: // Id assigned, not to the biome;
                returnId = getOrTryBiomeAssignment(this.biomeId + 1, this.biomeLocation);
                feedback.append(" - not assigned: newly registered biome assigned biomeId ").append(returnId);
                updateMaster = true;
                break;
            default:
                throw new IllegalStateException("Desagas: unexpected canAssignIdtoBiome() logic value assigned: " + canAssignIdtoBiome());
        }

        LOGGER.info(feedback);

        if (updateMaster) { writeJson(getPrettyJsonString()); }

        return returnId;
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
        String betterJson = betterGson.toJson(parsedJson);

        LOGGER.debug(new StringBuilder().append("Desagas: your ").append(masterFile).append(" contains the following:").append(betterJson));

        return betterJson;
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