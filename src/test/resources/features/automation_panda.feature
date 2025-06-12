@Panda_Page
Feature: Automation Panda Page
  Scenario: Verify all existed pages
    Given we navigate to panda_url url
    Then we click on page panda_page locator home_tab
    And we capture screenshot on page panda_page locator body name "home_tab"
    And we click on page panda_page locator about_ta
    And we capture screenshot on page panda_page locator body name "about_tab"
    When we click on page panda_page locator contact_tab
    And we capture screenshot on page panda_page locator body name "contact_tab"
    Then we click on page panda_page locator speaking_tab
    And we capture screenshot on page panda_page locator body name "speaking_tab"
    Then we click on page panda_page locator teaching_tab
    And we capture screenshot on page panda_page locator body name "teaching_tab"
    Then we click on page panda_page locator bdd_tab
    And we capture screenshot on page panda_page locator body name "bdd_tab"
    Then we click on page panda_page locator development_tab
    And we capture screenshot on page panda_page locator body name "development_tab"
    Then we click on page panda_page locator testing_tab
    And we capture screenshot on page panda_page locator body name "testing_tab"
    Then we click on page panda_page locator python_tab
    And we capture screenshot on page panda_page locator body name "python_tab"
