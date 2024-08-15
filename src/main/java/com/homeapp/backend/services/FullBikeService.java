package com.homeapp.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeapp.backend.models.bike.Frame;
import com.homeapp.backend.models.bike.FullBike;
import com.homeapp.backend.models.logger.ErrorLogger;
import com.homeapp.backend.models.logger.InfoLogger;
import com.homeapp.backend.models.logger.WarnLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.homeapp.backend.models.bike.Enums.BrakeType.*;
import static com.homeapp.backend.models.bike.Enums.FrameStyle.*;
import static com.homeapp.backend.models.bike.Enums.GroupsetBrand.SHIMANO;
import static com.homeapp.backend.models.bike.Enums.HandleBarType.*;
import static com.homeapp.backend.models.bike.Enums.ShifterStyle.*;

/**
 * The Full Bike Service class.
 * Houses all methods relating to designing a Full Bike.
 */
@Service
public class FullBikeService {

    private FullBike bike;
    private static final ObjectMapper om = new ObjectMapper();
    private static final String JSON_BIKES_FILE = "src/main/resources/bikes.json";
    private static final String JSON_BIKES_FILE_BACKUP = "src/main/resources/bikes_backup.json";
    private List<FullBike> bikeList;
    private final InfoLogger infoLogger = new InfoLogger();
    private final WarnLogger warnLogger = new WarnLogger();
    private final ErrorLogger errorLogger = new ErrorLogger();
    private final ShimanoGroupsetService shimanoGroupsetService;

    /**
     * Instantiates a new Full bike service.
     * This instantiation is Autowired to allow this Service class to use methods from the Shimano Groupset Service class and the Exception Handler.
     * Sets the bike object on instance to a new FullBike so has no influence from previous calls.
     * Sets the list of bikes on the instance to a fresh load of the bike file.
     *
     * @param shimanoGroupsetService the Shimano Groupset service
     */
    @Autowired
    public FullBikeService(@Lazy ShimanoGroupsetService shimanoGroupsetService) {
        this.shimanoGroupsetService = shimanoGroupsetService;
        this.bike = new FullBike();
        this.bikeList = readBikesFile();
    }

    private List<FullBike> readBikesFile() {
        infoLogger.log("Reading Bikes From File");
        try {
            File file = new File(JSON_BIKES_FILE);
            return om.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            errorLogger.log("An IOException occurred from method: readBikesFile!!See error message: " + e.getMessage() + "!!From: " + getClass());
        }
        return new ArrayList<>();
    }

    /**
     * Reload bikes from backup file, writes the back-up bikes onto the normal bike file.
     */
    public void reloadBikesFromBackup() {
        infoLogger.log("Reloading Bikes From Backup File");
        try {
            deleteAllBikes();
            File file = new File(JSON_BIKES_FILE_BACKUP);
            List<FullBike> bikes = om.readValue(file, new TypeReference<>() {
            });
            writeBikesToFile(bikes);
        } catch (IOException e) {
            errorLogger.log("An IOException occurred from method: readBikesFromBackup!!See error message: " + e.getMessage() + "!!From: " + getClass());
        }
    }

    /**
     * Write bikes to normal bike file.
     * Sets the bike list on the instance to the passed in list, to avoid an additional call to read the file.
     *
     * @param list the list
     */
    public void writeBikesToFile(List<FullBike> list) {
        infoLogger.log("Writing Bikes Back to File");
        try {
            om.writeValue(new File(JSON_BIKES_FILE), list);
            bikeList = list;
        } catch (IOException e) {
            errorLogger.log("An IOException occurred from method: writeBikesToFile!!See error message: " + e.getMessage() + "!!From: " + getClass());
        }
    }

    private List<FullBike> getBikeList() {
        return bikeList;
    }

    /**
     * Gets bike from the instance.
     *
     * @return the bike on the instance.
     */
    public FullBike getBike() {
        return bike;
    }

    /**
     * Sets bike to this instance.
     *
     * @param bike the bike
     */
    public void setBike(FullBike bike) {
        this.bike = bike;
    }

    /**
     * Gets all full bikes on the instance.
     * For efficiency this method does not read the file.
     *
     * @return a list of full bikes.
     */
    public List<FullBike> getAllFullBikes() {
        List<FullBike> bikeList = getBikeList();
        warnLogger.log("Getting list of all bikes, number returned: " + bikeList.size());
        return bikeList;
    }

    /**
     * Create and save a Full Bike to the file.
     * Bike passed in is given a unique ID before getting saved to the file.
     * Method does not check the existence of this bike, or check for similar bikes. It simply adds this bike to the list.
     *
     * @param bike the bike
     */
    public void create(FullBike bike) {
        infoLogger.log("Adding new bike!");
        long newId = bikeList.size() + 1;
        bike.setFullBikeId(newId);
        bikeList.add(bike);
        writeBikesToFile(bikeList);
    }

