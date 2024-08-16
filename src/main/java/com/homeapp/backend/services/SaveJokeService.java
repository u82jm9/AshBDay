package com.homeapp.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeapp.backend.models.DTOJoke;
import com.homeapp.backend.models.logger.ErrorLogger;
import com.homeapp.backend.models.logger.InfoLogger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class SaveJokeService {
    private static final String JOKES_FILE = "src/main/resources/jokes.json";
    private static final String JOKES_FILE_BACKUP = "src/main/resources/jokes_backup.json";
    private final ObjectMapper om = new ObjectMapper();
    private final ArrayList<DTOJoke> jokes;
    private final InfoLogger infoLogger = new InfoLogger();
    private final ErrorLogger errorLogger = new ErrorLogger();


    public SaveJokeService() {
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
        saveToFile(JOKES_FILE, jokes);
    }

    private void saveToFile(String fileName, ArrayList<DTOJoke> jokes) {
        try {
            om.writeValue(new File(fileName), jokes);
        } catch (IOException e) {
            errorLogger.log("Error Logging joke!\n" + e);
        }
    }

    public void deleteAllJokes() {
        backupJokes();
        try {
            om.writeValue(new File(JOKES_FILE), new ArrayList<>());
        } catch (IOException e) {
            errorLogger.log("Error Deleting Jokes.\n" + e);
        }
    }

    private void backupJokes() {
        ArrayList<DTOJoke> currentJokes = readSavedJokesFile();
        try {
            File file = new File(JOKES_FILE_BACKUP);
            if (!file.exists()) {
                file.createNewFile();
            }
            om.writeValue(file, currentJokes);
        } catch (IOException e) {
            errorLogger.log("Error Deleting Jokes.\n" + e);
        }
    }

    public void reloadJokesFromBackup() {
        ArrayList<DTOJoke> backupJokes = new ArrayList<>();
        try {
            File file = new File(JOKES_FILE_BACKUP);
            backupJokes = om.readValue(file, new TypeReference<>() {
            });
            saveToFile(JOKES_FILE, backupJokes);
        } catch (IOException e) {
            errorLogger.log("Error Backing up files.\n" + e);
        }
    }
}
