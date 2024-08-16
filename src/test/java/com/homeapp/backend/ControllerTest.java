package com.homeapp.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeapp.backend.controller.StickyNoteController;
import com.homeapp.backend.models.DTOJoke;
import com.homeapp.backend.models.DTOLog;
import com.homeapp.backend.models.bike.Frame;
import com.homeapp.backend.models.bike.FrontGears;
import com.homeapp.backend.models.bike.FullBike;
import com.homeapp.backend.models.bike.RearGears;
import com.homeapp.backend.models.note.DTOnote;
import com.homeapp.backend.models.note.StickyNote;
import com.homeapp.backend.services.FullBikeService;
import com.homeapp.backend.services.SaveJokeService;
import com.homeapp.backend.services.StickyNoteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static com.homeapp.backend.models.bike.Enums.BrakeType.*;
import static com.homeapp.backend.models.bike.Enums.FrameStyle.GRAVEL;
import static com.homeapp.backend.models.bike.Enums.FrameStyle.ROAD;
import static com.homeapp.backend.models.bike.Enums.GroupsetBrand.SHIMANO;
import static com.homeapp.backend.models.bike.Enums.GroupsetBrand.SRAM;
import static com.homeapp.backend.models.bike.Enums.HandleBarType.DROPS;
import static com.homeapp.backend.models.bike.Enums.ShifterStyle.STI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Controller test.
 */
