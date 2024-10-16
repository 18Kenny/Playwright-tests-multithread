package oneShopUI.browsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import oneShopUI.utils.LogManager;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class PropertiesReader extends YamlReader {
    private static final String envConfigFilePath = "src/main/resources/config.yml";
    public static final String configProperty = "config.properties";
    public static Map<String, Object> mapOfTranslationMap = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(PropertiesReader.class);
    @Getter
    public static Properties testProperties = new Properties();
    private Map<String, Object> envsProperties;
    private static final AtomicBoolean isTranslationSet = new AtomicBoolean(false);

    public PropertiesReader() {
        super(envConfigFilePath);
        Yaml yaml = new Yaml();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configProperty)) {
            if (input == null) {
                logger.debug("Sorry, unable to find " + configProperty);
                return;
            }
            this.testProperties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (FileInputStream inputStream = new FileInputStream(envConfigFilePath)) {
            this.envsProperties = yaml.load(inputStream);
        } catch (IOException e) {
            logger.debug("Sorry, unable to read config.yml");
            throw new RuntimeException(e);
        }
        setMapOfTranslation();
    }

    public static String getProperty(String key) {
        return testProperties.getProperty(key);
    }

    public String getEnvProperty(String key) {
        Map<String, Object> envData = (Map<String, Object>) envsProperties.get(getProperty("environment"));
        return envData.get(key).toString();
    }

    public static Map<String, Object> flattenJson(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    public static Object getNestedValue(Map<String, Object> map, String... keys) {
        Map<String, Object> currentMap = map;

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Object value = currentMap.get(key);

            if (value == null) {
                return null;
            }

            if (i == keys.length - 1) {
                return value;
            }

            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                return null;
            }
        }
        return null;
    }

    public static Object getNestedValue(List<String> keys) {
        Map<String, Object> currentMap = mapOfTranslationMap;

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = currentMap.get(key);

            if (value == null) {
                return null;
            }

            if (i == keys.size() - 1) {
                return value;
            }

            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                return null;
            }
        }
        return null;
    }

    public static String getNameForKeyFromTranslationMaps(String keys){
        List<String> list = Arrays.asList(keys.split("\\."));
        return String.valueOf(getNestedValue(list));
    }

    public static String getCurrentEnvironment(){
        return String.valueOf(getProperty("environment"));
    }

    public Map<String, Object> setTranslationJsonMap(String envName) {
        String cmsEnv = String.valueOf(getNestedValue(envsProperties, envName, "cmsenv"));
        String cdnPath = String.valueOf(getNestedValue(envsProperties, envName, "trnspath"));

        RestAssured.defaultParser = Parser.JSON;
        RestAssured.baseURI = cmsEnv;
        RequestSpecification request = RestAssured.given();

        Response response = request.get(cdnPath);

        if (response.getStatusCode() != 200) {
            logger.debug("Error: Received status code {} for {}", response.getStatusCode(), cdnPath);
            logger.debug("Response body: {}", response.getBody().asString());
            throw new RuntimeException("Failed to fetch translations: status code " + response.getStatusCode());
        }

        if (!ContentType.JSON.toString().equalsIgnoreCase(response.getContentType())) {
            logger.debug("Error: Expected JSON but received: {}", response.getContentType());
            logger.debug("Response body: {}", response.getBody().asString());
            throw new RuntimeException("Expected JSON but received: " + response.getContentType());
        }

        String cdn = response.asString();
        return flattenJson(cdn);
    }

    public void setMapOfTranslation() {
        if (isTranslationSet.compareAndSet(false, true)) {
            ExecutorService executor = Executors.newFixedThreadPool(10);

            List<CompletableFuture<Void>> futures = envsProperties.keySet().stream()
                    .map(key -> CompletableFuture.runAsync(() -> {
                                try {
                                    logger.debug("Fetching translations for key: {}", key);
                                    Map<String, Object> translationMap = setTranslationJsonMap(key);
                                    synchronized (mapOfTranslationMap) {
                                        mapOfTranslationMap.put(key, translationMap);
                                    }
                                } catch (Exception e) {
                                    logger.debug("Error processing key: {}", key);
                                    e.printStackTrace();
                                }
                            }, executor).orTimeout(4, TimeUnit.SECONDS)
                            .exceptionally(e -> {
                                logger.debug("Timeout or error occurred for key: {}", key);
                                return null;
                            }))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executor.shutdown();
        } else {
            logger.debug("Translations already set, skipping setMapOfTranslation.");
        }
    }

    public static List<String>  findKeyByValue(Object valueToFind) {
        List<String> foundKeys = new ArrayList<>();
        for (Map.Entry<String, Object> entry : mapOfTranslationMap.entrySet()) {
            String foundKey = searchNestedMap(entry.getKey(), entry.getValue(), valueToFind);
            if (foundKey != null) {
                logger.debug(foundKey);
                foundKeys.add(foundKey);
            }
        }
        return foundKeys;
    }

    private static String searchNestedMap(String currentKey, Object currentValue, Object valueToFind) {
        if (currentValue instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) currentValue;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String foundKey = searchNestedMap(currentKey + "." + entry.getKey(), entry.getValue(), valueToFind);
                if (foundKey != null) {
                    return foundKey;
                }
            }
        } else if (valueToFind.equals(currentValue)) {
            return currentKey;
        }
        return null;
    }
}
