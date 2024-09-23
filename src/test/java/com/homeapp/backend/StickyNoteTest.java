package com.homeapp.backend;

import com.homeapp.backend.models.note.StickyNote;
import com.homeapp.backend.services.StickyNoteService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The Sticky Note tests.
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class StickyNoteTest {

    private static boolean isSetupDone = false;
    @Autowired
    private StickyNoteService stickyNoteService;

    /**
     * Sets up testing suite.
     * Uses a boolean to ensure test suite is only set once.
     * New specific test Notes are added. This is to make testing more rigid and predictable.
     */
    @BeforeEach
    public void setup() {
        if (!isSetupDone) {
            Map<String, Boolean> map1 = new HashMap<>();
            map1.put("This is the message for the before all method", false);
            StickyNote note = new StickyNote("Before All Method", map1, false);
            stickyNoteService.create(note);
            Map<String, Boolean> map2 = new HashMap<>();
            map2.put("This is the message for the second before all method", true);
            StickyNote note2 = new StickyNote("Second Before All Method", map2, true);
            stickyNoteService.create(note2);
            Map<String, Boolean> map3 = new HashMap<>();
            map3.put("This is the message for the third before all method", true);
            map3.put("This message is not complete", false);
            map3.put("Putting in a second message, that is complete", true);
            StickyNote note3 = new StickyNote("Third Before All Method", map3, false);
            stickyNoteService.create(note3);
            stickyNoteService.create("Go for a run!", "Seriously get up early and go for a run!!\nYou're just being lazy!", false);
            stickyNoteService.create("Gardening Work Left", "Dig more soil from Zebo. Flatten front and back lawns. Seed new grass. Fix nasty bit behind shed", true);
            stickyNoteService.create("Nothing Useful", "This is just to make an extra Sticky Note as I thought 4 would look better than 3!", true);
            isSetupDone = true;
        }
    }

    /**
     * Clearup after tests complete.
     * Replaces the bikes on file from the back-up version, to remove all testing impact on the proper FE bikes.
     */
    @AfterAll
    public void clearup() {
        stickyNoteService.reloadNotesFromBackup();
    }

    /**
     * Test that retrieve all returns list of notes.
     */
    @Test
    public void test_That_Retrieve_All_Returns_List_Of_Notes() {
        List<StickyNote> stickyNoteList = stickyNoteService.retrieveAllNotes();
        assertNotNull(stickyNoteList);
    }

    /**
     * Test that retrieve all returns list of notes.
     */
    @Test
    public void test_That_Complete_Messages_Are_First() {
        List<StickyNote> stickyNoteList = stickyNoteService.retrieveAllNotes();
        for (StickyNote n : stickyNoteList) {
            if (n.getTitle().equals("Third Before All Method")) {
                List<Map.Entry<String, Boolean>> messages = n.getMessageMap().entrySet().stream().toList();
                assertTrue(messages.get(0).getValue());
                assertTrue(messages.get(1).getValue());
                assertFalse(messages.get(2).getValue());
            }
        }
    }

    /**
     * Test that a sticky note can be created.
     */
    @Test
    public void test_That_A_StickyNote_Can_be_Created() {
        int notesBefore = stickyNoteService.retrieveAllNotes().size();
        Map<String, Boolean> map = new HashMap<>();
        map.put("First test", true);
        StickyNote note = new StickyNote("Title for test", map, true);
        stickyNoteService.create(note);
        assertTrue(notesBefore < stickyNoteService.retrieveAllNotes().size());
    }

    /**
     * Test that a sticky note can be retrieved by id.
     */
    @Test
    public void test_That_A_StickyNote_Can_Be_Retrieved_By_Id() {
        assertNotNull(stickyNoteService.retrieveById(2L));
    }

    /**
     * Test that a stick note can be edited.
     */
    @Test
    public void test_That_A_StickNote_Can_Be_Edited() {
        StickyNote noteBefore = stickyNoteService.retrieveById(2L);
        Map<String, Boolean> mapBefore = noteBefore.getMessageMap();
        Map<String, Boolean> map = new HashMap<>();
        map.put("Test Message", false);
        map.put("Test Message2", false);
        map.put("Test Messagee", true);
        noteBefore.setMessageMap(map);
        stickyNoteService.editStickyNote(noteBefore);
        Map<String, Boolean> mapAfter = stickyNoteService.retrieveById(2L).getMessageMap();
        assertNotEquals(mapAfter, mapBefore);
    }

    /**
     * Test that a sticky note can be retrieved by title.
     */
    @Test
    public void test_That_A_StickyNote_Can_Be_Retrieved_By_Title() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("Message", false);
        StickyNote testNote = new StickyNote("Before All Method", map, false);
        assertNotNull(stickyNoteService.retrieveByTitle(testNote.getTitle()));
    }

    /**
     * Test that two notes cannot have the same title.
     */
    @Test
    public void test_That_Two_Notes_Cannot_Have_The_Same_Title() {
        Map<String, Boolean> map1 = new HashMap<>();
        map1.put("Test Message", false);
        StickyNote note1 = new StickyNote("Test Note", map1, false);
        stickyNoteService.create(note1);
        Map<String, Boolean> map2 = new HashMap<>();
        map1.put("Different Message", false);
        int notesBefore = stickyNoteService.retrieveAllNotes().size();
        StickyNote note2 = new StickyNote("Test Note", map2, false);
        stickyNoteService.create(note2);
        int notesAfter = stickyNoteService.retrieveAllNotes().size();
        assertEquals(notesAfter, notesBefore);
    }

    /**
     * Test that a sticky note can be deleted.
     */
    @Test
    public void test_That_A_StickyNote_Can_Be_Deleted() {
        int notesBefore = stickyNoteService.retrieveAllNotes().size();
        StickyNote note = stickyNoteService.retrieveById(2L);
        stickyNoteService.deleteNote(note);
        isSetupDone = false;
        int notesAfter = stickyNoteService.retrieveAllNotes().size();
        assertTrue(notesAfter < notesBefore);
    }

    /**
     * Test all sticky notes can be deleted.
     */
    @Test
    public void test_All_StickyNotes_Can_Be_Deleted() {
        stickyNoteService.create("Delete me!", "I will be deleted, goodbye. But I will have served a purpose. Please remember me.", true);
        stickyNoteService.deleteAll();
        isSetupDone = false;
        assertNotNull(stickyNoteService.retrieveAllNotes());
    }
}