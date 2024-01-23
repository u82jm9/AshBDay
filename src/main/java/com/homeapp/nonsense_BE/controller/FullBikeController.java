package com.homeapp.nonsense_BE.controller;

import com.homeapp.nonsense_BE.models.bike.FullBike;
import com.homeapp.nonsense_BE.models.logger.InfoLogger;
import com.homeapp.nonsense_BE.models.logger.WarnLogger;
import com.homeapp.nonsense_BE.services.FullBikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("FullBike/")
@CrossOrigin(origins = "http://localhost:3000")
public class FullBikeController {

    private final InfoLogger infoLogger = new InfoLogger();
    private final WarnLogger warnLogger = new WarnLogger();
    private final FullBikeService fullBikeService;

    @Autowired
    public FullBikeController(FullBikeService fullBikeService) {
        this.fullBikeService = fullBikeService;
    }

    @GetMapping("GetAll")
    public ResponseEntity<List<FullBike>> getallBikes() {
        infoLogger.log("Get all Bikes, API");
        List<FullBike> bikeList = fullBikeService.getAllFullBikes();
        warnLogger.log("Returning " + bikeList.size() + " bikes to FE");
        return new ResponseEntity<>(bikeList, HttpStatus.ACCEPTED);
    }

    @GetMapping("StartNewBike")
    public ResponseEntity<FullBike> startNewBike() {
        infoLogger.log("Starting new Bike, API");
        FullBike bike = fullBikeService.startNewBike();
        warnLogger.log("Returning new Bike to FE: " + bike);
        return new ResponseEntity<>(bike, HttpStatus.ACCEPTED);
    }

    @PostMapping("AddFullBike")
    public ResponseEntity<HttpStatus> addFullBike(@RequestBody FullBike bike) {
        infoLogger.log("Adding new full bike, API");
        fullBikeService.create(bike);
        warnLogger.log("Adding new full bike, API.Bike: " + bike);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("UpdateBike")
    public ResponseEntity<FullBike> updateBike(@RequestBody FullBike bike) {
        infoLogger.log("Updating Bike, API");
        FullBike updatedBike = fullBikeService.updateBike(bike);
        warnLogger.log("Updating Bike: " + bike);
        return new ResponseEntity<>(updatedBike, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("DeleteBike")
    public ResponseEntity<HttpStatus> deleteBike(@RequestBody FullBike bike) {
        infoLogger.log("Deleting Bike from DB with id " + bike.getFullBikeId());
        fullBikeService.deleteBike(bike.getFullBikeId());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}