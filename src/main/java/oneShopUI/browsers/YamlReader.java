package oneShopUI.browsers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class YamlReader {

    public static String yamlFilePath;

    public YamlReader(String path) {
        yamlFilePath = path;
    }

    public static String getYamlValue(String token) {
        return getValue(token);
    }

    private static String getValue(String token) {
        Reader doc;
        try {
            doc = new FileReader(yamlFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Yaml yaml = new Yaml();
        Map<String, Object> object = (Map<String, Object>) yaml.load(doc);
        try {
            return getMapValue(object, token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getMapValue(Map<String, Object> object, String token) {
        String[] st = token.split("\\.");
        try {
            return parseMap(object, token).get(st[st.length - 1]).toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static Map<String, Object> parseMap(Map<String, Object> object, String token) {
        if (token.contains(".")) {
            String[] st = token.split("\\.");
            object = parseMap((Map<String, Object>) object.get(st[0]), token.replace(st[0] + ".", ""));
        }
        return object;
    }
}