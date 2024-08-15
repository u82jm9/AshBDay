package com.homeapp.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeapp.backend.models.bike.BikeParts;
import com.homeapp.backend.models.bike.Error;
import com.homeapp.backend.models.bike.FullBike;
import com.homeapp.backend.models.bike.Part;
import com.homeapp.backend.models.logger.ErrorLogger;
import com.homeapp.backend.models.logger.InfoLogger;
import com.homeapp.backend.models.logger.WarnLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.homeapp.backend.models.bike.Enums.BrakeType.*;
import static com.homeapp.backend.models.bike.Enums.ShifterStyle.STI;

/**
 * The type Shimano groupset service.
 */
@Service
public class ShimanoGroupsetService {
    private static FullBike bike;
    private BikeParts bikeParts;
    private final InfoLogger infoLogger = new InfoLogger();
    private final WarnLogger warnLogger = new WarnLogger();
    private final ErrorLogger errorLogger = new ErrorLogger();
    private final FullBikeService fullBikeService;
    private static final String LINKS_FILE = "src/main/resources/links.json";
    private final ObjectMapper om;

    /**
     * Instantiates a new Shimano Groupset Service.
     * Autowires in a FullBike Service to allow for access to instance Bike in all methods within this class.
     *
     * @param fullBikeService the full bike service
     */
    @Autowired
    public ShimanoGroupsetService(@Lazy FullBikeService fullBikeService, ObjectMapper om) {
        this.fullBikeService = fullBikeService;
        this.om = om;
    }

    /**
     * Gets shimano groupset.
     * Sets the passed-in parts to the instance object, to be used throughout the class.
     * Runs each separate component method in parallel to improve efficiency.
     * Each component method chooses the correct link based on design bike and then retrieves the individual part information from the web.
     *
     * @param parts the parts
     */
    public void getShimanoGroupset(BikeParts parts) {
        infoLogger.log("Getting Parts for Shimano Groupset.");
        bikeParts = parts;
        bike = fullBikeService.getBike();
        if (!bike.getShifterStyle().equals(STI)) {
            getLeverShifters();
            getBrakeLevers();
        } else {
            if ((bike.getBrakeType().equals(MECHANICAL_DISC)) || (bike.getBrakeType().equals(RIM))) {
                getMechanicalSTIShifters();
            } else if (bike.getBrakeType().equals(HYDRAULIC_DISC)) {
                getHydraulicSTIShifters();
            }
        }
        CompletableFuture<Void> brakeFuture = CompletableFuture.runAsync(this::getBrakeCalipers);
        CompletableFuture<Void> chainringFuture = CompletableFuture.runAsync(this::getChainring);
        CompletableFuture<Void> cassetteFuture = CompletableFuture.runAsync(this::getCassette);
        CompletableFuture<Void> chainFuture = CompletableFuture.runAsync(this::getChain);
        CompletableFuture<Void> rearDerailleurFuture = CompletableFuture.runAsync(this::getRearDerailleur);
        CompletableFuture<Void> frontDerailleurFuture = CompletableFuture.runAsync(this::getFrontDerailleur);
        CompletableFuture.allOf(brakeFuture, chainringFuture, cassetteFuture, chainFuture, rearDerailleurFuture, frontDerailleurFuture).join();
        if (!bikeParts.getErrorMessages().isEmpty()) {
            errorLogger.log("BikeParts has " + bikeParts.getErrorMessages().size() + " errors: " + bikeParts.getErrorMessages());
        }
    }

    private void getBrakeLevers() {
        String ref = "";
        String component = "Brake-Levers";
        infoLogger.log("Getting Parts for: " + component);
        bike = fullBikeService.getBike();
        if (bike.getBrakeType().equals(HYDRAULIC_DISC)) {
            ref = "Left-HydraulicBrakeLever";
            findPartFromInternalRef(ref);
            ref = "Right-HydraulicBrakeLever";
            findPartFromInternalRef(ref);
        } else {
            ref = "MechanicalBrakeLever";
            findPartFromInternalRef(ref);
        }
    }

    private void getBrakeCalipers() {
        String ref = "";
        String component = "Brake-Caliper";
        String method = "getBrakeCalipers";
        infoLogger.log("Getting Parts for: " + component);
        bike = fullBikeService.getBike();
        switch (bike.getBrakeType()) {
            case RIM -> ref = "RimBrakeCaliper";
            case MECHANICAL_DISC -> ref = "MechanicalBrakeCaliper";
            default -> {
                warnLogger.log("Not getting link for calipers as Hydraulic calipers and levers are together");
            }
        }
        if (ref != null || !ref.equals("")) {
            findPartFromInternalRef("Front-" + ref);
            findPartFromInternalRef("Rear-" + ref);
        } else {
            if (!bike.getBrakeType().equals(HYDRAULIC_DISC)) {
                bikeParts.getErrorMessages().add(new Error(component, method, ref));
            }
        }
    }

