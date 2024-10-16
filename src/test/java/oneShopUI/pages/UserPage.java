package oneShopUI.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class UserPage {
    private final Page page;

    // Locators
    private final Locator userName;
    private final Locator userEmail;
    private final Locator addNewPlanButton;
    private final Locator welcomeMessage;
    private final Locator viewOffersParagraph;
    private final Locator myTMobileLink;
    private final Locator signOutButton;
    private Locator closeIcon;

    // Constructor
    public UserPage(Page page) {
        this.page = page;
        this.userName = page.locator("div[data-qa='CTX_UserName']");
        this.userEmail = page.locator("div[data-qa='CTX_Email']");
        this.addNewPlanButton = page.locator("div[data-qa='CTX_AddNewPlan']");
        this.welcomeMessage = page.locator("h2.dt_title");
        this.viewOffersParagraph = page.locator("div.descText");
        this.myTMobileLink = page.locator("div[data-qa='CTX_TelekomApp'] a.dt_anchor");
        this.signOutButton = page.locator("div[data-qa='CTX_SignOut'] a.dt_anchor");
        this.closeIcon = page.locator("div[data-qa='closeIcon']"); // Close icon
    }

    // Methods to interact with elements

    public String getUserName() {
        return this.userName.textContent().trim();
    }

    public String getUserEmail() {
        return this.userEmail.textContent().trim();
    }

    public void clickAddNewPlan() {
        this.addNewPlanButton.click();
    }

    public String getWelcomeMessage() {
        return this.welcomeMessage.textContent().trim();
    }

    public String getViewOffersText() {
        return this.viewOffersParagraph.textContent().trim();
    }

    public void clickMyTMobile() {
        this.myTMobileLink.click();
    }

    public void clickSignOut() {
        this.signOutButton.click();
    }

    // Method to click on the close icon
    public void clickCloseIcon() {
        closeIcon.click();
    }

    // Method to wait for page elements to load
    public void waitForPageToLoad() {
        this.userName.waitFor();
        this.userEmail.waitFor();
        this.addNewPlanButton.waitFor();
        this.welcomeMessage.waitFor();
        this.viewOffersParagraph.waitFor();
        this.myTMobileLink.waitFor();
        this.signOutButton.waitFor();
    }
}
