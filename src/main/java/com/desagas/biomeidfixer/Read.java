package com.desagas.biomeidfixer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Read extends Write {

    private static List<String> thisJsonFile = new ArrayList<>();
    private static List<String> thisPropertiesFile = new ArrayList<>();
    private static String fullJsonString = "";

    // Get JSON file and read it.
    protected void importBiomeMap(boolean isTemp) {
        String fileToRead = isTemp ? tempMasterFile : pathToMaster;
        try { thisJsonFile = Files.readAllLines(Paths.get(fileToRead));
            LOGGER.debug("Desagas: read for importing the master list of biomes at " + fileToRead);
        } catch (IOException e) {
            LOGGER.error("Desagas: could not read for importing the master list of biomes at " + fileToRead);
            e.printStackTrace(); }

        for (String jsonLine : thisJsonFile) {
            fullJsonString += jsonLine
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

    // Reads either server.properties or the temp file created in Write to get world folder name, before registries happen.
    protected String getServerWorldFolder (String fileName) {
        if (!new File(String.valueOf(fileName)).exists()) {
            LOGGER.debug("Desagas: request failed for getting world name from " + fileName);
            return "isTemp";
        } else {
            try { thisPropertiesFile = Files.readAllLines(Paths.get(fileName));
                LOGGER.debug("Desagas: found " + fileName);
            } catch (IOException e) {
                LOGGER.error("Desagas: could not find " + fileName);
                e.printStackTrace(); }

            for (String serverPropertyLine : thisPropertiesFile) {
                if (serverPropertyLine.contains("level-name=")) {
                    return serverPropertyLine.split("=")[1];
                }
            }
            return "isTemp";
        }
    }
}