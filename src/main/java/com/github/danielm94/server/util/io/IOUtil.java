package com.github.danielm94.server.util.io;

import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Flogger
public class IOUtil {
    private IOUtil() {
    }

    /**
     * Converts the content of an InputStream into a String.
     * <p>
     * This method reads the input stream and builds a String out of its content.
     * It checks if the BufferedReader is ready before attempting to read.
     * In case of an IOException during the check or read operations,
     * the method logs the exception and returns what has been read up to that point.
     * </p>
     *
     * @param stream The InputStream to be converted into a String. If the stream is null,
     *               a warning is logged and the method returns null.
     * @return A String representing the content of the InputStream. If an IOException
     * occurs during reading, it returns the partial string read up to the point
     * of the exception. Returns null if the InputStream is null, or if an exception
     * occurs before any content is read.
     */
    public static String parseInputStreamToText(InputStream stream) {
        if (stream == null) {
            log.atWarning().log("Could not parse InputStream into a String as it was null.");
            return null;
        }

        val inputStreamReader = new InputStreamReader(stream);
        val bufferedReader = new BufferedReader(inputStreamReader);
        val stringBuilder = new StringBuilder();

        while (true) {
            try {
                if (!bufferedReader.ready()) break;
            } catch (IOException e) {
                log.atWarning()
                   .withCause(e)
                   .log("IOException occurred while checking if the bufferedReader is ready. " +
                           "Utility was only able to process up until: [%s]", stringBuilder.toString());
                return null;
            }
            var character = 0;
            try {
                character = (char) bufferedReader.read();
            } catch (IOException e) {
                log.atWarning()
                   .withCause(e)
                   .log("Failed to read next character from buffered reader. Utility was only able to read up until: [%s]", stringBuilder.toString());
                return null;
            }
            stringBuilder.append((char) character);
        }

        var result = stringBuilder.toString();
        log.atFine().log("Parsed the following string from the input stream: %s", result);
        return result;
    }
}
