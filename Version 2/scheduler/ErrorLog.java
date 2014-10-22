package edu.sdstate.eastweb.prototype.scheduler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import edu.sdstate.eastweb.prototype.Config;

public final class ErrorLog {
    private ErrorLog() {
    }

    private static final Object sErrorLogLock = new Object();
    private static PrintStream sErrorLogPrintStream;

    /**
     * Reports an error.
     * @param message Error message, suitable for presentation to the user
     * @param cause Cause of the error, may be null
     */
    public static void add(String message, Throwable cause) {
        synchronized (sErrorLogLock) {
            printToLogFile(message, cause);
            printToStderr(message, cause);
        }
    }

    private static void printToLogFile(String message, Throwable cause) {
        try {
            if (sErrorLogPrintStream == null) {
                final GregorianCalendar cal = new GregorianCalendar(TimeZone.getDefault());
                final FileOutputStream fos = new FileOutputStream(String.format(
                        "%s/errors.%04d-%02d-%02d.%02d%02d%02d.log",
                        Config.getInstance().getRootDirectory(),
                        cal.get(GregorianCalendar.YEAR),
                        cal.get(GregorianCalendar.MONTH) + 1,
                        cal.get(GregorianCalendar.DAY_OF_MONTH),
                        cal.get(GregorianCalendar.HOUR_OF_DAY),
                        cal.get(GregorianCalendar.MINUTE),
                        cal.get(GregorianCalendar.SECOND)
                ));
                sErrorLogPrintStream = new PrintStream(fos);
            }

            sErrorLogPrintStream.println(message);
            if (cause != null) {
                cause.printStackTrace(sErrorLogPrintStream);
            }
            sErrorLogPrintStream.println();
            sErrorLogPrintStream.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to the error log");
        }
    }

    private static void printToStderr(String message, Throwable cause) {
        System.err.println("ERROR: " + message);
        if (cause != null) {
            cause.printStackTrace();
        }
    }
}