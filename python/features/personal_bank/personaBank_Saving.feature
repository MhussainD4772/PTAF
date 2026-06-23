@regression @personal_bank_business @smoke
Feature: Personal Bank Feature

  Background: Navigate to URL
    Given we navigate to eStore_QA url
    Then get title of page

  @FirstRate
  Scenario Outline: Apply for FirstRate Savings account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_Saving
    And we click on page personal locator <AccountType>
#    Then we contain on page personal of locator FirstRate_Savings_header value "<AccountHeader>"
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
    Then we enter value on new page personal locator idNumber value "<IdNumber>"
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
    And we enter value on new page personal locator FirstRate value "99"
    And we wait for some time
    When we click on new page personal locator Credit_Debit_Card

    And we click on pop frame personal locator Credit_debit_Frame
    Then we click on pop frame personal locator Card_NbrField
    When we enter value on pop frame personal locator Card_Nbr value "4111111111111111"
    And we enter value on pop frame personal locator Card_Expiry value "03/30"
    And we enter value on pop frame personal locator Card_Code value "123"
    And we enter value on pop frame personal locator Card_FirstName value "<FName>"
    And we enter value on pop frame personal locator Card_LastName value "<LName>"
    And we enter value on pop frame personal locator Card_ZipCode value "<Zip>"
    Then we click on pop frame personal locator ProcessCard
    Then we click on new page personal locator CardContinueBtn
#    When we click on new page personal locator CardContinueBtn
    And we wait for some time

    And we click on new page personal locator confirmSSN
    And we click on new page personal locator confirmWithholding
    And we click on new page personal locator viewAndAccept1
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
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
    When we enter value on new page personal locator signature value "<Signature>"
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
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType      | AccountHeader     | FName  | LName  | Company | Email               | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |  | Signature |  | IdNumber |
      | FirstRate_saving | FirstRate Savings | Dinesh | Tiwari | FNB     | Tiwari@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |  | Dinesh    |  | 11111111 |


  @FirstRateMoney
  Scenario Outline: Apply for FirstRate Money Market savings account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_Saving
    And we click on page personal locator <AccountType>
#    Then we contain on page personal of locator FirstRateMoneyMktHd value "<AccountHeader>"
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
    Then we verify on new page personal of locator heading_pg3 is visible
    And we click on new page personal locator applyForCreditCard
    Then we click on new page personal locator citizenship
    And we click on new page personal locator idType
    Then we click on new page personal locator idValue
    Then we enter value on new page personal locator idNumber value "<IdNumber>"
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
    And we enter value on new page personal locator FirstRateMoney value "99"
    And we wait for some time
    When we click on new page personal locator Credit_Debit_Card
    
    And we click on pop frame personal locator Credit_debit_Frame
    Then we click on pop frame personal locator Card_NbrField
    When we enter value on pop frame personal locator Card_Nbr value "4111111111111111"
    And we enter value on pop frame personal locator Card_Expiry value "03/30"
    And we enter value on pop frame personal locator Card_Code value "123"
    And we enter value on pop frame personal locator Card_FirstName value "<FName>"
    And we enter value on pop frame personal locator Card_LastName value "<LName>"
    And we enter value on pop frame personal locator Card_ZipCode value "<Zip>"
    Then we click on pop frame personal locator ProcessCard
    When we click on new page personal locator CardContinueBtn
    And we wait for some time

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
    When we enter value on new page personal locator signature value "<Signature>"
    And we click on new page personal locator i_accept_btn
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    And we check on new page personal locator debitcard
    And we click on new page personal locator continue_btn_window
    And we check on new page personal locator CheckOrder
    And we click on new page personal locator continue_btn_window
    Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType       | AccountHeader          | FName  | LName  | Company | Email               | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |  | Signature |  | IdNumber |
      | FirstRateMoneyMkt | FirstRate Money Market | Dinesh | Tiwari | FNB     | Tiwari@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |  | Dinesh    |  | 11111111 |


  @HealthSavingsAccount
  Scenario Outline: Apply for FirstRate Money Market savings account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_Saving
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator estyle_account_header value "<AccountHeader>"
    Then we click on page personal locator add_to_cart
    When we click on page personal locator cart_proceed_to_check
    When we click on page personal locator apply_now_check
    Then we select on page personal locator HSAaccountType value "<HSAaccountValue>"
    Then we wait for some time
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
    And we click on new page personal locator ApplyCheckingAc
    Then we click on new page personal locator citizenship
    And we click on new page personal locator idType
    Then we click on new page personal locator idValue
    Then we enter value on new page personal locator idNumber value "<IdNumber>"
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
    And we enter value on new page personal locator AnnualIncome value "100000"
    Then we click on new page personal locator next_btn
    And we wait for some time
    Then we click on new page personal locator TaxYear
    Then we click on new page personal locator purpose
    Then we click on new page personal locator purposeType
    Then we click on new page personal locator source
    Then we click on new page personal locator sourceType
    Then we click on new page personal locator cashDeposit
    Then we click on new page personal locator wireTransfer
    Then we click on new page personal locator foreignWTransfer
    Then we click on new page personal locator next_btn
    And we enter value on new page personal locator HCAIndividual value "99"
    And we wait for some time
    When we click on new page personal locator Credit_Debit_Card

    And we click on pop frame personal locator Credit_debit_Frame
