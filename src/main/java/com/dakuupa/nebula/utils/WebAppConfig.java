package com.dakuupa.nebula.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Eric
 */
public class WebAppConfig {

    //web port and database settings
    public static final String LOG_TAG = "Nebula Config";

    public static final String APP_CONTEXT_PATH = PropertiesReader.getProperty("appcontext");
    public static final String SERVER_PORT = PropertiesReader.getProperty("port");
    public static final String DB_LOCATION = PropertiesReader.getProperty("dbhost");
    public static final String DB_PORT = PropertiesReader.getProperty("dbport");
    public static final String DB_NAME = PropertiesReader.getProperty("dbname");
    public static final String DB_USERNAME = PropertiesReader.getProperty("dbuser");
    public static final String DB_PASSWORD = PropertiesReader.getProperty("dbpassword");
    //server-wide debug flag
    public static boolean DEBUG = false;
    public static final String NEBULA_DIRECTORY = "/usr/local/satfi/";
    public static final String VERSION_FILENAME = "version.txt";
    public static final String ACTIVITY_PATH = PropertiesReader.getProperty("activitypath");
    public static final String ACTIVITY_PREFIX = PropertiesReader.getProperty("activityprefix");
    public static final String ACTIVITY_SUFFIX = PropertiesReader.getProperty("activitysuffix");
    public static final String HANDLER_PATH = PropertiesReader.getProperty("handlerpath");
    public static final List<Class> HANDLERS;

    static {
        HANDLERS = new ArrayList<>();

        String handlersConf = PropertiesReader.getProperty("handlers");

        if (StringUtils.isNotEmpty(handlersConf)) {
            for (String hand : Arrays.asList(handlersConf.split(","))) {
                NebulaLogger.info(LOG_TAG, "Loading handler " + hand);
                try {
                    Class clz = Class.forName(WebAppConfig.HANDLER_PATH + "." + hand);
                    HANDLERS.add(clz);
                } catch (ClassNotFoundException ex) {
                    NebulaLogger.exception(LOG_TAG, ex);
                }
            }
        }

    }
}
