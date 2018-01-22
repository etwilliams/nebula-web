package com.dakuupa.nebula.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author etwilliams
 */
public class NebulaLogger {

    private static final String INFO = "INFO";
    private static final String WARNING = "WARN";
    private static final String EXCEPTION = "SEVERE";
    private static final String TYPE_SPACER = ": ";
    private static File logFile;

    public static void info(String tag, String msg, Object[] objs) {
        String log = INFO + TYPE_SPACER + getMessage(tag, msg, objs);
        log(log);
    }

    public static void info(String tag, String msg, String obj) {
        info(tag, msg, new Object[]{obj});
    }

    public static void info(String tag, String msg) {
        info(tag, msg, new Object[]{""});
    }

    public static void warn(String tag, String msg, Object[] objs) {
        String log = WARNING + TYPE_SPACER + getMessage(tag, msg, objs);
        log(log);
    }

    public static void warn(String tag, String msg, String obj) {
        warn(tag, msg, new Object[]{obj});
    }

    public static void warn(String tag, String msg) {
        warn(tag, msg, new Object[]{""});
    }

    public static void exception(String tag, String msg, Exception ex) {
        String log = EXCEPTION + TYPE_SPACER + getMessage(tag, msg + " " + ex.getMessage(), new Object[]{});
        log(log);
    }

    public static void exception(String tag, Exception ex) {
        String log = EXCEPTION + TYPE_SPACER + getMessage(tag, ex.getMessage(), new Object[]{});
        log(log);
    }

    private static String getMessage(String tag, String str, Object[] objs) {
        try {
            if (str != null) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                return dateFormatter.format(new Date()) + "\t" + tag + " " + String.format(str, objs);
            }
        } catch (Exception e) {
            exception(tag, e);
            return "ERROR with string " + str;
        }
        return null;
    }

    private static void log(String logMessage) {
        //System.out.println(logMessage);
        writeToLogFile(logMessage);
    }

    private static void writeToLogFile(String logMessage) {
        if (logFile != null && logFile.exists() && logFile.canWrite()) {
            try (FileOutputStream fos = new FileOutputStream(logFile, true); OutputStreamWriter osw = new OutputStreamWriter(fos)) {
                osw.write(logMessage);
                osw.write("\n");
                osw.flush();
                osw.close();
            } catch (Exception ex) {

            }
        }

    }

    public static void setLogFile(File nlogFile) {
        logFile = nlogFile;
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(NebulaLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
