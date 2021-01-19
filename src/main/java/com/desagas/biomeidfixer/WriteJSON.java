package com.desagas.biomeidfixer;

import com.google.gson.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class WriteJSON {
    private static final Logger LOGGER = LogManager.getLogger();

    private int biomeId;
    private String biomeLocation;
    private static final Map<Integer, String> biomes = new HashMap<Integer, String>();
    private static final String masterFile = "config/biomeidfixer.json";

    public Object getOrTryBiomeAssignment (int biomeId, String biomeLocation) {

        this.biomeId = biomeId;
        this.biomeLocation = biomeLocation;

        LOGGER.info(new StringBuilder().append("Desagas: received request for biomeId ").append(biomeId).append(" at ").append(biomeLocation));

        if (!masterExists() && !createMaster()) { return null; }
        biomes.put(-999999, "desagas:demo");

        int returnId = 0;
        boolean returnFalse = false;

        switch (canAssignIdtoBiome()) {
            case 0: // Already assigned;
                LOGGER.info(new StringBuilder().append("Desagas: biomeId ").append(this.biomeId).append(" already assigned at ").append(this.biomeLocation));
                returnFalse = true;
                break;
            case 1: // Not assigned;
                LOGGER.info(new StringBuilder().append("Desagas: biomeId ").append(this.biomeId).append(" assigned at ").append(this.biomeLocation));
                returnId = assignId();
                break;
            case 2: // Biome assigned, not to the id;
                LOGGER.info(new StringBuilder().append("Desagas: biomeId ").append(this.biomeId).append(" not assigned at ").append(this.biomeLocation));
                getOrTryBiomeAssignment(findId(), biomeLocation);
            case 3: // Id assigned, not to the biome;
                LOGGER.info(new StringBuilder().append("Desagas: biomeId ").append(this.biomeId).append(" not assigned at ").append(this.biomeLocation));
                getOrTryBiomeAssignment(biomeId++, biomeLocation);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + canAssignIdtoBiome());
        }

        String outputJson = getPrettyJsonString();
        writeJson(outputJson);

        return !returnFalse ? returnId : returnFalse;
    }

    private int assignId () {
        biomes.put(this.biomeId, this.biomeLocation);
        return this.biomeId;
    }

    private int findId () {
        Iterator huntId = biomes.entrySet().iterator();
        while (huntId.hasNext()) {
            Map.Entry pair = (Map.Entry)huntId.next();
            if (pair.getValue() == this.biomeLocation) { return this.biomeId; }
        }
        return -1;
    }

    private String getPrettyJsonString () {
        Gson uglyGson = new Gson();
        JsonParser parseJson = new JsonParser();
        Gson betterGson = new GsonBuilder().setPrettyPrinting().create();

//        // ReOrdering the List
//        Set<Entry<Integer, String>> entries = biomes.entrySet();
//        Comparator<Entry<Integer, String>> valueComparator = (e1, e2) -> {
//            Integer v1 = e1.getKey();
//            Integer v2 = e2.getKey();
//            return v1.compareTo(v2);
//        };
//        List<Entry<Integer, String>> listOfEntries = new ArrayList<>(entries);
//        listOfEntries.sort(valueComparator);
//        LinkedHashMap<Integer, String> sortedByValue = new LinkedHashMap<>(listOfEntries.size());
//        for(Entry<Integer, String> entry : listOfEntries){ sortedByValue.put(entry.getKey(), entry.getValue()); }
//        Set<Entry<Integer, String>> entrySetSortedByValue = sortedByValue.entrySet();
//        for(Entry<Integer, String> mapping : entrySetSortedByValue){ LOGGER.debug(mapping.getKey() + " ==> " + mapping.getValue()); }

        String uglyJson = uglyGson.toJson(biomes);
        JsonElement parsedJson = parseJson.parse(uglyJson);
        String betterJson = betterGson.toJson(parsedJson);

        LOGGER.debug(new StringBuilder().append("Desagas:jsonOutput:").append(betterJson));

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
            LOGGER.info("Desagas: could not create master list of mapped biomes.");
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
            LOGGER.info("Desagas: updated master list of mapped biomes.");
        } catch (IOException e) {
            LOGGER.info("Desagas: could not update master list of mapped biomes.");
            e.printStackTrace();
        }
    }
}