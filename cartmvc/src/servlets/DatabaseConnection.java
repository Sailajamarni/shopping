package servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnection {

    private static final String PROPERTY_FILE_NAME = "db.properties";
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME)) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new IOException("Unable to find db.properties");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }

    public static String getDriver() {
        return properties.getProperty("db.driver");
    }
}
