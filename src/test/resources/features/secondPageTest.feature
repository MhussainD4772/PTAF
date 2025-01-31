@secondPageTest
Feature: Second Page Test

  Background: Navigate to URL
    Given we navigate to eStore_QA url
    Then get title of page

  Scenario: Apply for FirstRate Savings account from the personal tab
    Given we click on page landing locator personal_tab
    When we click on page personal locator checking_and_savings
    Then we click on page personal locator browse_all_Saving
    And we click on page personal locator FirstRate_saving
    When we click on page personal locator add_to_cart
    And we click on page personal locator cart_proceed_to_check
    Then we click on page personal locator apply_now_check
    When we click on page personal locator checkout_btn
    And we click on page personal locator continue_btn
    Then we enter value on page personal locator first_name_field value "MJ"
    When we enter value on page personal locator last_name_field value "Azimov"
    And we enter value on page personal locator organization_name_field value "FNB-Corp"
    Then we enter value on page personal locator email_field value "azimovm@fnb-corp.com"
    When we enter value on page personal locator zip_code_field value "15212"
    And we enter value on page personal locator phone_number value "412-482-1222"
    Then we get list of elements on page landing locator list_of_element
    Then we click on page personal locator new_customer_check
    When we click on page personal locator continue_btn
    Then we click on new page personal locator continue_btn_window
    And we click on new page personal locator view_and_accept
    When we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we click on new page personal locator canvas

  Scenario: Apply for FirstRate Savings account from the personal tab 2
    Given we click on page landing locator personal_tab
    When we click on page personal locator checking_and_savings
    Then we click on page personal locator browse_all_Saving
    And we click on page personal locator FirstRate_saving
    When we click on page personal locator add_to_cart
    And we click on page personal locator cart_proceed_to_check
    Then we click on page personal locator apply_now_check
    When we click on page personal locator checkout_btn
    And we click on page personal locator continue_btn
    Then we enter value on page personal locator first_name_field value "MJ"
    When we enter value on page personal locator last_name_field value "Azimov"
    And we enter value on page personal locator organization_name_field value "FNB-Corp"
    Then we enter value on page personal locator email_field value "azimovm@fnb-corp.com"
    When we enter value on page personal locator zip_code_field value "15212"
    And we enter value on page personal locator phone_number value "412-482-1222"
    Then we click on page personal locator new_customer_check
    When we click on page personal locator continue_btn
    Then we click on new page personal locator continue_btn_window
    And we click on new page personal locator view_and_accept
    When we click on new page personal locator canvas
#    Then we press on new page personal locator body key "End" keyboard
