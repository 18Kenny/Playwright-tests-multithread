package oneShopUI.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import oneShopUI.browsers.PropertiesReader;
import org.slf4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LocatorSearcher extends PropertiesReader {
    private static final Logger logger = LogManager.getLogger(LocatorSearcher.class);
    public static String extractText(String input) {
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static List<String> filterListByPrefix(List<String> inputList, String prefix) {
        return inputList.stream()
                .filter(item -> {
                    String firstPart = item.split("\\.")[0];
                    return firstPart.equals(prefix);
                })
                .collect(Collectors.toList());
    }

    public static Locator locatorPrintKeysByText(Page page, String textFromLocator) {
        List<String> listObjects = filterListByPrefix(findKeyByValue(textFromLocator), getCurrentEnvironment());
        listObjects.forEach(logger::warn);
        Locator locator = page.locator("//*[contains(text(),'"+getNameForKeyFromTranslationMaps(listObjects.get(0))+"')]");
        if (locator.count()>1){
            return locator.nth(1);
        }else{
            return locator;
        }

    }

    public static Locator locatorCustom(Page page, String selector) {
        String modified = getCurrentEnvironment()+"."+ selector;
        Locator locator = page.locator("//*[contains(text(),'"+getNameForKeyFromTranslationMaps(modified)+"')]");
        if (locator.count()>1){
            return locator.nth(1);
        }else{
            return locator;
        }
    }
}