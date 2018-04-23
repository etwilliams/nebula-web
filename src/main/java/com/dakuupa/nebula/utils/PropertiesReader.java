package com.dakuupa.nebula.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Eric
 */
public class PropertiesReader {

    public static String getProperty(String propName) {

        Properties prop = new Properties();
        InputStream input = null;
        String value = null;

        try {
            input = new FileInputStream("/usr/local/satfi/config/nebula.properties");
            prop.load(input);
            value = prop.getProperty(propName);
            NebulaLogger.info(WebAppConfig.LOG_TAG, propName + "=" + value);
        } catch (IOException ex) {
            NebulaLogger.exception(PropertiesReader.class.getSimpleName(), ex);

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    NebulaLogger.exception(PropertiesReader.class.getSimpleName(), ex);
                }
            }
        }
        return value;

    }
}
