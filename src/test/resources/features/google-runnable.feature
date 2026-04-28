@google_search
Feature: Google Search Functionality

  Scenario: Perform a search and verify results
    Given we navigate to google_url url
    When we enter value on page google_page locator search_flt value "Playwright automation"
    And we press on page google_page locator search_flt key "Enter" keyboard
    Then we verify on page google_page of locator body is visible
