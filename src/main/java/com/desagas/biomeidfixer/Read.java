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
        StringBuilder bmFB = new StringBuilder().append("Desagas: ");
        String fileToRead = isTemp ? tempMasterFile : pathToMaster;
        try { thisJsonFile = Files.readAllLines(Paths.get(fileToRead));
            bmFB.append("current master biomemap '").append(fileToRead).append("'");
        } catch (IOException e) {
            bmFB.append("could not read master biomemap '").append(fileToRead).append("'.");
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

        bmFB.append(" contains: ").append(biomes);
        LOGGER.info(bmFB);
    }

    // Reads either server.properties or the temp file created in Write to get world folder name, before registries happen.
    protected String getServerWorldFolder (String fileName) {
        String worldFolder = "isTemp";

        if (new File(String.valueOf(fileName)).exists()) {
            try { thisPropertiesFile = Files.readAllLines(Paths.get(fileName)); }
            catch (IOException e) {
                LOGGER.error("Desagas: could not read config file '" + fileName + "'.");
                e.printStackTrace(); }

            for (String serverPropertyLine : thisPropertiesFile) {
                if (serverPropertyLine.contains("level-name=")) {
                    worldFolder = serverPropertyLine.split("=")[1];
                }
            }
        }

        return worldFolder;
    }
}