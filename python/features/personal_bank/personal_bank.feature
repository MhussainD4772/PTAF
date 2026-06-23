@regression @personal_bank_test
Feature: Personal Bank Feature

  Background: Navigate to URL
    Given we navigate to eStore_QA url
    Then get title of page
#    When we click on landing locator personal_tab
#    When we click on landing locator eStore_tab
#    When we click on landing locator business_tab
#    When we click on landing locator fnb_logo
#    When we click on landing locator search_logo
#    When we click on landing locator login_btn
#    When we click on landing locator x_btn
#@testing
#  Scenario: Testing
#  Given we click on landing locator personal_tab
#  Then we click on personal locator checking_and_savings
#  Then we contain on landing locator checking_txt value "Checking"
#  When we click on personal locator browse_all_products


  Scenario Outline: Apply for checking account from the personal tab 01
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_products
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator estyle_account_header value "<AccountHeader>"
    When we click on page personal locator add_to_cart
    And we click on page personal locator cart_proceed_to_check
    Then we click on page personal locator apply_now_check
    When we click on page personal locator checkout_btn
    And we click on page personal locator continue_btn
    #Validating error messages in all fields
    Then we verify on page personal of locator fName_Validation_message is existed
    When we verify on page personal of locator lName_Validation_message is existed
    And we verify on page personal of locator email_Validation_message is existed
    Then we verify on page personal of locator zip_Validation_message is existed
    When we verify on page personal of locator phone_Validation_message is existed
    And we verify on page personal of locator new_customer_Validation_message is existed
    Then we capture screenshot on page personal locator body name "personal_page/application/error_messages"
    #Providing data in all fields
    When we enter value on page personal locator first_name_field value "<FName>"
    And we enter value on page personal locator last_name_field value "<LName>"
    Then we enter value on page personal locator organization_name_field value "<Company>"
    When we enter value on page personal locator email_field value "<Email>"
    And we enter value on page personal locator zip_code_field value "<Zip>"
    Then we enter value on page personal locator phone_number value "<Phone>"
    And we capture screenshot on page personal locator body name "personal_page/application/after_entering_credentials"
    When we click on page personal locator new_customer_check
    And we click on page personal locator continue_btn
    Then we click on new page personal locator continue_btn_window
    When we click on new page personal locator view_and_accept
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we wait for some time
    And we capture screenshot on new page personal locator body name "personal_page/application/accept_application"
    When we click on new page personal locator i_accept_btn
    Then we enter value on new page personal locator dateOfBirth value "01/01/1992"
    And we click on new page personal locator prefill_info
    When we click on new page personal locator continue_btn_window
    Then we enter value on new page personal locator firstName value "<FName>"
    And we enter value on new page personal locator lastName value "<LName>"
    And we enter value on new page personal locator email value "<Email>"
    When we click on new page personal locator suffix
    And we click on new page personal locator suffix_Text
    Then we enter value on new page personal locator ssn value "839128580"
    When we enter value on new page personal locator street_Address1 value "<StAddress1>"
    And we enter value on new page personal locator street_Address2 value "<StAddress2>"
    Then we enter value on new page personal locator city value "<City>"
    And we enter value on new page personal locator state value "<State>"
    When we press on new page personal locator body key "Enter" keyboard
    Then we get value on new page personal locator zip
    And we verify element has value on new page personal of locator zip value "<Zip>"
    Then we wait for some time
    And we click on new page personal locator next_btn
    Then we verify on new page personal of locator heading_pg3 is visible
    And we click on new page personal locator applyForCreditCard
    Then we click on new page personal locator citizenship
    And we click on new page personal locator idType
    Then we click on new page personal locator idValue
    Then we enter value on new page personal locator idNumber value "11111111"
    Then we enter value on new page personal locator stateOfIssue value "<State>"
    Then we click on new page personal locator stateOfIssueValue
    And we enter value on new page personal locator issueDate value "01/01/2010"
    And we enter value on new page personal locator expDate value "01/01/2030"
    Then we click on new page personal locator next_btn
    Then we click on new page personal locator residenceStatus
    And we click on new page personal locator residenceType
    And we enter value on new page personal locator currentRentOrMor value "3000"
    And we enter value on new page personal locator noOfYears value "5"
    When we click on new page personal locator mailingAddress
    When we click on new page personal locator next_btn
    When we click on new page personal locator employmentStatus
    When we click on new page personal locator employmentType
    And we enter value on new page personal locator empName value "FNB"
    And we enter value on new page personal locator jobTitle value "QA Lead"
    Then we click on new page personal locator OcpCategory
    Then we click on new page personal locator OcpType
    Then we click on new page personal locator payFreq
    Then we click on new page personal locator payFreqSel
    And we enter value on new page personal locator paymentAmount value "5000"
    And we enter value on new page personal locator years value "5"
    And we enter value on new page personal locator months value "6"
    Then we click on new page personal locator next_btn
    Then we click on new page personal locator coApp
    Then we click on new page personal locator next_btn
    And we wait for some time
    Then we click on new page personal locator coAppSecond
    Then we click on new page personal locator purpose
    Then we click on new page personal locator purposeType
    Then we click on new page personal locator source
    Then we click on new page personal locator sourceType
    Then we click on new page personal locator cashDeposit
    Then we click on new page personal locator wireTransfer
    Then we click on new page personal locator foreignWTransfer
    Then we click on new page personal locator next_btn
    And we enter value on new page personal locator estyle value "500"
    And we wait for some time
    When we click on new page personal locator transferFromBank
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regions bank"
    And we click on plad frame personal locator bankNameRegions
    Then we enter value on plad frame personal locator RegionsbankUserName value "custom_fnb_accounts"
    When we enter value on plad frame personal locator RegionsbankPassword value "Password"
    
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
    And we wait for some time
    Then we click radio on new page personal list locator accountSelectionRB
    And we capture screenshot on new page personal locator body name "personal_page/application/ratio"
    And we wait for some time