    private void getMechanicalSTIShifters() {
        String ref = "";
        String component = "STI-Shifter";
        String method = "getMechanicalSTIShifters";
        infoLogger.log("Getting Parts for: " + component);
        bike = fullBikeService.getBike();
        switch ((int) bike.getNumberOfFrontGears()) {
            //Could not find active site for 1 by components
            //Below links are useless, have taken out option for Frontend selection
            case 1 -> {
                if (bike.getNumberOfRearGears() == 9) {
                    ref = "MechanicalSTI_1_9";
                } else if (bike.getNumberOfRearGears() == 10) {
                    ref = "MechanicalSTI_1_10";
                } else if (bike.getNumberOfRearGears() == 11) {
                    ref = "MechanicalSTI_1_11";
                } else {
                    ref = "MechanicalSTI_1_12";
                }
            }
            case 2 -> {
                if (bike.getNumberOfRearGears() == 9) {
                    ref = "MechanicalSTI_2_9";
                } else if (bike.getNumberOfRearGears() == 10) {
                    ref = "MechanicalSTI_2_10";
                } else if (bike.getNumberOfRearGears() == 11) {
                    ref = "MechanicalSTI_2_11";
                } else {
                    //No Option is currently provided for 2x12
                    ref = "MechanicalSTI_2_12";
                }
            }
            case 3 -> {
                if (bike.getNumberOfRearGears() == 9) {
                    ref = "MechanicalSTI_3_9";
                } else {
                    bike.setNumberOfRearGears(10);
                    ref = "MechanicalSTI_3_10";
                    warnLogger.log("3 by Shimano Gears are restricted to a maximum of 10 at the back");
                }
            }
        }
        if (!ref.isEmpty()) {
            findPartFromInternalRef(ref);
        } else {
            bikeParts.getErrorMessages().add(new Error(component, method, ref));
        }
    }

    private void getHydraulicSTIShifters() {
        String ref = "";
        String component = "Hydraulic-Shifter";
        String method = "getHydraulicSTIShifters";
        infoLogger.log("Getting Parts for: " + component);
        bike = fullBikeService.getBike();
        if (bike.getNumberOfRearGears() == 10) {
            ref = "HydraulicSTI_10";
        } else if (bike.getNumberOfRearGears() == 11) {
            ref = "HydraulicSTI_11";
        } else if (bike.getNumberOfRearGears() == 12) {
            ref = "HydraulicSTI_12";
        } else {
            ref = "HydraulicSTI_9";
        }
        if (!ref.isEmpty()) {
            findPartFromInternalRef("Right-" + ref);
            ref = null;
        } else {
            bikeParts.getErrorMessages().add(new Error(component, method, ref));
        }
        if (bike.getNumberOfFrontGears() == 1) {
            ref = "Left-HydraulicSTI_1";
        } else if (bike.getNumberOfFrontGears() == 2) {
            ref = "Left-HydraulicSTI_2";
        } else if (bike.getNumberOfFrontGears() == 3) {
            ref = "Left-HydraulicSTI_3";
        } else {
            ref = null;
        }
        if (!ref.isEmpty()) {
            findPartFromInternalRef("Left-" + ref);
        } else {
            bikeParts.getErrorMessages().add(new Error(component, method, ref));
        }
    }

    private void getLeverShifters() {
        String ref = "";
        String component = "Trigger-Shifter";
        String method = "getLeverShifters";
        infoLogger.log("Getting Parts for: " + component);
        switch ((int) bike.getNumberOfRearGears()) {
            case 8 -> ref = "TriggerShifter_8";
            case 9 -> ref = "TriggerShifter_9";
            case 10 -> ref = "TriggerShifter_10";
            case 11 -> ref = "TriggerShifter_11";
            default -> ref = "";
        }
        if (!ref.isEmpty()) {
            findPartFromInternalRef(ref);
        } else {
            bikeParts.getErrorMessages().add(new Error(component, method, ref));
        }
    }

    private void getChainring() {
        String ref = "";
        String component = "Chainring";
        String method = "getChainring";
        infoLogger.log("Getting Parts for: " + component);
        bike = fullBikeService.getBike();
        switch ((int) bike.getNumberOfFrontGears()) {
            //Could not find active site for 1 by components
            //Below links are useless, have taken out option for Frontend selection
            case 1 -> {
                if (bike.getNumberOfRearGears() == 10) {
                    ref = "ChainSet_1_10";
                } else if (bike.getNumberOfRearGears() == 11) {
                    ref = "ChainSet_1_11";
                } else if (bike.getNumberOfRearGears() == 12) {
                    ref = "ChainSet_1_10";
                } else {
                    ref = "ChainSet_1";
                }
            }
            case 2 -> {
                if (bike.getNumberOfRearGears() == 9) {
                    ref = "ChainSet_2_9";
                } else if (bike.getNumberOfRearGears() == 10) {
                    ref = "ChainSet_2_10";
                } else if (bike.getNumberOfRearGears() == 11) {
                    ref = "ChainSet_2_11";
                } else if (bike.getNumberOfRearGears() == 12) {
                    ref = "ChainSet_2_12";
                } else {
                    ref = "ChainSet_2";
                }
            }
            case 3 -> {
                if (bike.getNumberOfRearGears() == 9) {
                    ref = "ChainSet_3_9";
                } else if (bike.getNumberOfRearGears() == 10) {
                    ref = "ChainSet_3_10";
                    bike.setNumberOfRearGears(10);
                } else {
                    ref = "ChainSet_3";
                }
            }
        }
        if (!ref.isEmpty()) {
            findPartFromInternalRef(ref);
        } else {
            bikeParts.getErrorMessages().add(new Error(component, method, ref));
        }
    }

