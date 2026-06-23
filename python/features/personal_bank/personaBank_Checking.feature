@regression @personal_bank @smoke
Feature: Personal Bank Feature

  Background: Navigate to URL
    Given we navigate to eStore_QA url
    Then get title of page

  @estyle
  Scenario Outline: Apply for estyle checking account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_products
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator estyle_account_header value "<AccountHeader>"
    Then we click on page personal locator add_to_cart
    When we click on page personal locator cart_proceed_to_check
    When we click on page personal locator apply_now_check
    When we click on page personal locator checkout_btn
    When we click on page personal locator continue_btn
    #Validating error messages in all fields
    Then we verify on page personal of locator fName_Validation_message is existed
    Then we verify on page personal of locator lName_Validation_message is existed
    Then we verify on page personal of locator email_Validation_message is existed
    Then we verify on page personal of locator zip_Validation_message is existed
    Then we verify on page personal of locator phone_Validation_message is existed
    Then we verify on page personal of locator new_customer_Validation_message is existed
    #Providing data in all fields
    And we enter value on page personal locator first_name_field value "<FName>"
    And we enter value on page personal locator last_name_field value "<LName>"
    And we enter value on page personal locator organization_name_field value "<Company>"
    And we enter value on page personal locator email_field value "<Email>"
    And we enter value on page personal locator zip_code_field value "<Zip>"
    And we enter value on page personal locator phone_number value "<Phone>"
    When we click on page personal locator new_customer_check
    When we click on page personal locator continue_btn
    When we click on new page personal locator continue_btn_window
    When we click on new page personal locator view_and_accept
    When we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we click on new page personal locator i_accept_btn
    And we enter value on new page personal locator dateOfBirth value "01/01/1992"
    When we click on new page personal locator prefill_info
    Then we click on new page personal locator continue_btn_window
    And we enter value on new page personal locator firstName value "<FName>"
    And we enter value on new page personal locator lastName value "<LName>"
    And we enter value on new page personal locator email value "<Email>"
    When we click on new page personal locator suffix
    When we click on new page personal locator suffix_Text
    Then we enter value on new page personal locator ssn value "839128580"
    Then we enter value on new page personal locator street_Address1 value "<StAddress1>"
    Then we enter value on new page personal locator street_Address2 value "<StAddress2>"
    Then we enter value on new page personal locator city value "<City>"
    Then we enter value on new page personal locator state value "<State>"
    And we press on new page personal locator body key "Enter" keyboard
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
    And we enter value on new page personal locator estyle value "100"
    And we wait for some time
    When we click on new page personal locator transferFromBank
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regional"
    And we click on plad frame personal locator bankName
    When we enter value on plad frame personal locator bankUserName value "custom_fnb_accounts"
    Then we enter value on plad frame personal locator bankPassword value "Password"
    Then we capture screenshot on plad frame personal locator body name "personal_page/application/plad_screen"
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
    Then we click radio on new page personal list locator accountSelectionRB
    Then we click on new page personal locator next_btn
    And we click on new page personal locator confirmSSN
    And we click on new page personal locator confirmWithholding
    And we click on new page personal locator viewAndAccept1
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept2
    Then we wait for some time
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept3
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept4
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we enter value on new page .* locator .* value ".*"
    When we enter value on new page personal locator signature value "Suresh"
    And we click on new page personal locator i_accept_btn
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    Then we check on new page personal locator debitcard
    And we click on new page personal locator continue_btn_window
    Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType    | AccountHeader  | FName  | LName    | Company | Email                 | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |
      | estyle_account | eStyle Account | Suresh | Simhadri | FNB     | simhadri@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |

  @estylePlus
  Scenario Outline: Apply for estyle plus checking account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_products
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator estyle_account_header value "<AccountHeader>"
    Then we click on page personal locator add_to_cart
    When we click on page personal locator cart_proceed_to_check
    When we click on page personal locator apply_now_check
    When we click on page personal locator checkout_btn
    When we click on page personal locator continue_btn
    #Validating error messages in all fields
    Then we verify on page personal of locator fName_Validation_message is existed
    Then we verify on page personal of locator lName_Validation_message is existed
    Then we verify on page personal of locator email_Validation_message is existed
    Then we verify on page personal of locator zip_Validation_message is existed
    Then we verify on page personal of locator phone_Validation_message is existed
    Then we verify on page personal of locator new_customer_Validation_message is existed
    #Providing data in all fields
    And we enter value on page personal locator first_name_field value "<FName>"
    And we enter value on page personal locator last_name_field value "<LName>"
    And we enter value on page personal locator organization_name_field value "<Company>"
    And we enter value on page personal locator email_field value "<Email>"
    And we enter value on page personal locator zip_code_field value "<Zip>"
    And we enter value on page personal locator phone_number value "<Phone>"
    When we click on page personal locator new_customer_check
    When we click on page personal locator continue_btn
    When we click on new page personal locator continue_btn_window
    When we click on new page personal locator view_and_accept
    When we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we click on new page personal locator i_accept_btn
    And we enter value on new page personal locator dateOfBirth value "01/01/1992"
    When we click on new page personal locator prefill_info
    Then we click on new page personal locator continue_btn_window
    And we enter value on new page personal locator firstName value "<FName>"
    And we enter value on new page personal locator lastName value "<LName>"
    And we enter value on new page personal locator email value "<Email>"
    When we click on new page personal locator suffix
    When we click on new page personal locator suffix_Text
    Then we enter value on new page personal locator ssn value "839128580"
    Then we enter value on new page personal locator street_Address1 value "<StAddress1>"
    Then we enter value on new page personal locator street_Address2 value "<StAddress2>"
    Then we enter value on new page personal locator city value "<City>"
    Then we enter value on new page personal locator state value "<State>"
    And we press on new page personal locator body key "Enter" keyboard
    Then we verify element has value on new page personal of locator zip value "<Zip>"
    Then we wait for some time
    And we click on new page personal locator next_btn
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
    And we enter value on new page personal locator noOfYears value "5"
    When we click on new page personal locator mailingAddress
    When we click on new page personal locator next_btn
    When we click on new page personal locator employmentStatus
    When we click on new page personal locator employmentType
    And we enter value on new page personal locator empName value "FNB"
    And we enter value on new page personal locator jobTitle value "QA Lead"
    Then we click on new page personal locator OcpCategory
    Then we click on new page personal locator OcpType
    And we enter value on new page personal locator estimated_annualIncome value "5000"
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
    And we enter value on new page personal locator estyle value "100"
    And we enter value on new page personal locator firstRateSaving value "50"
    And we wait for some time
    When we click on new page personal locator transferFromBank
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regional"
    And we click on plad frame personal locator bankName
    When we enter value on plad frame personal locator bankUserName value "custom_fnb_accounts"
    Then we enter value on plad frame personal locator bankPassword value "Password"
    Then we capture screenshot on plad frame personal locator body name "personal_page/application/plad_screen"
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
    Then we click radio on new page personal list locator accountSelectionRB
    Then we click on new page personal locator next_btn
    And we click on new page personal locator confirmSSN
    And we click on new page personal locator confirmWithholding
    And we click on new page personal locator viewAndAccept1
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept2
    Then we wait for some time
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept3
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "Suresh"
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept4
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "Suresh"
    And we click on new page personal locator i_accept_btn
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    And we check on new page personal locator debitcard
    And we click on new page personal locator continue_btn_window
        Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible

    Examples:
      | AccountType          | AccountHeader         | FName  | LName    | Company | Email                 | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |
#      |estyle_plus      |eStyle Plus Account| Suresh | Simhadri  | FNB     | simhadri@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor |Pittsburgh  |PA   |
      | premierstyle_account | Premierstyle Checking | Suresh | Simhadri | FNB     | simhadri@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |

  @freeStyle @lifeStyle
  Scenario Outline: Apply for free style checking account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_products
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator estyle_account_header value "<AccountHeader>"
    Then we click on page personal locator add_to_cart
    When we click on page personal locator cart_proceed_to_check
    When we click on page personal locator apply_now_check
    When we click on page personal locator checkout_btn
    When we click on page personal locator continue_btn
    #Validating error messages in all fields
    Then we verify on page personal of locator fName_Validation_message is existed
    Then we verify on page personal of locator lName_Validation_message is existed
    Then we verify on page personal of locator email_Validation_message is existed
    Then we verify on page personal of locator zip_Validation_message is existed
    Then we verify on page personal of locator phone_Validation_message is existed
    Then we verify on page personal of locator new_customer_Validation_message is existed
    #Providing data in all fields
    And we enter value on page personal locator first_name_field value "<FName>"
    And we enter value on page personal locator last_name_field value "<LName>"
    And we enter value on page personal locator organization_name_field value "<Company>"
    And we enter value on page personal locator email_field value "<Email>"
    And we enter value on page personal locator zip_code_field value "<Zip>"
    And we enter value on page personal locator phone_number value "<Phone>"
    When we click on page personal locator new_customer_check
    When we click on page personal locator continue_btn
    When we click on new page personal locator continue_btn_window
    When we click on new page personal locator view_and_accept
    When we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we click on new page personal locator i_accept_btn
    And we enter value on new page personal locator dateOfBirth value "01/01/1992"
    When we click on new page personal locator prefill_info
    Then we click on new page personal locator continue_btn_window
    And we enter value on new page personal locator firstName value "<FName>"
    And we enter value on new page personal locator lastName value "<LName>"
    And we enter value on new page personal locator email value "<Email>"
    When we click on new page personal locator suffix
    When we click on new page personal locator suffix_Text
    Then we enter value on new page personal locator ssn value "839128580"
    Then we enter value on new page personal locator street_Address1 value "<StAddress1>"
    Then we enter value on new page personal locator street_Address2 value "<StAddress2>"
    Then we enter value on new page personal locator city value "<City>"
    Then we enter value on new page personal locator state value "<State>"
    And we press on new page personal locator body key "Enter" keyboard
    Then we verify element has value on new page personal of locator zip value "<Zip>"
    Then we wait for some time
    And we click on new page personal locator next_btn
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
    And we enter value on new page personal locator noOfYears value "5"
    When we click on new page personal locator mailingAddress
    When we click on new page personal locator next_btn
    When we click on new page personal locator employmentStatus
    When we click on new page personal locator employmentType
    And we enter value on new page personal locator empName value "FNB"
    And we enter value on new page personal locator jobTitle value "QA Lead"
    Then we click on new page personal locator OcpCategory
    Then we click on new page personal locator OcpType
    And we enter value on new page personal locator estimated_annualIncome value "5000"
    Then we click on new page personal locator next_btn
