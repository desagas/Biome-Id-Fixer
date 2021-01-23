package com.desagas.biomeidfixer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Read extends Write {

    private static List<String> thisJsonFile = new ArrayList<>();
    private static List<String> thisPropertiesFile = new ArrayList<>();
    private static String fullJsonString = "";

    protected void importBiomeMap() {
        LOGGER.info(pathToMaster);
        try { thisJsonFile = Files.readAllLines(Paths.get(pathToMaster));
            LOGGER.debug("Desagas: read for importing the master list of biomes at " + pathToMaster);
        } catch (IOException e) {
            LOGGER.error("Desagas: could not read for importing the master list of biomes at " + pathToMaster);
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

    protected String getServerWorldFolder (String fileName) {
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

        return null;
    }
}