    private void getCassette() {
        String ref = "";
        String component = "Cassette";
        String method = "getCassette";
        infoLogger.log("Getting Parts for: " + component);
        bike = fullBikeService.getBike();
        switch ((int) bike.getNumberOfRearGears()) {
            case 8 -> ref = "Cassette_8";
            case 9 -> ref = "Cassette_9";
            case 10 -> ref = "Cassette_10";
            case 11 -> ref = "Cassette_11";
            case 12 -> ref = "Cassette_12";
            default -> ref = "Cassette_1";
        }
        if (!ref.isEmpty()) {
            findPartFromInternalRef(ref);
        } else {
            bikeParts.getErrorMessages().add(new Error(component, method, ref));
        }
    }

    private void getChain() {
        String ref = "";
        String component = "Chain";
        String method = "getChain";
        infoLogger.log("Getting Parts for: " + component);
        bike = fullBikeService.getBike();
        switch ((int) bike.getNumberOfRearGears()) {
            case 8 -> ref = "Chain_8";
            case 9 -> ref = "Chain_9";
            case 10 -> ref = "Chain_10";
            case 11 -> ref = "Chain_11";
            case 12 -> ref = "Chain_12";
            default -> ref = "Chain_1";
        }
        if (!ref.isEmpty()) {
            findPartFromInternalRef(ref);
        } else {
            bikeParts.getErrorMessages().add(new Error(component, method, ref));
        }
    }

    private void getRearDerailleur() {
        String ref = "";
        String component = "Rear-Derailleur";
        String method = "getRearDerailleur";
        infoLogger.log("Getting Parts for: " + component);
        bike = fullBikeService.getBike();
        switch ((int) bike.getNumberOfRearGears()) {
            case 9 -> ref = "RDerailleur_9";
            case 10 -> ref = "RDerailleur_10";
            case 11 -> ref = "RDerailleur_11";
            case 12 -> ref = "RDerailleur_12";
            default -> ref = "RDerailleur_8";
        }
        if (!ref.isEmpty() && bike.getNumberOfRearGears() > 1) {
            findPartFromInternalRef(ref);
        } else {
            bikeParts.getErrorMessages().add(new Error(component, method, ref));
        }
    }

    private void getFrontDerailleur() {
        String ref = "";
        String component = "Front-Derailleur";
        String method = "getFrontDerailleur";
        infoLogger.log("Getting Parts for: " + component);
        bike = fullBikeService.getBike();
        switch ((int) bike.getNumberOfFrontGears()) {
            case 1 -> {
                ref = "FDerailleur_1";
                warnLogger.log("Front Derailleur not required, providing chain catcher");
            }
            case 2 -> {
                if (bike.getNumberOfRearGears() == 9) {
                    ref = "FDerailleur_2_9";
                } else if (bike.getNumberOfRearGears() == 10) {
                    ref = "FDerailleur_2_10";
                } else if (bike.getNumberOfRearGears() == 11) {
                    ref = "FDerailleur_2_11";
                } else if (bike.getNumberOfRearGears() == 12) {
                    ref = "FDerailleur_2_12";
                }
            }
            case 3 -> {
                if (bike.getNumberOfRearGears() == 9) {
                    ref = "FDerailleur_3_9";
                } else {
                    ref = "FDerailleur_3_10";
                }
            }
        }
        if (!ref.isEmpty()) {
            findPartFromInternalRef(ref);
        } else {
            bikeParts.getErrorMessages().add(new Error(component, method, ref));
        }
    }

    public void findPartFromInternalRef(String internalRef) {
        infoLogger.log("Reading all Links from File");
        try {
            Optional<Part> part = retrievePartFromLinks(internalRef);
            part.ifPresentOrElse(p -> {
                        bikeParts.getListOfParts().add(p);
                        infoLogger.log("Part found and added to bikeParts: " + p);
                    },
                    () -> errorLogger.log("No Part was found on File for Internal Ref: " + internalRef));
        } catch (IOException e) {
            errorLogger.log("An IOException occurred from method: readLinksFile!!See error message: " + e.getMessage() + "!!From: " + getClass());
        }
    }

    private Optional<Part> retrievePartFromLinks(String ref) throws IOException {
        LinkedList<Part> parts = om.readValue(new File(LINKS_FILE), new TypeReference<>() {
        });
        return parts.stream().filter(p -> p.getInternalReference().equals(ref)).findFirst();
    }
}