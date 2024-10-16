package oneShopUI.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AllureConfigLoader {

    private static final String CONFIG_FILE = "allure.properties";
    private static Properties properties = new Properties();

    static {
        try (InputStream input = AllureConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + CONFIG_FILE, e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static int getThreadCount() {
        String threadCountStr = properties.getProperty("allure.threads");
        try {
            return Integer.parseInt(threadCountStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid thread count specified in allure.properties: " + threadCountStr);
        }
    }

    public static String getResultsDirectory() {
        return properties.getProperty("allure.results.directory", "target/allure-results");
    }
}

