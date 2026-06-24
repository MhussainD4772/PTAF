@FrameTesting
Feature: Argo Teller

  Background: Navigate to URL
    Given we navigate to Argo_Teller_qaEnvUrl url
#    Given we navigate switch to frame Argo_Teller_qaEnvUrl
    And we wait for some time

  Scenario: Argo Connects SignOn
    Then get title of page
#    When we select on frame
    When we select on frame login locator location_flt value "L1"
    And we enter value on frame login locator cashbox_flt value "77"
    Then we click on frame login locator sign_on_bt
    And we wait for some time
    And we wait for some time
    And we wait for some time
    And we wait for some time
