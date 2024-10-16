package oneShopUI.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class HomePage {
    private final Page page;

    private final Locator registerButton;
    private final Locator mobileTariffsMenu;
    private final Locator internetMenu;
    private final Locator supportMenu;
    private final Locator searchInput;
    private final Locator searchButton;
    private final Locator acceptCookiesButton;
    private final Locator plansMenu;
    private final Locator offersMenu;
    private final Locator myAccountButton;

    public HomePage(final Page page) {
        this.page = page;
        this.myAccountButton = page.locator("i[class*='iconWithoutConfirm']");;
        this.registerButton = page.locator("a[data-testid='register-link']");
        this.mobileTariffsMenu = page.locator("a[href*='oferta/abonament']");
        this.internetMenu = page.locator("a[href*='oferta/internet']");
        this.supportMenu = page.locator("a[href*='pomoc']");
        this.searchInput = page.locator("input[placeholder='Szukaj']");
        this.searchButton = page.locator("button[data-testid='search-submit-button']");
        this.plansMenu = page.locator("a[data-testid='plans-menu']");
        this.offersMenu = page.locator("a[data-testid='offers-menu']");
        this.acceptCookiesButton = page.locator(
                "#didomi-notice-agree-button, " +
                        "button.accept-all, " +
                        "#allow-cookies-button, " +
                        "button.acceptcookies, " +
                        "button[id*='accept-all'], " +
                        ".con-ban-btn:first-of-type, " +
                        "#cookies-notification-accept-cookie, " +
                        "#acceptAllQuick, " +
                        ".con-ban-btn.btn-left,"+
                        ".button[id='all-cookies-btn']," +
                        "[id*='AcceptAll'], " +
                        "[id*='all-'], " +
                        "[id*='notice-agree'], " +
                        "#acceptAllQuick span, " +
                        "[id*='consentAcceptAll'], " +
                        "#acceptAllQuick"
                );
    }

    public LoginPage openLoginMenu() {
        myAccountButton.click();
        return new LoginPage(page);
    }

    public void clickRegisterButton() {
        registerButton.click();
    }

    public void navigateToMobileTariffs() {
        mobileTariffsMenu.click();
    }

    public void navigateToInternet() {
        internetMenu.click();
    }

    public void navigateToSupport() {
        supportMenu.click();
    }

    public SearchResultPage search(String query) {
        searchInput.fill(query);
        searchButton.click();
        return new SearchResultPage(page);}

    public void acceptCookies() {
        acceptCookiesButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        acceptCookiesButton.click();
    }

    public void navigateToPlansMenu() {
        plansMenu.click();
    }

    public void navigateToOffersMenu() {
        offersMenu.click();
    }

    public void waitForPageToLoad() {
        this.acceptCookiesButton.waitFor();
    }
}