@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ControllerTest {

    /**
     * The constant TEST_API_URL.
     */
    final static String TEST_API_URL = "/Test/";
    /**
     * The constant STICKY_NOTE_URL.
     */
    final static String STICKY_NOTE_URL = "/StickyNotes/";
    /**
     * The constant FULL_BIKE_URL.
     */
    final static String FULL_BIKE_URL = "/FullBike/";
    /**
     * The constant OPTIONS_URL.
     */
    final static String OPTIONS_URL = "/Options/";
    private static boolean isSetupDone = false;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StickyNoteController stickyNoteController;
    @Autowired
    private FullBikeService fullBikeService;
    @Autowired
    private StickyNoteService stickyNoteService;
    @Autowired
    private SaveJokeService saveJokeService;
    private MockMvc mockMvc;
    private MockHttpSession session;

    /**
     * Sets up testing suite.
     * Uses a boolean to ensure test suite is only set once.
     * First all bikes on file are deleted, then new specific test bikes are added. This is to make testing more rigid and predictable.
     */
    @BeforeEach
    public void setup() {
        if (!isSetupDone) {
            saveJokeService.deleteAllJokes();
            fullBikeService.deleteAllBikes();
            Frame frame = new Frame(GRAVEL, true, false, true);
            FullBike bike = new FullBike("bike", frame, MECHANICAL_DISC, SHIMANO, DROPS, 1L, 11L, STI);
            fullBikeService.create(bike);
            Frame frame1 = new Frame(ROAD, false, true, true);
            FullBike bike1 = new FullBike("bike1", frame1, RIM, SHIMANO, DROPS, 2L, 10L, STI);
            fullBikeService.create(bike1);

            Map<String, Boolean> map3 = new HashMap<>();
            map3.put("This is the message for the third before all method", false);
            StickyNote note3 = new StickyNote("Third Before All Method", map3, false);
            stickyNoteService.create(note3);
            stickyNoteService.create("Gardening Work Left", "Dig more soil from Zebo. Flatten front and back lawns. Seed new grass. Fix nasty bit behind shed", true);
            stickyNoteService.create("Nothing Useful", "This is just to make an extra Sticky Note as I thought 4 would look better than 3!", true);
            stickyNoteService.create("Go for a run!", "Seriously get up early and go for a run!!\nYou're just being lazy!", false);
            isSetupDone = true;
        }
        this.session = new MockHttpSession();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SharedHttpSessionConfigurer.sharedHttpSession()).build();
    }

    /**
     * Closes active session and Mock MVC after each test.
     */
    @AfterEach
    public void close() {
        this.session = null;
        this.mockMvc = null;
    }

    /**
     * Clearup reloads Sticky Notes and Bikes from back-up files.
     */
    @AfterAll
    public void clearup() {
        fullBikeService.reloadBikesFromBackup();
        stickyNoteService.reloadNotesFromBackup();
        saveJokeService.reloadJokesFromBackup();
    }


    /**
     * Test Options start new Bike API return HTTP - status OK
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_Options_is_returned_with_Brands() throws Exception {
        this.mockMvc.perform(get(OPTIONS_URL + "StartNewBike"))
                .andExpect(status().isOk());
    }

    /**
     * Test that the front can send a joke back with null setup and punchline.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_the_FE_can_send_a_Joke_no_body_only() throws Exception {
        DTOJoke joke = new DTOJoke();
        joke.setSetup("Test Joke Setup");
        joke.setPunchline("Some witty punchline");
        this.mockMvc.perform(post(TEST_API_URL + "SaveThis").session(session).contentType("application/json")
                .content(objectMapper.writeValueAsString(joke))).andExpect(status().isCreated());
    }

    /**
     * Test that the front can send a joke back with null setup and punchline.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_the_FE_can_send_a_Joke_body_only() throws Exception {
        DTOJoke joke = new DTOJoke();
        joke.setBody("Test Joke Body");
        this.mockMvc.perform(post(TEST_API_URL + "SaveThis").session(session).contentType("application/json")
                .content(objectMapper.writeValueAsString(joke))).andExpect(status().isCreated());
    }

    /**
     * Test that the front can send a log back.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_the_front_can_send_a_log() throws Exception {
        DTOLog log = new DTOLog("INFO", "TEST LOG!! TESTING LOGS", "today-date");
        this.mockMvc.perform(post(TEST_API_URL + "LogThis").session(session).contentType("application/json")
                .content(objectMapper.writeValueAsString(log))).andExpect(status().isCreated());
    }

    /**
     * Test that the front can check the BE is running.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_the_front_can_check_back_end_is_on() throws Exception {
        this.mockMvc.perform(get(TEST_API_URL + "IsThisThingOn"))
                .andExpect(status().isOk());
    }

    /**
     * Test that a note can be created.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_a_note_can_be_created() throws Exception {
        DTOnote DTOnote = new DTOnote("Controller test", "Test Message", false);
        this.mockMvc.perform(post(STICKY_NOTE_URL + "AddNote").session(session).contentType("application/json")
                .content(objectMapper.writeValueAsString(DTOnote))).andExpect(status().isCreated());
    }

    /**
     * Test that a list of notes can be returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_a_list_of_Notes_can_be_returned() throws Exception {
        this.mockMvc.perform(get(STICKY_NOTE_URL + "GetAll"))
                .andExpect(status().isAccepted());
    }

    /**
     * Test that a single note can be deleted.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_a_single_Note_can_be_deleted() throws Exception {
        this.mockMvc.perform(delete(STICKY_NOTE_URL + "DeleteNote/2"))
                .andExpect(status().isOk());
        isSetupDone = false;
    }

    /**
     * Test that all notes can be deleted.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_all_Notes_can_be_deleted() throws Exception {
        this.mockMvc.perform(delete(STICKY_NOTE_URL + "DeleteAllNotes"))
                .andExpect(status().isOk());
        isSetupDone = false;
    }

    /**
     * Test that a note can be edited.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_a_Note_can_be_edited() throws Exception {
        Map<String, Boolean> map = new HashMap<>();
        map.put("Do it NOW!", false);
        StickyNote note = new StickyNote(2, "Paint! Boo", map, false);
        this.mockMvc.perform(post(STICKY_NOTE_URL + "EditNote").session(session).contentType("application/json")
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(status().isOk());
    }

    /**
     * Test that a list of full bikes can be returned.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_a_list_of_Full_Bikes_can_be_returned() throws Exception {
        this.mockMvc.perform(get(FULL_BIKE_URL + "GetAll"))
                .andExpect(status().isAccepted());
    }

    /**
     * Test that a single bike can be deleted.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_a_single_Bike_can_be_deleted() throws Exception {
        FullBike bike = fullBikeService.getBikeUsingName("bike1").get();
        this.mockMvc.perform(delete(FULL_BIKE_URL + "DeleteBike").session(session).contentType("application/json").content(objectMapper.writeValueAsString(bike)))
                .andExpect(status().isAccepted());
        isSetupDone = false;
    }

    /**
     * Test that an empty bike can be created.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_an_empty_bike_can_be_created() throws Exception {
        FullBike bike = new FullBike();
        this.mockMvc.perform(post(FULL_BIKE_URL + "AddFullBike").session(session).contentType("application/json")
                .content(objectMapper.writeValueAsString(bike))).andExpect(status().isCreated());
    }

    /**
     * Test that an in flight bike can be updated.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_an_in_flight_bike_can_be_updated() throws Exception {
        Frame frame = new Frame(GRAVEL, true, false, true);
        FullBike bike = new FullBike("bike", frame, HYDRAULIC_DISC, SRAM, DROPS, 1L, 11L, STI);
        bike.setBikeName("Bike Update");
        this.mockMvc.perform(post(FULL_BIKE_URL + "UpdateBike").session(session).contentType("application/json")
                .content(objectMapper.writeValueAsString(bike))).andExpect(status().isAccepted());
    }

    /**
     * Test that a fully defined bike can be created.
     *
     * @throws Exception the exception
     */
    @Test
    public void test_That_a_fully_defined_bike_can_be_created() throws Exception {
        Frame frame = new Frame(GRAVEL, false, true, true);
        FrontGears frontGears = new FrontGears(1);
        RearGears rearGears = new RearGears(11);
        FullBike testBike = new FullBike("testBike", frame, MECHANICAL_DISC, SHIMANO, DROPS, 1L, 11L, STI);
        this.mockMvc.perform(post(FULL_BIKE_URL + "AddFullBike").session(session).contentType("application/json")
                .content(objectMapper.writeValueAsString(testBike))).andExpect(status().isCreated());
    }
}