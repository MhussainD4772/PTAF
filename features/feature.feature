@regression @login_page
Feature: Login Page Feature

  Background: Navigate to URL
    Given we navigate to qa_url url
    Then get title of page

  Scenario: Successful login
    Given we enter value on page login locator username value "test_mj"
    And we contain on page login of locator username value ""
    And we verify on page login of locator password is visible
    When we enter value on page login locator password value "Password123$"
    Then we get text on page login locator loginButton
    And we click on page login locator loginButton
    Then we enter value on page login locator otp_field value "123456"
    When we capture screenshot on page login locator body name "testing/screenshot"
    And we click on page login locator otp_continue_btn

  Scenario: Successful login 2
    Given we enter value on page login locator username value "test_mj"
    And we contain on page login of locator login_text value "Login "
    When we enter value on page login locator password value "Password123$"
    Then we get text on page login locator loginButton
    And we click on page login locator loginButton
#    Then we enter value on page login locator otp_field value "123456"
    When we capture screenshot on page login locator body name "testing/screenshot"
    And we click on page login locator otp_continue_btn