#    Then we click on new page personal locator coApp
#    Then we click on new page personal locator next_btn
    And we wait for some time
    Then we click on new page personal locator coAppSecond
    Then we click on new page personal locator purpose
    Then we click on new page personal locator purposeType
    Then we click on new page personal locator source
    Then we click on new page personal locator sourceType
    Then we click on new page personal locator cashDeposit
    Then we click on new page personal locator wireTransfer
    Then we click on new page personal locator foreignWTransfer
    Then we click on new page personal locator automaticCoverage
    And we click on new page personal locator view_and_accept
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "Suresh"
    And we click on new page personal locator i_accept_btn
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
    And we enter value on new page personal locator estyle value "100"
    And we wait for some time
    And we enter value on new page personal locator firstRateSaving value "50"
    When we click on new page personal locator transferFromBank
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regional"
    And we click on plad frame personal locator bankName
    When we enter value on plad frame personal locator bankUserName value "custom_fnb_accounts"
    Then we enter value on plad frame personal locator bankPassword value "Password"
    Then we capture screenshot on plad frame personal locator body name "personal_page/application/plad_screen"
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
    Then we click radio on new page personal list locator accountSelectionRB
    Then we click on new page personal locator next_btn
    And we click on new page personal locator confirmSSN
    And we click on new page personal locator confirmWithholding
    And we click on new page personal locator viewAndAccept1
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept2_FS
    Then we wait for some time
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept3
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "Suresh"
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept4_FS
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "Suresh"
    And we click on new page personal locator i_accept_btn
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    And we check on new page personal locator debitcard
    And we click on new page personal locator continue_btn_window
    And we check on new page personal locator orderChecks
    And we click on new page personal locator continue_btn_window
        Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType       | AccountHeader      | FName  | LName    | Company | Email                 | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |
      | freestyle_account | Freestyle Checking | Suresh | Simhadri | FNB     | simhadri@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |
      | lifestyle_account | Lifestyle Checking | Suresh | Simhadri | FNB     | simhadri@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |


  @premiumStyle
  Scenario Outline: Apply for free style checking account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_products
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator estyle_account_header value "<AccountHeader>"
    Then we click on page personal locator add_to_cart
    When we click on page personal locator cart_proceed_to_check
    When we click on page personal locator apply_now_check
    When we click on page personal locator checkout_btn
    When we click on page personal locator continue_btn
    #Validating error messages in all fields
    Then we verify on page personal of locator fName_Validation_message is existed
    Then we verify on page personal of locator lName_Validation_message is existed
    Then we verify on page personal of locator email_Validation_message is existed
    Then we verify on page personal of locator zip_Validation_message is existed
    Then we verify on page personal of locator phone_Validation_message is existed
    Then we verify on page personal of locator new_customer_Validation_message is existed
    #Providing data in all fields
    And we enter value on page personal locator first_name_field value "<FName>"
    And we enter value on page personal locator last_name_field value "<LName>"
    And we enter value on page personal locator organization_name_field value "<Company>"
    And we enter value on page personal locator email_field value "<Email>"
    And we enter value on page personal locator zip_code_field value "<Zip>"
    And we enter value on page personal locator phone_number value "<Phone>"
    When we click on page personal locator new_customer_check
    When we click on page personal locator continue_btn
    When we click on new page personal locator continue_btn_window
    When we click on new page personal locator view_and_accept
    When we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we click on new page personal locator i_accept_btn
    And we enter value on new page personal locator dateOfBirth value "01/01/1992"
    When we click on new page personal locator prefill_info
    Then we click on new page personal locator continue_btn_window
    And we enter value on new page personal locator firstName value "<FName>"
    And we enter value on new page personal locator lastName value "<LName>"
    And we enter value on new page personal locator email value "<Email>"
    When we click on new page personal locator suffix
    When we click on new page personal locator suffix_Text
    Then we enter value on new page personal locator ssn value "839128580"
    Then we enter value on new page personal locator street_Address1 value "<StAddress1>"
    Then we enter value on new page personal locator street_Address2 value "<StAddress2>"
    Then we enter value on new page personal locator city value "<City>"
    Then we enter value on new page personal locator state value "<State>"
    And we press on new page personal locator body key "Enter" keyboard
    Then we verify element has value on new page personal of locator zip value "<Zip>"
    Then we wait for some time
    And we click on new page personal locator next_btn
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
    And we enter value on new page personal locator estimated_annualIncome value "5000"
    Then we click on new page personal locator next_btn
