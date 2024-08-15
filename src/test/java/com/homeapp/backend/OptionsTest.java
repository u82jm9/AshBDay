package com.homeapp.backend;

import com.homeapp.backend.models.bike.CombinedData;
import com.homeapp.backend.models.bike.Frame;
import com.homeapp.backend.models.bike.FullBike;
import com.homeapp.backend.models.bike.Options;
import com.homeapp.backend.services.FullBikeService;
import com.homeapp.backend.services.OptionsService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.homeapp.backend.models.bike.Enums.BrakeType.*;
import static com.homeapp.backend.models.bike.Enums.FrameStyle.*;
import static com.homeapp.backend.models.bike.Enums.GroupsetBrand.SHIMANO;
import static com.homeapp.backend.models.bike.Enums.HandleBarType.*;
import static com.homeapp.backend.models.bike.Enums.ShifterStyle.STI;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The Options tests.
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OptionsTest {

    private static boolean isSetupDone = false;

    @Autowired
    private OptionsService optionsService;
    @Autowired
    private FullBikeService fullBikeService;

    @BeforeAll
    public void setup() {
        if (!isSetupDone) {
            fullBikeService.deleteAllBikes();
            Frame frame = new Frame(GRAVEL, true, false, true);
            FullBike bike = new FullBike("bike", frame, MECHANICAL_DISC, SHIMANO, DROPS, 1L, 11L, STI);
            fullBikeService.create(bike);
            Frame frame1 = new Frame(ROAD, false, true, true);
            FullBike bike1 = new FullBike("bike1", frame1, RIM, SHIMANO, DROPS, 2L, 10L, STI);
            fullBikeService.create(bike1);
            isSetupDone = true;
        }
    }

    /**
     * Clearup reloads Bikes from back-up files.
     */
    @AfterAll
    public void clearup() {
        fullBikeService.reloadBikesFromBackup();
    }

    /**
     * Test that start new bike returns correctly.
     */
    @Test
    public void test_That_Start_New_Bike_Returns_Correctly() {
        Options options = optionsService.startNewBike();
        assertEquals(options.getFrameStyles().size(), 4);
        assertTrue(options.isShowFrameSizes());
        assertTrue(options.isShowGroupSetBrand());
        assertEquals(options.getGroupsetBrand().size(), 1);
    }

    /**
     * Test that single speed gets no gear options.
     */
    @Test
    public void test_That_Single_Speed_Gets_No_Gear_Options() {
        Options o = optionsService.startNewBike();
        o.setShowFrameStyles(false);
        FullBike bike = fullBikeService.startNewBike();
        bike.getFrame().setFrameStyle(SINGLE_SPEED);
        CombinedData cd = new CombinedData();
        cd.setBike(bike);
        cd.setOptions(o);
        Options options = optionsService.updateOptions(cd);
        assertFalse(options.isShowFrontGears());
        assertFalse(options.isShowRearGears());
        assertTrue(options.isShowBarStyles());
        assertTrue(options.isShowBrakeStyles());
        assertTrue(options.getBrakeStyles().contains(RIM.getName()));
        assertTrue(options.getBrakeStyles().contains(NOT_REQUIRED.getName()));
        assertEquals(options.getBrakeStyles().size(), 2);
        assertTrue(options.getBarStyles().contains(BULLHORNS.getName()));
        assertTrue(options.getBarStyles().contains(FLAT.getName()));
        assertTrue(options.getBarStyles().contains(DROPS.getName()));
        assertEquals(options.getBarStyles().size(), 3);
    }

    /**
     * Test that road gets right options.
     */
    @Test
    public void test_That_Road_Gets_Right_Options() {
        Options o = optionsService.startNewBike();
        FullBike bike = fullBikeService.startNewBike();
        bike.getFrame().setFrameStyle(ROAD);
        o.setShowFrameStyles(false);
        CombinedData cd = new CombinedData();
        cd.setBike(bike);
        cd.setOptions(o);
        Options options = optionsService.updateOptions(cd);
        assertTrue(options.isShowFrontGears());
        assertTrue(options.isShowRearGears());
        assertTrue(options.isShowBarStyles());
        assertTrue(options.isShowWheelPreference());
        assertTrue(options.getBrakeStyles().contains(MECHANICAL_DISC.getName()));
        assertTrue(options.getBrakeStyles().contains(HYDRAULIC_DISC.getName()));
        assertEquals(options.getBrakeStyles().size(), 3);
        assertTrue(options.getBarStyles().contains(DROPS.getName()));
        assertFalse(options.getBarStyles().contains(FLARE.getName()));
        assertFalse(options.getBarStyles().contains(FLAT.getName()));
        assertEquals(options.getBarStyles().size(), 1);
        assertTrue(options.getNumberOfRearGears().contains(9L));
        assertTrue(options.getNumberOfRearGears().contains(10L));
        assertTrue(options.getNumberOfRearGears().contains(11L));
        assertTrue(options.getNumberOfRearGears().contains(12L));
        assertEquals(options.getNumberOfRearGears().size(), 4);
        assertTrue(options.getNumberOfFrontGears().contains(2L));
        assertEquals(options.getNumberOfFrontGears().size(), 1);
        assertTrue(options.getWheelPreference().contains("Cheap"));
        assertTrue(options.getWheelPreference().contains("Expensive"));
    }

    /**
     * Test that gravel gets right options.
     */
    @Test
    public void test_That_Gravel_Gets_Right_Options() {
        Options o = optionsService.startNewBike();
        o.setShowFrameStyles(false);
        FullBike bike = fullBikeService.startNewBike();
        bike.getFrame().setFrameStyle(GRAVEL);
        CombinedData cd = new CombinedData();
        cd.setBike(bike);
        cd.setOptions(o);
        Options options = optionsService.updateOptions(cd);
        assertTrue(options.isShowFrontGears());
        assertTrue(options.isShowRearGears());
        assertTrue(options.isShowBarStyles());
        assertTrue(options.isShowWheelPreference());
        assertTrue(options.getBrakeStyles().contains(RIM.getName()));
        assertTrue(options.getBrakeStyles().contains(MECHANICAL_DISC.getName()));
        assertTrue(options.getBrakeStyles().contains(HYDRAULIC_DISC.getName()));
        assertEquals(options.getBrakeStyles().size(), 3);
        assertTrue(options.getBarStyles().contains(DROPS.getName()));
        assertTrue(options.getBarStyles().contains(FLARE.getName()));
        assertEquals(options.getBarStyles().size(), 2);
        assertTrue(options.getNumberOfRearGears().contains(9L));
        assertTrue(options.getNumberOfRearGears().contains(10L));
        assertTrue(options.getNumberOfRearGears().contains(11L));
        assertEquals(options.getNumberOfRearGears().size(), 3);
        assertTrue(options.getNumberOfFrontGears().contains(2L));
        assertEquals(options.getNumberOfFrontGears().size(), 1);
        assertTrue(options.getWheelPreference().contains("Cheap"));
        assertTrue(options.getWheelPreference().contains("Expensive"));
    }

    /**
     * Test that tour gets right options.
     */
    @Test
    public void test_That_Tour_Gets_Right_Options() {
        Options o = optionsService.startNewBike();
        o.setShowFrameStyles(false);
        FullBike bike = fullBikeService.startNewBike();
        bike.getFrame().setFrameStyle(TOUR);
        CombinedData cd = new CombinedData();
        cd.setBike(bike);
        cd.setOptions(o);
        Options options = optionsService.updateOptions(cd);
        assertTrue(options.isShowFrontGears());
        assertTrue(options.isShowRearGears());
        assertTrue(options.isShowBarStyles());
        assertTrue(options.isShowWheelPreference());
        assertTrue(options.getBrakeStyles().contains(RIM.getName()));
        assertTrue(options.getBrakeStyles().contains(MECHANICAL_DISC.getName()));
        assertTrue(options.getBrakeStyles().contains(HYDRAULIC_DISC.getName()));
        assertEquals(options.getBrakeStyles().size(), 3);
        assertTrue(options.getBarStyles().contains(DROPS.getName()));
        assertTrue(options.getBarStyles().contains(FLARE.getName()));
        assertTrue(options.getBarStyles().contains(FLAT.getName()));
        assertEquals(options.getBarStyles().size(), 3);
        assertTrue(options.getNumberOfRearGears().contains(9L));
        assertTrue(options.getNumberOfRearGears().contains(10L));
        assertTrue(options.getNumberOfRearGears().contains(11L));
        assertEquals(options.getNumberOfRearGears().size(), 3);
        assertTrue(options.getNumberOfFrontGears().contains(3L));
        assertTrue(options.getNumberOfFrontGears().contains(2L));
        assertEquals(options.getNumberOfFrontGears().size(), 2);
        assertTrue(options.getWheelPreference().contains("Cheap"));
        assertTrue(options.getWheelPreference().contains("Expensive"));
    }
}