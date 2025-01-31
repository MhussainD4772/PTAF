package com.ptaf.stepdefinitions;

import com.microsoft.playwright.Page;
import com.ptaf.hooks.Hooks;
import com.ptaf.pages.FrameCommonMethods;
import com.ptaf.pages.PageCommonMethods;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class NewPageCommonSteps {
    private final Page page = Hooks.getPage();
    private final Page new_page = page.waitForPopup(() -> page.getByLabel("Please complete the previous").click());
    private final PageCommonMethods pageCommonMethods = new PageCommonMethods(new_page);
    private final FrameCommonMethods frameCommonMethods = new FrameCommonMethods(new_page);
    private static final String plad_frame = "iframe[title=\"Plaid Link\"]";
    private static final String pop_frame = "//*[@id=\"AcceptUIContainer\"]/iframe";
    private final String iFrame = "iframe[name='iframeApplicationContent']";
    private final String iFrame_2 = "";
    private final String iFrame_3 = "";

    @Then("^we click on new page (.*?) locator (.*?)$")
    public void weClickActionNewOnPage(String element, String locator) {
        pageCommonMethods.click(new_page, element, locator);
    }

    @Then("^we double click on new page (.*?) locator (.*?)$")
    public void weDoubleClickActionOnPage(String element, String locator) {
        pageCommonMethods.dblclick(new_page, element, locator);
    }

    @Then("^we enter value on new page (.*?) locator (.*?) value \"(.*?)\"$")
    public void weEnterValueOnPage(String element, String locator, String value) {
        pageCommonMethods.fill(new_page, element, locator, value);
    }

    @Then("^we select on new page (.*?) locator (.*?) value \"(.*?)\"$")
    public void weSelectValueOnPage(String element, String locator, String value) {
        pageCommonMethods.select(new_page, element, locator, value);
    }

    @Then("^we check on new page (.*?) locator (.*?)$")
    public void weCheckActionOnPage(String element, String locator) {
        pageCommonMethods.check(new_page, element, locator);
    }

    @Then("^we uncheck on new page (.*?) locator (.*?)$")
    public void weUncheckActionOnPage(String element, String locator) {
        pageCommonMethods.check(new_page, element, locator);
    }

    @Then("^we hover on new page (.*?) locator (.*?)$")
    public void weHoverActionOnPage(String element, String locator) {
        pageCommonMethods.hover(new_page, element, locator);
    }

    @Then("^we type on new page (.*?) locator (.*?) value \"(.*?)\"$")
    public void weTypeValueOnPage(String element, String locator, String value) {
        pageCommonMethods.type(new_page, element, locator, value);
    }

    @Then("^we scroll on new page (.*?) locator (.*?)$")
    public void weScrollToLocatorOnPage(String element, String locator) {
        pageCommonMethods.scroll(new_page, element, locator);
    }

    @Then("^we clear value on new page (.*?) locator (.*?) value \"(.*?)\"$")
    public void weClearValueOnPage(String element, String locator) {
        pageCommonMethods.clear(new_page, element, locator);
    }

    @Then("^we verify on new page (.*?) of locator (.*?) is visible$")
    public void weVerifyOnPageLocatorIsVisible(String element, String locator) {
        pageCommonMethods.isvisible(new_page, element, locator);
    }

    @Then("^we verify on new page (.*?) of locator (.*?) is checked$")
    public void weVerifyOnPageLocatorIsChecked(String element, String locator) {
        pageCommonMethods.ischecked(new_page, element, locator);
    }

    @Then("^we verify on new page (.*?) of locator (.*?) is enabled")
    public void weVerifyOnPageLocatorIsEnabled(String element, String locator) {
        pageCommonMethods.isenabled(new_page, element, locator);
    }

    @Then("^we get value on new page (.*?) locator (.*?)$")
    public void weGetValueOnPage(String element, String locator) {
        pageCommonMethods.getvalue(new_page, element, locator);
    }

    @Then("^we verify element has value on new page (.*?) of locator (.*?) value \"(.*?)\"$")
    public void weHasValueOnPageLocatorValue(String element, String locator, String value) {
        pageCommonMethods.hasvalue(new_page, element, locator, value);
    }

    @Then("^we verify on new page (.*?) of locator (.*?) is existed")
    public void weVerifyOnPageLocatorIsExisted(String element, String locator) {
        pageCommonMethods.exists(new_page, element, locator);
    }

    @Then("^we contain on new page (.*?) of locator (.*?) value \"(.*?)\"$")
    public void weContainOnPageLocatorValue(String element, String locator, String value) {
        pageCommonMethods.contain(new_page, element, locator, value);
    }

    @Then("^we get text on new page(.*?) locator (.*?)$")
    public void weGetTextOnPage(String element, String locator) {
        pageCommonMethods.gettext(new_page, element, locator);
    }

    @And("^we capture screenshot on new page (.*?) locator (.*?) name \"(.*?)\"$")
    public void weCaptureScreenshotOnPage(String element, String locator, String name) {
        String filePath = "test-output/screenshots/" + name + ".png";
        pageCommonMethods.screenshot(new_page, element, locator, filePath);
    }

    @And("^we press on new page (.*?) locator (.*?) key \"(.*?)\" keyboard$")
    public void wePressOnPageKey(String element, String locator, String value) {
        pageCommonMethods.press(new_page, element, locator, value);
    }

    @When("we click radio on new page (.*?) list locator (.*?)$")
    public void clickRadioOnPage(String element, String locator) {
        pageCommonMethods.clickRadioButton(new_page, element, locator);
    }

    @Then("^we click on plad frame (.*?) locator (.*?)$")
    public void weClickActionOnPladFrame(String element, String locator) {
        frameCommonMethods.click(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we double click on plad frame (.*?) locator (.*?)$")
    public void weDoubleClickActionOnPladFrame(String element, String locator) {
        frameCommonMethods.dblclick(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we enter value on plad frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weEnterValueOnPladFrame(String element, String locator, String value) {
        frameCommonMethods.fill(new_page, iFrame, iFrame_2, iFrame_3, element, locator, value);
    }

    @Then("^we select on plad frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weSelectValueOnPladFrame(String element, String locator, String value) {
        frameCommonMethods.select(new_page, iFrame, iFrame_2, iFrame_3, element, locator, value);
    }

    @Then("^we check on plad frame (.*?) locator (.*?)$")
    public void weCheckActionOnPladFrame(String element, String locator) {
        frameCommonMethods.check(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we uncheck on plad frame (.*?) locator (.*?)$")
    public void weUncheckActionOnPladFrame(String element, String locator) {
        frameCommonMethods.check(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we hover on plad frame (.*?) locator (.*?)$")
    public void weHoverActionOnPladFrame(String element, String locator) {
        frameCommonMethods.hover(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we type on plad frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weTypeValueOnPladFrame(String element, String locator, String value) {
        frameCommonMethods.type(new_page, iFrame, iFrame_2, iFrame_3, element, locator, value);
    }

    @Then("^we scroll on plad frame (.*?) locator (.*?)$")
    public void weScrollToLocatorOnPladFrame(String element, String locator) {
        frameCommonMethods.scroll(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we clear value on plad frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weClearValueOnPladFrame(String element, String locator) {
        frameCommonMethods.clear(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we verify on plad frame (.*?) of locator (.*?) is visible$")
    public void weVerifyOnPladFrameLocatorIsVisible(String element, String locator) {
        frameCommonMethods.isvisible(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we verify on plad frame (.*?) of locator (.*?) is checked$")
    public void weVerifyOnPladFrameLocatorIsChecked(String element, String locator) {
        frameCommonMethods.ischecked(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we verify on plad frame (.*?) of locator (.*?) is enabled")
    public void weVerifyOnPladFrameLocatorIsEnabled(String element, String locator) {
        frameCommonMethods.isenabled(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we get value on plad frame (.*?) locator (.*?)$")
    public void weGetValueOnPladFrame(String element, String locator) {
        frameCommonMethods.getvalue(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we verify element has value on plad frame (.*?) of locator (.*?) value \"(.*?)\"$")
    public void weHasValueOnPladFrameLocatorValue(String element, String locator, String value) {
        frameCommonMethods.hasvalue(new_page, iFrame, iFrame_2, iFrame_3, element, locator, value);
    }

    @Then("^we verify on plad frame (.*?) of locator (.*?) is existed")
    public void weVerifyOnPladFrameLocatorIsExisted(String element, String locator) {
        frameCommonMethods.exists(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we contain on plad frame (.*?) of locator (.*?) value \"(.*?)\"$")
    public void weContainOnPladFrameLocatorValue(String element, String locator, String value) {
        frameCommonMethods.contain(new_page, iFrame, iFrame_2, iFrame_3, element, locator, value);
    }

    @Then("^we get text on plad frame(.*?) locator (.*?)$")
    public void weGetTextOnPladFrame(String element, String locator) {
        frameCommonMethods.gettext(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @And("^we capture screenshot on plad frame (.*?) locator (.*?) name \"(.*?)\"$")
    public void weCaptureScreenshotOnPladFrame(String element, String locator, String name) {
        String filePath = "test-output/screenshots/" + name + ".png";
        frameCommonMethods.screenshot(new_page, iFrame, iFrame_2, iFrame_3, element, locator, filePath);
    }

    @And("^we press on plad frame (.*?) locator (.*?) key \"(.*?)\" keyboard$")
    public void wePressOnPladFrameKey(String element, String locator, String value) {
        frameCommonMethods.press(new_page, iFrame, iFrame_2, iFrame_3, element, locator, value);
    }

    //___________________________________________________________

    @Then("^we click on pop frame (.*?) locator (.*?)$")
    public void weClickActionOnPopFrame(String element, String locator) {
        frameCommonMethods.click(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we double click on pop frame (.*?) locator (.*?)$")
    public void weDoubleClickActionOnPopFrame(String element, String locator) {
        frameCommonMethods.dblclick(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we enter value on pop frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weEnterValueOnPopFrame(String element, String locator, String value) {
        frameCommonMethods.fill(new_page, pop_frame, null, null, element, locator, value);
    }

    @Then("^we select on pop frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weSelectValueOnPopFrame(String element, String locator, String value) {
        frameCommonMethods.select(new_page, pop_frame, null, null, element, locator, value);
    }

    @Then("^we check on pop frame (.*?) locator (.*?)$")
    public void weCheckActionOnPopFrame(String element, String locator) {
        frameCommonMethods.check(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we uncheck on pop frame (.*?) locator (.*?)$")
    public void weUncheckActionOnPopFrame(String element, String locator) {
        frameCommonMethods.check(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we hover on pop frame (.*?) locator (.*?)$")
    public void weHoverActionOnPopFrame(String element, String locator) {
        frameCommonMethods.hover(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we type on pop frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weTypeValueOnPopFrame(String element, String locator, String value) {
        frameCommonMethods.type(new_page, pop_frame, null, null, element, locator, value);
    }

    @Then("^we scroll on pop frame (.*?) locator (.*?)$")
    public void weScrollToLocatorOnPopFrame(String element, String locator) {
        frameCommonMethods.scroll(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we clear value on pop frame (.*?) locator (.*?) value \"(.*?)\"$")
    public void weClearValueOnPopFrame(String element, String locator) {
        frameCommonMethods.clear(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we verify on pop frame (.*?) of locator (.*?) is visible$")
    public void weVerifyOnPopFrameLocatorIsVisible(String element, String locator) {
        frameCommonMethods.isvisible(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we verify on pop frame (.*?) of locator (.*?) is checked$")
    public void weVerifyOnPopFrameLocatorIsChecked(String element, String locator) {
        frameCommonMethods.ischecked(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we verify on pop frame (.*?) of locator (.*?) is enabled")
    public void weVerifyOnPopFrameLocatorIsEnabled(String element, String locator) {
        frameCommonMethods.isenabled(new_page, pop_frame, null, null, element, locator);
    }

    @Then("^we get value on pop frame (.*?) locator (.*?)$")
    public void weGetValueOnPopFrame(String element, String locator) {
        frameCommonMethods.getvalue(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we verify element has value on pop frame (.*?) of locator (.*?) value \"(.*?)\"$")
    public void weHasValueOnPopFrameLocatorValue(String element, String locator, String value) {
        frameCommonMethods.hasvalue(new_page, iFrame, iFrame_2, iFrame_3, element, locator, value);
    }

    @Then("^we verify on pop frame (.*?) of locator (.*?) is existed")
    public void weVerifyOnPopFrameLocatorIsExisted(String element, String locator) {
        frameCommonMethods.exists(new_page, iFrame, iFrame_2, iFrame_3, element, locator);
    }

    @Then("^we contain on pop frame (.*?) of locator (.*?) value \"(.*?)\"$")
    public void weContainOnPopFrameLocatorValue(String element, String locator, String value) {
        frameCommonMethods.contain(new_page, iFrame, iFrame_2, iFrame_3, element, locator, value);
    }

    @Then("^we get text on pop frame(.*?) locator (.*?)$")
    public void weGetTextOnPopFrame(String element, String locator) {
        frameCommonMethods.gettext(new_page, pop_frame, null, null, element, locator);
    }

    @And("^we capture screenshot on pop frame (.*?) locator (.*?) name \"(.*?)\"$")
    public void weCaptureScreenshotOnPopFrame(String element, String locator, String name) {
        String filePath = "test-output/screenshots/" + name + ".png";
        frameCommonMethods.screenshot(new_page, pop_frame, null, null, element, locator, filePath);
    }

    @And("^we press on pop frame (.*?) locator (.*?) key \"(.*?)\" keyboard$")
    public void wePressOnPopFrameKey(String element, String locator, String value) {
        frameCommonMethods.press(new_page, pop_frame, null, null, element, locator, value);
    }

}