#    And we click on pop frame personal locator Credit_debit_Frame
    Then we click on pop frame personal locator Card_NbrField
    When we enter value on pop frame personal locator Card_Nbr value "4111111111111111"
    And we enter value on pop frame personal locator Card_Expiry value "03/30"
    And we enter value on pop frame personal locator Card_Code value "123"
    And we enter value on pop frame personal locator Card_FirstName value "<FName>"
    And we enter value on pop frame personal locator Card_LastName value "<LName>"
    And we enter value on pop frame personal locator Card_ZipCode value "<Zip>"
    Then we click on pop frame personal locator ProcessCard
    When we click on new page personal locator CardContinueBtn
    And we wait for some time
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
    When we enter value on new page personal locator signature value "<Signature>"
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator viewAndAccept4
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "<Signature>"
    And we click on new page personal locator i_accept_btn
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    And we check on new page personal locator debitcard
    And we click on new page personal locator continue_btn_window
    And we check on new page personal locator CheckOrder
    And we click on new page personal locator continue_btn_window
    Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType      | AccountHeader  | FName  | LName  | Company | Email               | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |  | Signature |  | IdNumber | HSAaccountValue |
      | HealthSavAccount | Health Savings | Dinesh | Tiwari | FNB     | Tiwari@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |  | Dinesh    |  | 11111111 | HSA Individual  |


  @CertificateDeposit
  Scenario Outline: Apply for FirstRate Money Market savings account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_Saving
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator CertificateDepositHD value "<AccountHeader>"
    Then we click on page personal locator add_to_cart
    When we click on page personal locator cart_proceed_to_check
    When we click on page personal locator apply_now_check
    Then we select on page personal locator CertDipoType value "<CreditDipTenure>"
    Then we wait for some time
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
    And we click on new page personal locator ApplyCheckingAc
    Then we click on new page personal locator citizenship
    And we click on new page personal locator idType
    Then we click on new page personal locator idValue
    Then we enter value on new page personal locator idNumber value "<IdNumber>"
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
    And we enter value on new page personal locator AnnualIncome value "100000"
    Then we click on new page personal locator next_btn
    And we wait for some time
    Then we click on new page personal locator coApp
    Then we click on new page personal locator purpose
    Then we click on new page personal locator purposeType
    Then we click on new page personal locator source
    Then we click on new page personal locator sourceType
    Then we click on new page personal locator next_btn
    And we enter value on new page personal locator CertDipoAmount value "600"
    And we wait for some time
    When we click on new page personal locator transferFromBank1
    
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regions bank"
    And we click on plad frame personal locator bankNameRegions
    Then we enter value on plad frame personal locator RegionsbankUserName value "custom_fnb_accounts"
    When we enter value on plad frame on personal locator RegionsbankPassword value "Password"
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
    
    Then we wait for some time
    Then we click radio on new page personal list locator accountSelectionRB
    Then we click on new page personal locator next_btn
    And we wait for some time
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
    And we click on new page personal locator viewAndAcceptCertDipo
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "<Signature>"
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType        | AccountHeader           | FName  | LName  | Company | Email               | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |  | Signature |  | IdNumber | CreditDipTenure |
      | CertificateDeposit | Certificates of Deposit | Dinesh | Tiwari | FNB     | Tiwari@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |  | Dinesh    |  | 11111111 | 48 Months       |


  @SaversGoalCertDip
  Scenario Outline: Apply for FirstRate Money Market savings account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_Saving
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator SaversGoalCertDipHD value "<AccountHeader>"
    Then we click on page personal locator add_to_cart
    When we click on page personal locator cart_proceed_to_check
    When we click on page personal locator apply_now_check
    When we click on page personal locator checkout_btn
    When we click on page personal locator continue_btn
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
    And we click on new page personal locator ApplyCheckingAc
    Then we click on new page personal locator citizenship
    And we click on new page personal locator idType
    Then we click on new page personal locator idValue
    Then we enter value on new page personal locator idNumber value "<IdNumber>"
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
    And we enter value on new page personal locator AnnualIncome value "100000"
    Then we click on new page personal locator next_btn
    And we wait for some time
    Then we click on new page personal locator coApp
    Then we click on new page personal locator purpose
    Then we click on new page personal locator purposeType
    Then we click on new page personal locator source
    Then we click on new page personal locator sourceType
    Then we click on new page personal locator next_btn
    And we enter value on new page personal locator SaversGoalDipoAmount value "200"
    And we wait for some time
    When we click on new page personal locator transferFromBank1
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regions bank"
    And we click on plad frame personal locator bankNameRegions
    When we enter value on plad frame personal locator RegionsbankUserName value "custom_fnb_accounts"
    Then we enter value on plad frame personal locator RegionsbankPassword value "Password"
    Then we capture screenshot on plad frame personal locator body name "personal_page/application/plad_screen"
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
#Then stop the control
    Then we wait for some time
    Then we click radio on new page personal list locator accountSelectionRB
    Then we click on new page personal locator next_btn
    And we wait for some time
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
    And we click on new page personal locator viewAndAcceptCertDipo
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "<Signature>"
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType       | AccountHeader               | FName  | LName  | Company | Email               | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |  | Signature |  | IdNumber |
      | SaversGoalCertDip | Savers Goal Certificates of | Dinesh | Tiwari | FNB     | Tiwari@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |  | Dinesh    |  | 11111111 |

  @CertiDepositSpecial
  Scenario Outline: Apply for FirstRate Money Market savings account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_Saving
    And we click on page personal locator <AccountType>
    Then we contain on page personal of locator CertiDepositSpecialHD value "<AccountHeader>"
    Then we click on page personal locator add_to_cartSpDepo
    When we click on page personal locator cart_proceed_to_check
    When we click on page personal locator apply_now_check
    Then we select on page personal locator CertDipoType value "<CreditDipTenure>"
    #Then we wait for some time
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
    And we click on new page personal locator ApplyCheckingAc
    Then we click on new page personal locator citizenship
    And we click on new page personal locator idType
    Then we click on new page personal locator idValue
    Then we enter value on new page personal locator idNumber value "<IdNumber>"
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
    And we enter value on new page personal locator AnnualIncome value "100000"
    Then we click on new page personal locator next_btn
    And we wait for some time
    Then we click on new page personal locator coApp

    Then we click on new page personal locator ExitingFnbAC

    Then we click on new page personal locator purpose
    Then we click on new page personal locator purposeType
    Then we click on new page personal locator source
    Then we click on new page personal locator sourceType
    Then we click on new page personal locator next_btn
    And we enter value on new page personal locator CertiDepoSpecialAmt value "5000"
    And we wait for some time
    When we click on new page personal locator transferFromBank1
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regions bank"
    And we click on plad frame personal locator bankNameRegions
    Then we enter value on plad frame personal locator RegionsbankUserName value "custom_fnb_accounts"
    When we enter value on plad frame on personal locator RegionsbankPassword value "Password"
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
    Then we wait for some time
    Then we click radio on new page personal list locator accountSelectionRB
    Then we click on new page personal locator next_btn
    And we wait for some time
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
    And we click on new page personal locator viewAndAcceptCertDipo
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "<Signature>"
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType        | AccountHeader           | FName  | LName  | Company | Email               | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |  | Signature |  | IdNumber | CreditDipTenure |
      | CertiDepositSpecial | Certificate of Deposit | Dinesh | Tiwari | FNB     | Tiwari@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |  | Dinesh    |  | 11111111 | 52 Month CD Special |


  @FlexCD
  Scenario Outline: Apply for FirstRate Money Market savings account from the personal tab
    Given we click on page landing locator personal_tab
    Then we click on page personal locator checking_and_savings
    When we click on page personal locator browse_all_Saving
    And we click on page personal locator <AccountType>
    And we contain on page personal of locator FlexCDHD value "<AccountHeader>"
    Then we click on page personal locator add_to_cart
    When we click on page personal locator cart_proceed_to_check
    When we click on page personal locator apply_now_check
    #Then we select on page personal locator CertDipoType value "<CreditDipTenure>"
    Then we wait for some time
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
    And we click on new page personal locator ApplyCheckingAc
    Then we click on new page personal locator citizenship
    And we click on new page personal locator idType
    Then we click on new page personal locator idValue
    Then we enter value on new page personal locator idNumber value "<IdNumber>"
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
    And we enter value on new page personal locator AnnualIncome value "100000"
    Then we click on new page personal locator next_btn
    And we wait for some time
    Then we click on new page personal locator coApp

    #Then we click on new page personal locator ExitingFnbAC

    Then we click on new page personal locator purpose
    Then we click on new page personal locator purposeType
    Then we click on new page personal locator source
    Then we click on new page personal locator sourceType
    Then we click on new page personal locator next_btn
    And we enter value on new page personal locator FlexCDHDAmt value "500"
    And we wait for some time
    When we click on new page personal locator transferFromBank1
    And we click on plad frame personal locator continue_btn
    Then we enter value on plad frame personal locator searchBank value "Regions bank"
    And we click on plad frame personal locator bankNameRegions
    Then we enter value on plad frame personal locator RegionsbankUserName value "custom_fnb_accounts"
    When we enter value on plad frame on personal locator RegionsbankPassword value "Password"
    And we click on plad frame personal locator submitButton
    And we click on plad frame personal locator continue_btn
    And we click on plad frame personal locator continue_btn
    Then we wait for some time
    Then we click radio on new page personal list locator accountSelectionRB
    Then we click on new page personal locator next_btn
    And we wait for some time
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
    And we click on new page personal locator viewAndAcceptCertDipo
    And we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we enter value on new page personal locator signature value "<Signature>"
    And we click on new page personal locator i_accept_btn
    Then we wait for some time
    And we click on new page personal locator foreignGovtOfficial
    And we click on new page personal locator immediateFamilyMember
    And we click on new page personal locator deplomat
    And we click on new page personal locator continue_btn_window
    Then we wait for some time
    Then we verify on new page personal of locator congratulations is visible
    Then we verify on new page personal of locator confirmationID is visible
    Then we verify on new page personal of locator accountNumber is visible

    Examples:
      | AccountType| AccountHeader | FName  | LName  | Company | Email             | Zip   | Phone        | StAddress1     | StAddress2 | City       | State |  | Signature |  | IdNumber | CreditDipTenure |
      | FlexCD     | Flex CD       | Dinesh | Tiwari | FNB     | Tiwari@fnb-corp.com | 15212 | 412-422-2222 | 30 Isabella St | 6th Floor  | Pittsburgh | PA    |  | Dinesh    |  | 11111111 | 52 Month CD Special |

