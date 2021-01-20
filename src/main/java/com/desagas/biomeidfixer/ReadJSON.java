package com.desagas.biomeidfixer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReadJSON extends WriteJSON {

    private static List<String> thisJsonFile = new ArrayList<>();
    private static String fullJsonString = "";

    protected void importBiomeMap() {
        try { thisJsonFile = Files.readAllLines(Paths.get(masterFile));
            LOGGER.debug("Desagas: read for import the master list of biomes at " + masterFile);
        } catch (IOException e) {
            LOGGER.error("Desagas: could not read for import the master list of biomes at " + masterFile);
            e.printStackTrace(); }

        for (String jasonLine : thisJsonFile) {
            fullJsonString += jasonLine
                    .replace(" ", "")
                    .replace("{", "")
                    .replace("}", "")
                    .replace("\":\"", "#")
                    .replace("\"", "");
        }

        for (String i : fullJsonString.split(",")) {
            int thisBiomeId = Integer.parseInt(i.split("#")[0]);
            String thisBiomeLocation = i.split("#")[1];
            biomes.put(thisBiomeId, thisBiomeLocation);
        }

        LOGGER.debug("Desagas: found this master list of biomes: " + biomes);
    }
}