package com.homeapp.backend.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeapp.backend.models.logger.ErrorLogger;
import com.homeapp.backend.models.logger.InfoLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class SaveJoke {
    private static final String JOKES_FILE = "src/main/resources/jokes.json";
    private final ObjectMapper om = new ObjectMapper();
    private final ArrayList<DTOJoke> jokes;
    private final InfoLogger infoLogger = new InfoLogger();
    private final ErrorLogger errorLogger = new ErrorLogger();


    public SaveJoke() {
        this.jokes = readSavedJokesFile();
    }

    private ArrayList<DTOJoke> readSavedJokesFile() {
        try {
            File file = new File(JOKES_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return om.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            errorLogger.log("Error Reading joke file.\n" + e);
            throw new RuntimeException(e);
        }
    }

    public void save(DTOJoke joke) {
        infoLogger.log("Saving Jokes: " + joke);
        jokes.add(joke);
        saveToFile();
    }

    private void saveToFile() {
        try {
            om.writeValue(new File(JOKES_FILE), jokes);
        } catch (IOException e) {
            errorLogger.log("Error Logging joke!\n" + e);
        }
    }
}