#    Then we click on new page personal locator next_btn
#    And we click on new page personal locator confirmSSN
#    And we click on new page personal locator confirmWithholding
#    And we click on new page personal locator viewAndAccept1
#    And we click on new page personal locator canvas
#    Then we scroll until end of the page
#    And we click on new page personal locator i_accept_btn
#    Then we wait for some time
#    And we click on new page personal locator viewAndAccept2
#    Then we wait for some time
#    And we click on new page personal locator canvas
#    Then we scroll until end of the page
#    And we click on new page personal locator i_accept_btn
#    Then we wait for some time
#    And we click on new page personal locator viewAndAccept3
#    And we click on new page personal locator canvas
#    Then we scroll until end of the page
#    And we click on new page personal locator i_accept_btn
#    Then we wait for some time
#    And we click on new page personal locator viewAndAccept4
#    And we click on new page personal locator canvas
#    Then we scroll until end of the page
#    When we enter value on new page personal locator signature value "Suresh"
#    And we click on new page personal locator i_accept_btn
#    And we click on new page personal locator foreignGovtOfficial
#    And we click on new page personal locator immediateFamilyMember
#    And we click on new page personal locator deplomat
#    And we click on new page personal locator continue_btn_window
    Examples:
      |AccountType         |AccountHeader |  FName  | LName    | Company | Email                 | Zip   | Phone       | StAddress1   | StAddress2  |City        |State|
      |estyle_account      |eStyle Account| Suresh | Simhadri | FNB     | simhadri@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor |Pittsburgh  |PA   |

  Scenario Outline: Apply for checking account from the personal tab 02
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_products
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator estyle_account_header value "<AccountHeader>"
    When we click on page personal locator add_to_cart
    And we click on page personal locator cart_proceed_to_check
    Then we click on page personal locator apply_now_check
    When we click on page personal locator checkout_btn
    And we click on page personal locator continue_btn
    #Validating error messages in all fields
    Then we verify on page personal of locator fName_Validation_message is existed
    When we verify on page personal of locator lName_Validation_message is existed
    And we verify on page personal of locator email_Validation_message is existed
    Then we verify on page personal of locator zip_Validation_message is existed
    When we verify on page personal of locator phone_Validation_message is existed
    And we verify on page personal of locator new_customer_Validation_message is existed
    Then we capture screenshot on page personal locator body name "personal_page/application/error_messages"
    #Providing data in all fields
    When we enter value on page personal locator first_name_field value "<FName>"
    And we enter value on page personal locator last_name_field value "<LName>"
    Then we enter value on page personal locator organization_name_field value "<Company>"
    When we enter value on page personal locator email_field value "<Email>"
    And we enter value on page personal locator zip_code_field value "<Zip>"
    Then we enter value on page personal locator phone_number value "<Phone>"
    And we capture screenshot on page personal locator body name "personal_page/application/after_entering_credentials"
    When we click on page personal locator new_customer_check
    And we click on page personal locator continue_btn
    Then we click on new page personal locator continue_btn_window
    When we click on new page personal locator view_and_accept
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we wait for some time
    And we capture screenshot on new page personal locator body name "personal_page/application/accept_application"
    When we click on new page personal locator i_accept_btn
    Then we enter value on new page personal locator dateOfBirth value "01/01/1992"
    And we click on new page personal locator prefill_info
    When we click on new page personal locator continue_btn_window
    Then we enter value on new page personal locator firstName value "<FName>"
    And we enter value on new page personal locator lastName value "<LName>"
    And we enter value on new page personal locator email value "<Email>"
    When we click on new page personal locator suffix
    And we click on new page personal locator suffix_Text
    Then we enter value on new page personal locator ssn value "839128580"
    When we enter value on new page personal locator street_Address1 value "<StAddress1>"
    And we enter value on new page personal locator street_Address2 value "<StAddress2>"
    Then we enter value on new page personal locator city value "<City>"
    And we enter value on new page personal locator state value "<State>"
    When we press on new page personal locator body key "Enter" keyboard
    Then we get value on new page personal locator zip
    And we verify element has value on new page personal of locator zip value "<Zip>"
    Then we wait for some time
    And we click on new page personal locator next_btn
    Then we verify on new page personal of locator heading_pg3 is visible
    And we click on new page personal locator applyForCreditCard
    Then we click on new page personal locator citizenship
    And we click on new page personal locator idType
    Then we click on new page personal locator idValue
    Then we enter value on new page personal locator idNumber value "11111111"
    Then we enter value on new page personal locator stateOfIssue value "<State>"
    Then we click on new page personal locator stateOfIssueValue
    And we enter value on new page personal locator issueDate value "01/01/2010"
    And we enter value on new page personal locator expDate value "01/01/2030"
    Then we click on new page personal locator next_btn
    Then we click on new page personal locator residenceStatus
    And we click on new page personal locator residenceType
    And we enter value on new page personal locator currentRentOrMor value "3000"
    And we enter value on new page personal locator noOfYears value "5"
    When we click on new page personal locator mailingAddress
    When we click on new page personal locator next_btn
    When we click on new page personal locator employmentStatus
    When we click on new page personal locator employmentType
    And we enter value on new page personal locator empName value "FNB"
    And we enter value on new page personal locator jobTitle value "QA Lead"
    Then we click on new page personal locator OcpCategory
    Then we click on new page personal locator OcpType
    Then we click on new page personal locator payFreq
    Then we click on new page personal locator payFreqSel
    And we enter value on new page personal locator paymentAmount value "5000"
    And we enter value on new page personal locator years value "5"
    And we enter value on new page personal locator months value "6"
    Then we click on new page personal locator next_btn
    Then we click on new page personal locator coApp
    Then we click on new page personal locator next_btn
    And we wait for some time
    Then we click on new page personal locator coAppSecond
    Then we click on new page personal locator purpose
    Then we click on new page personal locator purposeType
    Then we click on new page personal locator source
    Then we click on new page personal locator sourceType
    Then we click on new page personal locator cashDeposit
    Then we click on new page personal locator wireTransfer
    Then we click on new page personal locator foreignWTransfer
    Then we click on new page personal locator next_btn
    And we enter value on new page personal locator estyle value "500"
    And we wait for some time
    When we click on new page personal locator transferFromBank
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regions bank"
    And we click on plad frame personal locator bankNameRegions
    Then we enter value on plad frame personal locator RegionsbankUserNam value "custom_fnb_accounts"
    When we enter value on plad frame personal locator RegionsbankPassword value "Password"
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
    And we wait for some time
    Then we click radio on new page personal list locator accountSelectionRB
    And we capture screenshot on new page personal locator body name "personal_page/application/ratio"
    And we wait for some time
