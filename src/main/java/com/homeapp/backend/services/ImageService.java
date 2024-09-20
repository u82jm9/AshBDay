package com.homeapp.backend.services;

import com.homeapp.backend.models.bike.FullBike;
import com.homeapp.backend.models.bike.Image;
import com.homeapp.backend.models.bike.ImageComparator;
import com.homeapp.backend.models.logger.InfoLogger;
import com.homeapp.backend.models.logger.WarnLogger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.homeapp.backend.models.bike.Enums.BrakeType.RIM;
import static com.homeapp.backend.models.bike.Enums.FrameStyle.SINGLE_SPEED;
import static com.homeapp.backend.models.bike.Enums.HandleBarType.FLAT;

/**
 * The Image Service Class.
 */
@Service
public class ImageService {
    private final InfoLogger infoLogger = new InfoLogger();
    private final WarnLogger warnLogger = new WarnLogger();

    /**
     * Gets images for passed-in bike.
     * Method runs the get Image calls in parallel to improve performance.
     * Each component has a separate method for setting the correct Image information, to keep the logic in this method to a minimum.
     *
     * @param b the Full Bike
     * @return the list of images
     */
    public List<Image> getImages(FullBike b) {
        List<Image> imageList = new ArrayList<>();
        infoLogger.log("Getting Images for Bike!");
        CompletableFuture<Void> frameImageFuture = CompletableFuture.runAsync(() -> imageList.add(chooseFrameImage(b)));
        CompletableFuture<Void> barImageFuture = CompletableFuture.runAsync(() -> imageList.add(chooseBarImage(b)));
        CompletableFuture<Void> brakeImageFuture = CompletableFuture.runAsync(() -> imageList.add(chooseBrakeImage(b)));
        if (b.getHandleBarType().equals(FLAT) || b.getFrame().getFrameStyle().equals(SINGLE_SPEED)) {
            if (b.getNumberOfRearGears() > 1) {
                imageList.add(chooseTriggerShiftersImage(b));
            }
            imageList.add(new Image(4, "Brake-Levers", "brake_lever.png", "Brake Levers"));
        } else {
            imageList.add(chooseSTIShiftersImage(b));
        }
        CompletableFuture<Void> chainImageFuture = CompletableFuture.runAsync(() -> imageList.add(chooseChainImage(b)));
        CompletableFuture<Void> cassetteImageFuture = CompletableFuture.runAsync(() -> imageList.add(chooseCassetteImage(b)));
        if (b.getNumberOfRearGears() > 1) {
            imageList.add(chooseRearDerailleurImage(b));
        }
        imageList.add(chooseChainsetImage(b));
        if (b.getNumberOfFrontGears() > 1) {
            imageList.add(chooseFrontDerailleurImage(b));
        }
        CompletableFuture<Void> wheelImageFuture = CompletableFuture.runAsync(() -> imageList.add(chooseWheelImage(b)));
        CompletableFuture.allOf(frameImageFuture, barImageFuture, brakeImageFuture, chainImageFuture, cassetteImageFuture, wheelImageFuture).join();
        imageList.sort(new ImageComparator());
        warnLogger.log("Bike: " + b);
        warnLogger.log("Returning List: " + imageList);
        return imageList;
    }

    private Image chooseFrameImage(FullBike b) {
        Image i = new Image(0, "Frame", "no_image.png", "No Frame Selected");
        switch (b.getFrame().getFrameStyle()) {
            case TOUR -> {
                i.setSrc("tour_xxx.png");
                i.setAltText("Tour Frame xxx");
            }
            case SINGLE_SPEED -> {
                i.setSrc("fixie_frame.png");
                i.setAltText("Single Speed Frame");
            }
            case ROAD -> {
                i.setSrc("road_xxx.png");
                i.setAltText("Road Frame xxx");
            }
            case GRAVEL -> {
                i.setSrc("gravel_disc.png");
                i.setAltText("Gravel Frame xxx");
            }
            default -> {
                i.setSrc("no_image.png");
                i.setAltText("No Frame Selected");
            }
        }
        if (b.getBrakeType().getName().equals("Rim")) {
            i.setSrc(i.getSrc().replace("xxx", "rim"));
            i.setAltText(i.getAltText().replace("xxx", "Rim"));
        } else {
            i.setSrc(i.getSrc().replace("xxx", "disc"));
            i.setAltText(i.getAltText().replace("xxx", "Disc"));
        }
        return i;
    }

