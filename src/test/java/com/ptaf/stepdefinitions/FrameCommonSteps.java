package com.ptaf.stepdefinitions;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.ptaf.hooks.Hooks;
import com.ptaf.pages.FrameCommonMethods;
import com.ptaf.utils.ConfigurationProperties;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FrameCommonSteps {
    private final Page page;
    private static Page iframePage;
    private final String iFrame = "#frame1";
    private final String iFrame_2 = null;
    private final String iFrame_3 = null;
    private final FrameCommonMethods frameCommonMethods;

    public FrameCommonSteps() {
        this.page = Hooks.getPage(); // Retrieve the Page instance from Hooks
        this.frameCommonMethods = new FrameCommonMethods(iframePage); // Initialize with the Page instance
    }

    public void switchToIframe() {
        iframePage = page.waitForPopup(() -> {
            page.frameLocator("iframe").getByRole(AriaRole.BUTTON, new FrameLocator.GetByRoleOptions().setName("Continue")).click();
        });
    }

    @Given("^we navigate to (.*?) url$")
    public void weNavigateToURL(String URL) {
        page.navigate(ConfigurationProperties.getBaseUrl(URL));
//        page.setViewportSize(1920, 1080);
//        switchToIframe();
    }

    @Then("^we click on frame (.*?) locator (.*?)$")
    public void weClickActionOnPage(String element, String locator) {
        frameCommonMethods.click(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we double click on frame (.*?) locator (.*?)$")
    public void weDoubleClickActionOnPage(String element, String locator) {
        frameCommonMethods.dblclick(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we enter value on frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weEnterValueOnPage(String element, String locator, String value) {
        frameCommonMethods.fill(iframePage, iFrame, null, null,element, locator, value);
    }

    @Then("^we select on frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weSelectValueOnPage(String element, String locator, String value) {
        frameCommonMethods.select(iframePage, iFrame, null, null, element, locator, value);
    }

    @Then("^we check on frame (.*?) locator (.*?)$")
    public void weCheckActionOnPage(String element, String locator) {
        frameCommonMethods.check(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we uncheck on frame (.*?) locator (.*?)$")
    public void weUncheckActionOnPage(String element, String locator) {
        frameCommonMethods.check(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we hover on frame (.*?) locator (.*?)$")
    public void weHoverActionOnPage(String element, String locator) {
        frameCommonMethods.hover(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we type on frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weTypeValueOnPage(String element, String locator, String value) {
        frameCommonMethods.type(iframePage, iFrame, null, null, element, locator, value);
    }

    @Then("^we scroll on frame (.*?) locator (.*?)$")
    public void weScrollToLocatorOnPage(String element, String locator) {
        frameCommonMethods.scroll(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we clear value on frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weClearValueOnPage(String element, String locator) {
        frameCommonMethods.clear(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we verify on frame (.*?) of locator (.*?) is visible$")
    public void weVerifyOnPageLocatorIsVisible(String element, String locator) {
        frameCommonMethods.isvisible(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we verify on frame (.*?) of locator (.*?) is checked$")
    public void weVerifyOnPageLocatorIsChecked(String element, String locator) {
        frameCommonMethods.ischecked(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we verify on frame (.*?) of locator (.*?) is enabled")
    public void weVerifyOnPageLocatorIsEnabled(String element, String locator) {
        frameCommonMethods.isenabled(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we verify on frame (.*?) of locator (.*?) is existed")
    public void weVerifyOnPageLocatorIsExisted(String element, String locator) {
        frameCommonMethods.exists(iframePage, iFrame, null, null, element, locator);
    }

    @Then("^we contain on frame (.*?) of locator (.*?) value \"(.*?)\"$")
    public void weContainOnPageLocatorValue(String element, String locator, String value) {
        frameCommonMethods.contain(iframePage, iFrame, null, null, element, locator, value);
    }

    @Then("^we get text on frame (.*?) locator (.*?)$")
    public void weGetTextOnPage(String element, String locator) {
        frameCommonMethods.gettext(page, iFrame, null, null, element, locator);
    }

    @Then("^we has value on frame (.*?) of locator (.*?) value \"(.*?)\"$")
    public void weHasValueOnNewPageLocatorValue(String element, String locator, String value) {
        frameCommonMethods.hasvalue(iframePage, iFrame, null, null, element, locator, value);
    }

    @Then("^we get list of elements on frame (.*?) locator (.*?)$")
    public void weGetListOfElementsOnNewPage(String element, String locator) {
        String value = frameCommonMethods.gettext(iframePage, iFrame, null, null, element, locator);
        System.out.println(value);
    }

    @When("we click radio on frame (.*?) list locator (.*?)$")
    public void clickRadioOnNewPage(String element, String locator) {
        frameCommonMethods.clickRadioButton(iframePage, iFrame, element, locator);
    }

    @And("^we capture screenshot on frame (.*?) locator (.*?) name \"(.*?)\"$")
    public void weCaptureScreenshotOnPage(String element, String locator, String name) {
        String filePath = "test-output/screenshots/" + name + ".png";
        frameCommonMethods.screenshot(iframePage, iFrame, null, null, element, locator, filePath);
    }

    @And("^we press on frame (.*?) locator (.*?) key \"(.*?)\" keyboard$")
    public void wePressOnPageKey(String element, String locator, String value) {
        frameCommonMethods.press(iframePage, iFrame, null, null, element, locator, value);
    }
}