#    Then we click on new page personal locator next_btn
#    And we click on new page personal locator confirmSSN
#    And we click on new page personal locator confirmWithholding
#    And we click on new page personal locator viewAndAccept1
#    And we click on new page personal locator canvas
#    Then we scroll until end of the page
#    And we click on new page personal locator i_accept_btn
#    Then we wait for some time
#    And we click on new page personal locator viewAndAccept2
#    Then we wait for some time
#    And we click on new page personal locator canvas
#    Then we scroll until end of the page
#    And we click on new page personal locator i_accept_btn
#    Then we wait for some time
#    And we click on new page personal locator viewAndAccept3
#    And we click on new page personal locator canvas
#    Then we scroll until end of the page
#    And we click on new page personal locator i_accept_btn
#    Then we wait for some time
#    And we click on new page personal locator viewAndAccept4
#    And we click on new page personal locator canvas
#    Then we scroll until end of the page
#    When we enter value on new page personal locator signature value "Suresh"
#    And we click on new page personal locator i_accept_btn
#    And we click on new page personal locator foreignGovtOfficial
#    And we click on new page personal locator immediateFamilyMember
#    And we click on new page personal locator deplomat
#    And we click on new page personal locator continue_btn_window
    Examples:
      |AccountType         |AccountHeader |  FName  | LName    | Company | Email                 | Zip   | Phone       | StAddress1   | StAddress2  |City        |State|
      |estyle_account      |eStyle Account| Suresh | Simhadri | FNB     | simhadri@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor |Pittsburgh  |PA   |