    private Image chooseBarImage(FullBike b) {
        Image i = new Image(1, "Bars", "no_image.png", "No Bars Selected");
        switch (b.getHandleBarType()) {
            case FLARE -> {
                i.setSrc("flared_bars.png");
                i.setAltText("Flared Bars");
            }
            case DROPS -> {
                i.setSrc("drop_bars.png");
                i.setAltText("Drop Bars");
            }
            case BULLHORNS -> {
                i.setSrc("bullhorn_bars.png");
                i.setAltText("Bull Horn Bars");
            }
            case FLAT -> {
                i.setSrc("flat_bars.png");
                i.setAltText("Flat Bars");
            }
            default -> {
                i.setSrc("no_image.png");
                i.setAltText("No Bars Selected");
            }
        }
        return i;
    }

    private Image chooseBrakeImage(FullBike b) {
        Image i = new Image(2, "Brakes", "shimano_xxx.png", "Shimano xxx Brakes");
        if (b.getBrakeType().getName().equals("NOT_REQUIRED")) {
            i.setSrc("no_image.png");
            i.setAltText("No Brakes Required! Brave ->) ");
        } else if (b.getBrakeType().getName().equals("Rim")) {
            i.setSrc(i.getSrc().replace("xxx", "rim"));
            i.setAltText(i.getAltText().replace("xxx", "Rim"));
        } else {
            i.setSrc(i.getSrc().replace("xxx", "disc"));
            i.setAltText(i.getAltText().replace("xxx", "Disc"));
        }
        return i;
    }

    private Image chooseSTIShiftersImage(FullBike b) {
        Image i = new Image(3, "Shifters", "no_image.png", "No Gears Selected");
        switch ((int) b.getNumberOfRearGears()) {
            case 1 -> {
                i.setSrc("1_STI.png");
                i.setAltText("Single Speed STI");
            }
            case 9 -> {
                i.setSrc("9_STI.png");
                i.setAltText("Nine Speed STI");
            }
            case 10 -> {
                i.setSrc("10_STI.png");
                i.setAltText("Ten Speed STI");
            }
            case 11 -> {
                i.setSrc("11_STI.png");
                i.setAltText("Eleven Speed STI");
            }
            case 12 -> {
                i.setSrc("12_STI.png");
                i.setAltText("Twelve Speed STI");
            }
            default -> {
                i.setSrc("no_image.png");
                i.setAltText("No Gears Selected");
            }
        }
        return i;
    }

    private Image chooseTriggerShiftersImage(FullBike b) {
        Image i = new Image(3, "Shifters", "no_image.png", "No Gears Selected");
        switch ((int) b.getNumberOfRearGears()) {
            case 9 -> {
                i.setSrc("9_trigger.png");
                i.setAltText("Nine Speed STI");
            }
            case 10 -> {
                i.setSrc("10_trigger.png");
                i.setAltText("Ten Speed STI");
            }
            case 11 -> {
                i.setSrc("11_trigger.png");
                i.setAltText("Eleven Speed STI");
            }
            case 12 -> {
                i.setSrc("12_trigger.png");
                i.setAltText("Twelve Speed STI");
            }
            default -> {
                i.setSrc("no_image.png");
                i.setAltText("No Gears Selected");
            }
        }
        return i;
    }

    private Image chooseRearDerailleurImage(FullBike b) {
        Image i = new Image(5, "Rear-Dearailleur", "no_image.png", "No Gears Selected");
        switch ((int) b.getNumberOfRearGears()) {
            case 9 -> {
                i.setSrc("9_derailleur.png");
                i.setAltText("Nine Speed Derailleur");
            }
            case 10 -> {
                i.setSrc("10_derailleur.png");
                i.setAltText("Ten Speed Derailleur");
            }
            case 11 -> {
                i.setSrc("11_derailleur.png");
                i.setAltText("Eleven Speed Derailleur");
            }
            case 12 -> {
                i.setSrc("12_derailleur.png");
                i.setAltText("Twelve Speed Derailleur");
            }
            default -> {
                i.setSrc("no_image.png");
                i.setAltText("No Gears Selected");
            }
        }
        return i;
    }