    /**
     * Update bike full bike.
     * Checks passed in bike meets with standards for each component.
     * If bike with the same name is on file this bike is replaced.
     *
     * @param bike the bike
     * @return the full bike
     */
    public FullBike updateBike(FullBike bike) {
        infoLogger.log("Updating bike on File!");
        setBike(bike);
        checkBikeShifters(bike);
        checkFrameStyle(bike);
        checkBrakeCompatibility(bike);
        if (getBikeUsingName(bike.getBikeName()).isPresent()) {
            removeBikeFromFile(bike.getBikeName());
        }
        bikeList.add(bike);
        writeBikesToFile(bikeList);
        return bike;
    }

    private void removeBikeFromFile(String bikeName) {
        bikeList.removeIf(i -> i.getBikeName().equals(bikeName));
        writeBikesToFile(bikeList);
    }

    private void checkBrakeCompatibility(FullBike bike) {
        if (bike.getBrakeType().equals(RIM) || bike.getBrakeType().equals(NOT_REQUIRED)) {
            bike.getFrame().setDiscBrakeCompatible(false);
        } else {
            bike.getFrame().setDiscBrakeCompatible(true);
        }
    }

    private void checkFrameStyle(FullBike bike) {
        if (bike.getFrame().getFrameStyle().equals(SINGLE_SPEED)) {
            bike.getFrame().setRequiresFrontGearCable(false);
            bike.getFrame().setRequiresRearGearCable(false);
            bike.setNumberOfRearGears(1);
            bike.setNumberOfFrontGears(1);
        } else if (bike.getFrame().getFrameStyle().equals(GRAVEL)) {
            bike.getFrame().setDiscBrakeCompatible(true);
            bike.getFrame().setRequiresFrontGearCable(false);
        } else if (bike.getFrame().getFrameStyle().equals(TOUR)) {
            bike.getFrame().setDiscBrakeCompatible(true);
        }
        if (bike.getNumberOfFrontGears() > 1) {
            bike.getFrame().setRequiresFrontGearCable(true);
        } else {
            bike.getFrame().setRequiresFrontGearCable(false);
        }
        if (bike.getNumberOfRearGears() > 1) {
            bike.getFrame().setRequiresRearGearCable(true);
        } else {
            bike.getFrame().setRequiresRearGearCable(false);
        }
    }

    private void checkBikeShifters(FullBike bike) {
        if (bike.getFrame().isRequiresFrontGearCable() || bike.getFrame().isRequiresRearGearCable()) {
            if (bike.getHandleBarType().equals(DROPS) || bike.getHandleBarType().equals(FLARE)) {
                bike.setShifterStyle(STI);
            } else if (bike.getHandleBarType().equals(FLAT)) {
                bike.setShifterStyle(TRIGGER);
            }
        } else {
            bike.setShifterStyle(NONE);
        }
    }

    /**
     * Start new bike full bike.
     * Checks there isn't currently a "new bike" using hardcoded bike name, returns bike if there is one.
     * Creates new bike and sets all variable to their version of "not selected".
     * Adds bike to list and returns it.
     *
     * @return the full bike
     */
    public FullBike startNewBike() {
        infoLogger.log("Starting new bike, service method.");
        Optional<FullBike> b = getBikeUsingName("Your Custom Bike");
        if (b.isPresent()) {
            warnLogger.log("Bike with that name already exists on DB.");
            return b.get();
        } else {
            Frame frame = new Frame();
            frame.setFrameStyle(NONE_SELECTED);
            bike = new FullBike();
            bike.setBikeName("Your Custom Bike");
            bike.setFrame(frame);
            bike.setBrakeType(NO_SELECTION);
            bike.setShifterStyle(NONE);
            bike.setGroupsetBrand(SHIMANO);
            bike.setHandleBarType(NOT_SELECTED);
            bike.setNumberOfFrontGears(0);
            bike.setNumberOfRearGears(0);
            bike.setWheelPreference("");
            create(bike);
            return bike;
        }
    }

    /**
     * Gets bike using name.
     *
     * @param bikeName the bike name
     * @return the bike
     */
    public Optional<FullBike> getBikeUsingName(String bikeName) {
        warnLogger.log("Getting single bike with bike name: " + bikeName);
        return bikeList.stream()
                .filter(item -> item.getBikeName().equals(bikeName))
                .findFirst();
    }

    /**
     * Delete bike by id.
     *
     * @param bikeId the bike id
     */
    public void deleteBike(long bikeId) {
        warnLogger.log("Deleting Bike with ID: " + bikeId);
        bikeList.removeIf(i -> i.getFullBikeId() == (bikeId));
        writeBikesToFile(bikeList);
    }

    /**
     * Delete all bikes, by writing an empty list back to normal bike file.
     */
    public void deleteAllBikes() {
        infoLogger.log("Deleting ALL BIKES on File");
        warnLogger.log("Deleting ALL BIKES on File");
        writeBikesToFile(new ArrayList<>());
    }
}