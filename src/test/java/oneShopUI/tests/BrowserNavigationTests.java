package oneShopUI.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import oneShopUI.pages.HomePage;
import org.testng.annotations.Test;


public class BrowserNavigationTests extends BaseTest {

    @Test
    public void testBrowserNavigation() {
        final Page page = this.browserManager.getPage();
        page.navigate(properties.getEnvProperty("url"), new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        final HomePage homePage = new HomePage(page);
        homePage.acceptCookies();
        homePage.openLoginMenu()
                .clickIndividualClientLink()
                .Login(
                        properties.getEnvProperty("username"),
                        properties.getEnvProperty("password"))
                .clickCloseIcon();
    }
}