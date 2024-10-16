package oneShopUI.browsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BrowserManager {
    private final Playwright playwright;
    private Browser browser;
    public BrowserContext context;
    @Getter
    private Page page;
    private static final String HEADLESS = "headless";

    public BrowserManager() {
        this.playwright = Playwright.create();
    }

    private static Map<String, JsonNode> devices;

    static {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(new File("src/main/resources/deviceConfig.json"));
            devices = new HashMap<>();
            JsonNode devicesNode = rootNode.get("devices");
            devicesNode.fieldNames().forEachRemaining(deviceName -> {
                devices.put(deviceName, devicesNode.get(deviceName));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BrowserContext emulateDevice(Browser browser, String deviceName) {
        JsonNode deviceConfig = devices.get(deviceName);
        if (deviceConfig == null) {
            throw new IllegalArgumentException("Device configuration not found for: " + deviceName);
        }

        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setViewportSize(deviceConfig.get("viewport").get("width").asInt(), deviceConfig.get("viewport").get("height").asInt())
                .setUserAgent(deviceConfig.get("userAgent").asText())
                .setScreenSize(deviceConfig.get("viewport").get("width").asInt(), deviceConfig.get("viewport").get("height").asInt())
                .setDeviceScaleFactor(deviceConfig.get("deviceScaleFactor").asDouble())
                .setIsMobile(deviceConfig.get("isMobile").asBoolean())
                .setHasTouch(deviceConfig.get("hasTouch").asBoolean());

        return browser.newContext(options);
    }


    public void createBrowser() {
        setupChromeBrowser();
    }

    private void setupChromeBrowser() {
        this.browser = this.playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty(HEADLESS))));
        this.context = emulateDevice(browser, "iPhone11");
        this.page = context.newPage();
    }

    public Page getPage() {
        if (this.page == null) {
            throw new IllegalStateException("Browser is not initialized. Call createBrowser() first.");
        }
        this.page.setDefaultTimeout(30000);
        return this.page;
    }

    public BrowserContext getContext() {
        if (this.context == null) {
            throw new IllegalStateException("Browser context is not initialized. Call createBrowser() first.");
        }
        return this.context;
    }

    private void setupFirefoxBrowser() {
        this.browser = this.playwright.firefox().launch(new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty(HEADLESS))));
        this.context = browser.newContext();
        this.page = context.newPage();
    }

    private void setupEdgeBrowser() {
        this.browser = this.playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty(HEADLESS)))
                .setChannel("msedge"));
        this.context = browser.newContext();
        this.page = context.newPage();
    }

    public void closeBrowser() {
        if (this.page != null) {
            this.page.close();
        }
        if (this.context != null) {
            this.context.close();
        }
        if (this.browser != null) {
            this.browser.close();
        }
        if (this.playwright != null) {
            this.playwright.close();
        }
    }
}
