package com.homeapp.backend.controller;

import com.homeapp.backend.models.bike.FullBike;
import com.homeapp.backend.models.logger.InfoLogger;
import com.homeapp.backend.models.logger.WarnLogger;
import com.homeapp.backend.services.FullBikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The Full Bike Controller.
 * Houses multiple APIs relating to a Full Bike design.
 */
@RestController
@RequestMapping("FullBike/")
@CrossOrigin(origins = "http://localhost:3000")
public class FullBikeController {

    private final InfoLogger infoLogger = new InfoLogger();
    private final WarnLogger warnLogger = new WarnLogger();
    private final FullBikeService fullBikeService;

    /**
     * Instantiates a new Full bike controller.
     * Autowires in a Full Bike Service for access to the methods.
     *
     * @param fullBikeService the full bike service
     */
    @Autowired
    public FullBikeController(FullBikeService fullBikeService) {
        this.fullBikeService = fullBikeService;
    }

    /**
     * Gets list of bikes.
     *
     * @return the bikes
     * @return HTTP - Status ACCEPTED
     */
    @GetMapping("GetAll")
    public ResponseEntity<List<FullBike>> getallBikes() {
        infoLogger.log("Get all Bikes, API");
        List<FullBike> bikeList = fullBikeService.getAllFullBikes();
        warnLogger.log("Returning " + bikeList.size() + " bikes to FE");
        return new ResponseEntity<>(bikeList, HttpStatus.ACCEPTED);
    }

    /**
     * Starts/returns a new bike.
     *
     * @return the response entity
     * @return HTTP - Status ACCEPTED
     */
    @GetMapping("StartNewBike")
    public ResponseEntity<FullBike> startNewBike() {
        infoLogger.log("Starting new Bike, API");
        FullBike bike = fullBikeService.startNewBike();
        warnLogger.log("Returning new Bike to FE: " + bike);
        return new ResponseEntity<>(bike, HttpStatus.ACCEPTED);
    }

    /**
     * Adds Full Bike to File.
     *
     * @param bike the bike
     * @return the response entity
     * @return HTTP - Status CREATED
     */
    @PostMapping("AddFullBike")
    public ResponseEntity<HttpStatus> addFullBike(@RequestBody FullBike bike) {
        infoLogger.log("Adding new full bike, API");
        fullBikeService.create(bike);
        warnLogger.log("Adding new full bike, API.Bike: " + bike);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Updates Full Bike already on file.
     *
     * @param bike the bike
     * @return the response entity
     * @return HTTP - Status ACCEPTED
     */
    @PostMapping("UpdateBike")
    public ResponseEntity<FullBike> updateBike(@RequestBody FullBike bike) {
        infoLogger.log("Updating Bike, API");
        FullBike updatedBike = fullBikeService.updateBike(bike);
        warnLogger.log("Updating Bike: " + bike);
        return new ResponseEntity<>(updatedBike, HttpStatus.ACCEPTED);
    }

    /**
     * Deletes Full Bike from file, using the bike ID.
     *
     * @param bike the bike
     * @return the response entity
     * @return HTTP - Status OK
     */
    @PostMapping("DeleteBike")
    ResponseEntity<HttpStatus> deleteBike(@RequestBody FullBike bike) {
        infoLogger.log("Deleting Bike from DB with id " + bike.getFullBikeId());
        fullBikeService.deleteBike(bike.getFullBikeId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}