package oneShopUI.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import oneShopUI.utils.LocatorSearcher;

public class LoginPage {
    private final Page page;
    private final Locator emailInput;
    private final Locator passwordInput;
    private Locator nextButton;
    private Locator loginButton;
    private final Locator individualClientLink;
    private final Locator businessClientLink;
    private final Locator iconLoginSuccess;

    public LoginPage(Page page) {
        this.page = page;
        this.emailInput = page.locator("#text");
        this.passwordInput = page.locator("input[type='password']");
        this.individualClientLink = LocatorSearcher.locatorCustom(page, "cart.global.prolongation.preLoginOptions.existingTmobileCustomer");
        this.businessClientLink = page.locator("//a[contains(@data-ga-ea, 'my-account-link')]").nth(1);
        this.iconLoginSuccess = page.locator("div.iconWithConfirm i.confirmIcon");
    }

    public void enterEmail(String email) {
        emailInput.fill(email);
        this.nextButton = LocatorSearcher.locatorCustom(page, "cart.login.common.next");
        nextButton.click();
    }

    public void enterPassword(String password) {
        page.waitForLoadState(LoadState.LOAD);
        passwordInput.fill(password);
        this.loginButton = LocatorSearcher.locatorCustom(page, "cart.login.loginEnterPassword.validatePassword");
        loginButton.click();
        iconLoginSuccess.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    public UserPage Login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        return new UserPage(page);
    }

    public LoginPage clickIndividualClientLink() {
        individualClientLink.click();
        isAccountMenuLoaded();
        return new LoginPage(page);
    }

    public LoginPage clickBusinessClientLink() {
        businessClientLink.click();
        isAccountMenuLoaded();
        return new LoginPage(page);
    }

    public boolean isAccountMenuLoaded() {
        return individualClientLink.isVisible() && businessClientLink.isVisible();
    }
}
