package com.homeapp.backend.controller;

import com.homeapp.backend.models.DTOJoke;
import com.homeapp.backend.models.DTOLog;
import com.homeapp.backend.models.logger.ErrorLoggerFE;
import com.homeapp.backend.models.logger.InfoLoggerFE;
import com.homeapp.backend.models.logger.WarnLoggerFE;
import com.homeapp.backend.services.SaveJokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * The Test controller.
 * Houses APIs not specifically relating to BE objects, just for general BE usage.
 */
@RestController
@RequestMapping("Test/")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {
    private final SaveJokeService saveJokeService = new SaveJokeService();
    private final InfoLoggerFE infoLogger = new InfoLoggerFE();
    private final WarnLoggerFE warnLogger = new WarnLoggerFE();
    private final ErrorLoggerFE errorLogger = new ErrorLoggerFE();

    /**
     * Instantiates a new Test controller.
     */
    @Autowired
    public TestController() {
    }

    /**
     * Is this thing on.
     * Used by FE to check if BE is up and running. FE pings this API every few seconds.
     *
     * @return HTTP - Status OK
     */
    @GetMapping("IsThisThingOn")
    public ResponseEntity<Boolean> isThisThingOn() {
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * DTOLog this!
     * Separates out different DTOLog levels before calling the appropriate logger for the message passed-in.
     *
     * @param dtoLog the DTOLog message
     * @return HTTP - Status CREATED
     */
    @PostMapping("LogThis")
    public ResponseEntity<HttpStatus> logThis(@RequestBody DTOLog dtoLog) {
        switch (dtoLog.getLevel()) {
            case "WARN" -> warnLogger.log(dtoLog.getMessage());
            case "INFO" -> infoLogger.log(dtoLog.getMessage());
            default -> errorLogger.log(dtoLog.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("SaveThis")
    public ResponseEntity<HttpStatus> saveThis(@RequestBody DTOJoke joke) {
        saveJokeService.save(joke);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("GetSavedJokes")
    public ResponseEntity<ArrayList<DTOJoke>> getSavedJokes() {
        ArrayList<DTOJoke> allJokes = saveJokeService.readSavedJokesFile();
        return new ResponseEntity<>(allJokes, HttpStatus.ACCEPTED);
    }
}