#    Then we click on new page personal locator coApp
#    Then we click on new page personal locator next_btn
    And we wait for some time
    Then we click on new page personal locator coAppSecond
    Then we click on new page personal locator purpose
    Then we click on new page personal locator purposeType
    Then we click on new page personal locator source
    Then we click on new page personal locator sourceType
    Then we click on new page personal locator cashDeposit
    Then we click on new page personal locator wireTransfer
    Then we click on new page personal locator foreignWTransfer
    Then we click on new page personal locator automaticCoverage
    And we click on new page personal locator view_and_accept
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "Suresh"
    And we click on new page personal locator i_accept_btn
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
    And we enter value on new page personal locator estyle value "100"
    And we wait for some time
    And we enter value on new page personal locator firstRateSaving value "50"
    When we click on new page personal locator transferFromBank
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regional"
    And we click on plad frame personal locator bankName
    When we enter value on plad frame personal locator bankUserName value "custom_fnb_accounts"
    Then we enter value on plad frame personal locator bankPassword value "Password"
    Then we capture screenshot on plad frame personal locator body name "personal_page/application/plad_screen"
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
    Then we click radio on new page personal list locator accountSelectionRB
    Then we click on new page personal locator next_btn
    And we click on new page personal locator confirmSSN
    And we click on new page personal locator confirmWithholding
    And we click on new page personal locator viewAndAccept1
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept2_FS
    Then we wait for some time
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept3
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "Suresh"
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept4_FS
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "Suresh"
    And we click on new page personal locator i_accept_btn
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    And we check on new page personal locator debitcard
    And we click on new page personal locator continue_btn_window
    And we check on new page personal locator orderChecks
    And we click on new page personal locator continue_btn_window
        Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType          | AccountHeader         | FName  | LName    | Company | Email                 | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |
      | premierstyle_account | Premierstyle Checking | Suresh | Simhadri | FNB     | simhadri@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |