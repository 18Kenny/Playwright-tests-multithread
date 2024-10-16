package oneShopUI.tests;

import com.microsoft.playwright.Page;
import io.qameta.allure.Attachment;
import oneShopUI.listeners.AllureConfigLoader;
import oneShopUI.browsers.BrowserManager;
import oneShopUI.browsers.PropertiesReader;
import org.testng.annotations.*;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Parameters({"browser", "deviceName"})
public class BaseTest extends PropertiesReader {
    BrowserManager browserManager;
    PropertiesReader properties = new PropertiesReader();
    public ExecutorService executor;

    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        int threadCount = AllureConfigLoader.getThreadCount();
        executor = Executors.newFixedThreadPool(threadCount);
        clearAllureResults();
        this.browserManager = new BrowserManager();
        this.browserManager.createBrowser();
    }


    @AfterClass(alwaysRun = true)
    public void tearDown() {
        executor.shutdown();
        if (browserManager != null) {
            browserManager.closeBrowser();
        }
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] attachScreenshot() {
        Page page = this.browserManager.getPage();
        return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }

    public static void clearAllureResults() {
        String allureResultsPath = "allure-results"; // Путь к папке allure-results
        File folder = new File(allureResultsPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        } else {
            System.out.println("Allure results folder not found or is not a directory.");
        }
    }
}