    private Image chooseChainImage(FullBike b) {
        Image i = new Image(6, "Chain", "no_image.png", "No Gears Selected");
        switch ((int) b.getNumberOfRearGears()) {
            case 1 -> {
                i.setSrc("1_chain.png");
                i.setAltText("Single Speed Chain");
            }
            case 9 -> {
                i.setSrc("9_chain.png");
                i.setAltText("Nine Speed Chain");
            }
            case 10 -> {
                i.setSrc("10_chain.png");
                i.setAltText("Ten Speed Chain");
            }
            case 11 -> {
                i.setSrc("11_chain.png");
                i.setAltText("Eleven Speed Chain");
            }
            case 12 -> {
                i.setSrc("12_chain.png");
                i.setAltText("Twelve Speed Chain");
            }
            default -> {
                i.setSrc("no_image.png");
                i.setAltText("No Gears Selected");
            }
        }
        return i;
    }

    private Image chooseCassetteImage(FullBike b) {
        Image i = new Image(7, "Cassette", "no_image.png", "No Gears Selected");
        switch ((int) b.getNumberOfRearGears()) {
            case 1 -> {
                i.setSrc("1_cassette.png");
                i.setAltText("Single Speed Cog");
            }
            case 9 -> {
                i.setSrc("9_cassette.png");
                i.setAltText("Nine Speed Cassette");
            }
            case 10 -> {
                i.setSrc("10_cassette.png");
                i.setAltText("Ten Speed Cassette");
            }
            case 11 -> {
                i.setSrc("11_cassette.png");
                i.setAltText("Eleven Speed Cassette");
            }
            case 12 -> {
                i.setSrc("12_cassette.png");
                i.setAltText("Twelve Speed Cassette");
            }
            default -> {
                i.setSrc("no_image.png");
                i.setAltText("No Gears Selected");
            }
        }
        return i;
    }

    private Image chooseChainsetImage(FullBike b) {
        Image i = new Image(8, "Chain Set", "no_image.png", "No Gears Selected");
        switch ((int) b.getNumberOfFrontGears()) {
            case 1 -> {
                i.setSrc("1_chainset.png");
                i.setAltText("Single Speed Chainset");
            }
            case 2 -> {
                i.setSrc("2_chainset.png");
                i.setAltText("Double Chainset");
            }
            case 3 -> {
                i.setSrc("3_chainset.png");
                i.setAltText("Triple Chainset");
            }
            default -> {
                i.setSrc("no_image.png");
                i.setAltText("No Gears Selected");
            }
        }
        return i;
    }

    private Image chooseFrontDerailleurImage(FullBike b) {
        Image i = new Image(9, "Front Derailleur", "no_image.png", "No Gears Selected");
        switch ((int) b.getNumberOfFrontGears()) {
            case 1 -> {
                i.setSrc("1_derailleur.png");
                i.setAltText("Chain Catch - Front");
            }
            case 2 -> {
                i.setSrc("2_derailleur.png");
                i.setAltText("Double Front Derailleur");
            }
            case 3 -> {
                i.setSrc("3_derailleur.png");
                i.setAltText("Triple Front Derailleur");
            }
            default -> {
                i.setSrc("no_image.png");
                i.setAltText("No Gears Selected");
            }
        }
        return i;
    }

    private Image chooseWheelImage(FullBike b) {
        Image i = new Image(10, "Wheels", "no_image.png", "No Wheel Preference");
        if (!b.getFrame().getFrameStyle().equals(SINGLE_SPEED)) {
            if (!b.getBrakeType().equals(RIM)) {
                if (b.getWheelPreference().equals("Cheap")) {
                    i.setSrc("cheap_disc_wheels_g.png");
                    i.setAltText("Cheap Disc Brake Wheels");
                } else {
                    i.setSrc("expensive_disc_wheels_g.png");
                    i.setAltText("Expensive Disc Brake Wheels");
                }
            } else {
                if (b.getWheelPreference().equals("Cheap")) {
                    i.setSrc("cheap_rim_wheels_g.png");
                    i.setAltText("Cheap Rim Brake Wheels");
                } else {
                    i.setSrc("expensive_rim_wheels_g.png");
                    i.setAltText("Expensive Rim Brake Wheels");
                }
            }
        } else {
            if (b.getWheelPreference().equals("Cheap")) {
                i.setSrc("cheap_ss_wheels.png");
                i.setAltText("Cheap Single Speed Wheels");
            } else {
                i.setSrc("expensive_ss_wheels.png");
                i.setAltText("Expensive Single Speed Wheels");
            }
        }
        return i;
    }
}