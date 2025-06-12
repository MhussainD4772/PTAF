@google
Feature: Google Validation

  Background: Navigate to Google
    Given we navigate to google_url url
    Then get title of page

  Scenario: Search for wooden spoon
    Given we enter value on page google_page locator search_flt value "wooden spoon"
    Then we get text of elements on page google_page locator footer_text
    When we press on page google_page locator body key "Enter" keyboard
    Then we capture screenshot on page google_page locator body name "google/body screenshot"
    Then we wait for some time