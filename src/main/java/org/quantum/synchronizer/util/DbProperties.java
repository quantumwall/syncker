package org.quantum.synchronizer.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DbProperties {

    private static final String PROPERTIES_ARG_FILENAME = "app.properties";
    private static final Properties properties = new Properties();
    private static final Logger log = LoggerFactory.getLogger(DbProperties.class);

    static {
        loadProperties();
    }

    private DbProperties() {
    }

    private static void loadProperties() {
        if (System.getProperty("app.properties") != null) {
            var filename = System.getProperty(PROPERTIES_ARG_FILENAME);
            try (var resource = new FileInputStream(filename)) {
                properties.load(resource);
            } catch (FileNotFoundException e) {
                log.error("file {} not found", filename);
                throw new RuntimeException(e);
            } catch (IOException e) {
                log.error("io exception during open resource file");
                throw new RuntimeException(e);
            }
        } else {
            try (var resourse = DbProperties.class.getClassLoader().getResourceAsStream("application.properties")) {
                properties.load(resourse);
            } catch (IOException e) {
                log.error("resource application.properties is not found");
                throw new IllegalStateException(e);
            }
        }

    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
