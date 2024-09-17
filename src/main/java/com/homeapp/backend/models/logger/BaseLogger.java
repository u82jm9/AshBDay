package com.homeapp.backend.models.logger;

import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

/**
 * The Abstract Base logger.
 * Used to ensure all Loggers have the same methods, through class extension.
 */
public abstract class BaseLogger {
    /**
     * The Logs stamp formatter. Used at the start of each log to record the time the log was written.
     */
    static final DateTimeFormatter LOGS_STAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
    /**
     * The File name formatter. Used to create a String for the date, which becomes part of the DTOLog file name. Using Iso standard of MM-dd-yyyy to help store files in correct order.
     */
    static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ISO_DATE;

    /**
     * Instantiates a new Base logger.
     * Each time a logger is Instantiated it runs the readLogsFile and overrides the TreeSet logs.
     */
    public BaseLogger() {
    }

    /**
     * Read logs file and returns a tree set.
     *
     * @return the tree set
     */
    protected abstract TreeSet<String> readLogsFile();

    /**
     * Gets file name.
     *
     * @return the file name
     */
    protected abstract String getFileName();

    /**
     * DTOLog to file, writes log message out to the correct file
     */
    protected abstract void logToFile();


    /**
     * DTOLog the passed in String
     *
     * @param message the message
     */
    protected abstract void log(String message);
}
