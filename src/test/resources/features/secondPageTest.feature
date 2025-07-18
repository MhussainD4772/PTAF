@secondPageTest
Feature: Second Page Test

  Background: Navigate to URL
    Given we navigate to tool_qa_url url
    Then get title of page

  Scenario: Opening New page Same Browser
    Given we click on page homePage locator alert_frame_and_window
    Then we click on page homePage locator browser_window
#    When we click on page homePage locator new_tab_btn
#    And we get text on new page homePage locator tab_semple_heading
    Then we click on page homePage locator frame_btn
    And we get text on frame homePage locator frame_semple_heading

    Scenario: Test Download Document Method
      Given we click on page homePage locator elements_tab
      When we click on page homePage locator update_and_download_tab
      Then we click download on page homePage locator download_btn
      And we wait for some time
