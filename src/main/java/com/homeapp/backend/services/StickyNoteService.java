package com.homeapp.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeapp.backend.models.logger.ErrorLogger;
import com.homeapp.backend.models.logger.InfoLogger;
import com.homeapp.backend.models.logger.WarnLogger;
import com.homeapp.backend.models.note.StickyNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Sticky Note Service class.
 */
@Service
public class StickyNoteService {

    private static final ObjectMapper om = new ObjectMapper();
    private static final String JSON_NOTES_FILE = "src/main/resources/notes.json";
    private static final String JSON_NOTES_FILE_BACKUP = "src/main/resources/notes_backup.json";
    private final InfoLogger infoLogger = new InfoLogger();
    private final WarnLogger warnLogger = new WarnLogger();
    private final ErrorLogger errorLogger = new ErrorLogger();
    private List<StickyNote> notesList;

    /**
     * Instantiates a new Sticky Note Service.
     * Reads all the notes located on File.
     */
    @Autowired
    public StickyNoteService() {
        this.notesList = readNotesFile();
    }

    private List<StickyNote> readNotesFile() {
        infoLogger.log("Reading Sticky Notes From File");
        try {
            File file = new File(JSON_NOTES_FILE);
            List<StickyNote> notes = om.readValue(file, new TypeReference<>() {
            });
            warnLogger.log("Returning Populated list of Sticky Notes from file, number of notes: " + notes.size());
            return notes;
        } catch (IOException e) {
            errorLogger.log("An IOException occurred from: readNotesFile!!See error message: " + e.getMessage() + "!!From: " + getClass());
        }
        warnLogger.log("Returning Empty list of Sticky Notes from file");
        return new ArrayList<>();
    }

    /**
     * Reload notes from backup.
     */
    public void reloadNotesFromBackup() {
        infoLogger.log("Reloading Sticky Notes From Backup File");
        try {
            deleteAll();
            File file = new File(JSON_NOTES_FILE_BACKUP);
            List<StickyNote> notes = om.readValue(file, new TypeReference<>() {
            });
            writeNotesToFile(notes);
        } catch (IOException e) {
            errorLogger.log("An IOException occurred from: reloadNotesFromBackup!!See error message: " + e.getMessage() + "!!From: " + getClass());
        }
    }

    /**
     * Write notes to file.
     *
     * @param list the list
     */
    public void writeNotesToFile(List<StickyNote> list) {
        infoLogger.log("Writing Sticky Notes back to File");
        try {
            om.writeValue(new File(JSON_NOTES_FILE), list);
            notesList = list;
        } catch (IOException e) {
            errorLogger.log("An IOException occurred from: writeNotesToFile!!See error message: " + e.getMessage() + "!!From: " + getClass());
        }
    }

    /**
     * Create Sticky Note from simple String objects.
     * Splits single String message into a new Hashmap and set each to the passed in boolean.
     *
     * @param title    the title
     * @param message  the message
     * @param complete the complete
     */
    public void create(String title, String message, Boolean complete) {
        infoLogger.log("Creating new Sticky Note");
        String newMessage = message.replace(".\s", "\n");
        Map<String, Boolean> map = new HashMap<>();
        String[] array = newMessage.split("\n");
        for (String s : array) {
            map.put(s, complete);
        }
        StickyNote n = new StickyNote();
        n.setTitle(title);
        n.setMessageMap(map);
        n.setComplete(complete);
        warnLogger.log("Created note: " + n);
        create(n);
    }

    /**
     * Create.
     *
     * @param note the note
     */
    public void create(StickyNote note) {
        infoLogger.log("Creating new Sticky Note");
        if (checkNoteTitle(note.getTitle())) {
            warnLogger.log("Sticky note with this title already exists, not creating a new one!");
        } else {
            warnLogger.log("Adding Sticky Note with title: " + note.getTitle());
            long newId = notesList.size() + 1;
            note.setStickyNoteId(newId);
            notesList.add(note);
            writeNotesToFile(notesList);
        }
    }

    private boolean checkNoteTitle(String title) {
        return notesList.stream().anyMatch(n -> n.getTitle().equals(title));
    }

    private boolean checkNoteId(Long id) {
        return notesList.stream().anyMatch(n -> n.getStickyNoteId() == id);
    }

    /**
     * Retrieve all notes list.
     *
     * @return the list
     */
    public List<StickyNote> retrieveAllNotes() {
        infoLogger.log("Getting all notes");
        notesList.sort(Comparator.comparing((StickyNote::getTitle)));
        for (StickyNote sn : notesList) {
            Map<String, Boolean> sortedMessages = sn.getMessageMap().entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (n1, n2) -> n1, LinkedHashMap::new));
            sn.setMessageMap(sortedMessages);
        }
        warnLogger.log("Number of notes found: " + notesList.size());
        return notesList;
    }

    /**
     * Retrieve by title sticky note.
     *
     * @param title the title
     * @return the sticky note
     */
    public StickyNote retrieveByTitle(String title) {
        infoLogger.log("Retrieving by Title, Sticky Note with Title: " + title);
        StickyNote noteFromFile = notesList.stream().filter(note -> note.getTitle().equals(title)).toList().get(0);
        if (checkNoteTitle(title)) {
            warnLogger.log("Retrieving by Title, Sticky Note: " + noteFromFile);
            return noteFromFile;
        } else {
            warnLogger.log("Could not retrieve Sticky Note by title for: " + title);
            return null;
        }
    }

    /**
     * Retrieve by id sticky note.
     *
     * @param id the id
     * @return the sticky note
     */
    public StickyNote retrieveById(Long id) {
        infoLogger.log("Retrieving by ID, Sticky Note with ID: " + id);
        StickyNote noteFromFile = notesList.stream().filter(note -> note.getStickyNoteId() == id).toList().get(0);
        if (checkNoteId(id)) {
            warnLogger.log("Retrieving by ID, Sticky Note: " + noteFromFile);
            return noteFromFile;
        } else {
            warnLogger.log("Could not retrieve Sticky Note by ID for: " + id);
            return null;
        }
    }

    /**
     * Edit sticky note.
     *
     * @param note the note
     */
    public void editStickyNote(StickyNote note) {
        infoLogger.log("Editing Sticky Note: " + note.getTitle());
        StickyNote noteFromFile = retrieveById(note.getStickyNoteId());
        warnLogger.log("Note Before edit: " + noteFromFile);
        noteFromFile.setTitle(note.getTitle());
        noteFromFile.setMessageMap(note.getMessageMap());
        noteFromFile = updateNoteComplete(note);
        warnLogger.log("Note After edit: " + noteFromFile);
        notesList.removeIf(n -> n.getTitle().equals(note.getTitle()));
        notesList.add(noteFromFile);
        writeNotesToFile(notesList);
    }

    /**
     * Delete note.
     *
     * @param note the note
     */
    public void deleteNote(StickyNote note) {
        notesList.removeIf(n -> n.getStickyNoteId() == note.getStickyNoteId());
        writeNotesToFile(notesList);
    }

    /**
     * Delete all.
     */
    public void deleteAll() {
        writeNotesToFile(new ArrayList<>());
    }

    private StickyNote updateNoteComplete(StickyNote note) {
        infoLogger.log("Updating if note is complete or not");
        if (note.getMessageMap().containsValue(false)) {
            warnLogger.log("Setting note to not complete, note: " + note);
            note.setComplete(false);
        } else {
            warnLogger.log("Setting note to complete, note: " + note);
            note.setComplete(true);
        }
        return note;
    }
}