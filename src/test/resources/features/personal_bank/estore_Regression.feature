Feature: Personal Bank Regression Feature

  Background: Navigate to URL
    Given we navigate to eStore_QA url
    Then get title of page


@regression_365_test
Scenario Outline: FNBSB-365_FE: Implement Co-Applicant Personal Details Page - Manual Entry
  When we enter value on page personal locator firstName value "<FName>"
  When we enter value on page personal locator lastName value "<LName>"
  When we enter value on page personal locator middleName value "<MName>"
  When we click on page personal locator suffix
  When we click on page personal locator suffix_Text
  When we enter value on page personal locator email value "<Email>"
  #AC.04 SSN cannot start with 999
  When we enter value on page personal locator ssn value "99999999"
  Then we verify on page personal of locator ssn_Error is existed
  When we enter value on page personal locator ssn value "839128580"
  When we enter value on page personal locator ssn value "123456789"
  When we verify on page personal of locator ssn_Error is existed
  When we enter value on page personal locator ssn value "839128580"
  When we enter value on page personal locator street_Address1 value "<StAddress1>"
  When we enter value on page personal locator street_Address2 value "<StAddress2>"
  When we enter value on page personal locator city value "<City>"
  When we enter value on page personal locator state value "<State>"
  And we press on page personal locator body key "Enter" keyboard
  When we enter value on page personal locator zip value "<Zip>"


Examples:
|  FName  | LName    |MName|  Email                 | Zip   |  StAddress1   | StAddress2  |City        |State|
| Suresh | Simhadri | Kumar| simhadri@fnb-corp.com | 15212 | 30 Isabella St | 6th Floor |Pittsburgh  |PA   |


  @regression_351
  Scenario Outline: FNBSB-351_FE: Display Co-Applicants for Primary Applicant from Consumer when applicable via API
    Then we verify on page personal of locator coAppHeading is existed
    Then we verify on page personal of locator coAppContent1 is existed
    Then we verify on page personal of locator coAppContent2 is existed
    Then we verify on page personal of locator coAppHeading3 is existed
    Then we verify on page personal of locator coAppContent3 is existed
    Then we verify on page personal of locator coAppHeading4 is existed
    Then we verify on page personal of locator coAppContent4 is existed
    Then we verify on page personal of locator coAppHeading5 is existed
    Then we verify on page personal of locator coAppSubHeading is existed
    Then we verify on page personal of locator coAppConent5 is existed
    Then we verify on page personal of locator existingCoAppCheckBox1 is visible
    Then we verify on page personal of locator existingCoAppCheckBox2 is visible
    Then we verify on page personal of locator existingCoAppName1 is visible
    Then we verify on page personal of locator existingCoAppName2 is visible
    When we click on page personal locator existingCoAppCheckBox1
    Then we verify on page personal of locator firstNameInPopup is existed
    Then we verify on page personal of locator firstNameInPopup is enabled
    Then we verify on page personal of locator lastNameInPopup is existed
    Then we verify on page personal of locator lastNameInPopup is enabled
    Then we verify on page personal of locator emailInPopup is existed
    Then we verify on page personal of locator mobileInPopup is existed
    Then we verify on page personal of locator beneficialOwnerChkBox is existed
    Then we verify on page personal of locator signerChkBox is existed
    Then we verify on page personal of locator conttrolPersonCheckBox is existed
    Then we verify on page personal of locator ownershipPersentage is existed
    When we click on page personal locator businessRole
    And we verify on page personal of locator president is existed
    And we verify on page personal of locator vicePresident is existed
    And we verify on page personal of locator assistantVicePresident is existed
    And we verify on page personal of locator ownershipPersentage is existed
    And we verify on page personal of locator chairman is existed
    And we verify on page personal of locator chiefExecutiveOffer is existed
    And we verify on page personal of locator chiefFinancialOfficer is existed
    And we verify on page personal of locator chiefOperatingOfficer is existed
    And we verify on page personal of locator chiefExcutiveVicePresident is existed
    And we verify on page personal of locator generalPartner is existed
    And we verify on page personal of locator limitedPartner is existed
    And we verify on page personal of locator manager is existed
    And we verify on page personal of locator managingMember is existed
    And we verify on page personal of locator member is existed
    And we verify on page personal of locator owner is existed
    And we verify on page personal of locator secretary is existed
    And we verify on page personal of locator seniorVicePresident is existed
    And we verify on page personal of locator tresurer is existed
    And we verify on page personal of locator ownershipPercentage is existed
    When we click on page personal locator saveInPopup

    #Validating remove button
    When we click on page personal locator removeCoapp
    Then we verify on page personal of locator areYouSure is existed
    When we click on page personal locator cancel
    When we click on page personal locator removeCoapp
    When we click on page personal locator removeBOwner
    When we click on page personal locator existingCoAppCheckBox1
    When we click on page personal locator saveInPopup

    #Editing the details of existing first coApplicant
    When we click on page personal locator editButton
    When we enter value on page personal locator emailInPopup element value "<Email>"
    When we enter value on page personal locator mobileInPopup element value "<Phone>"
    Then we click on page personal locator beneficialOwnerChkBox
    Then we click on page personal locator signerChkBox
    Then we click on page personal locator conttrolPersonCheckBox
    When we enter value on page personal locator ownershipPersentage element value "25"
    When we click on page personal locator businessRole
    When we click on page personal locator chiefExecutiveOffer
    When we click on page personal locator saveInPopup

    #Validating the details of second Co Applicant
    When we click on page personal locator existingCoAppCheckBox2
    Then we verify on page personal of locator firstNameInPopup is existed
    Then we verify on page personal of locator firstNameInPopup is enabled
    Then we verify on page personal of locator lastNameInPopup is existed
    Then we verify on page personal of locator lastNameInPopup is enabled
    Then we verify on page personal of locator emailInPopup is existed
    Then we verify on page personal of locator mobileInPopup is existed
    Then we verify on page personal of locator beneficialOwnerChkBox is existed
    Then we verify on page personal of locator signerChkBox is existed
    Then we verify on page personal of locator conttrolPersonCheckBox is existed
    Then we verify on page personal of locator ownershipPersentage is existed

    When we click on page personal locator businessRole
    Then we verify on page personal of locator president is existed
    Then we verify on page personal of locator vicePresident is existed
    Then we verify on page personal of locator assistantVicePresident is existed
    Then we verify on page personal of locator ownershipPersentage is existed
    Then we verify on page personal of locator chairman is existed
    Then we verify on page personal of locator chiefExecutiveOffer is existed
    Then we verify on page personal of locator chiefFinancialOfficer is existed
    Then we verify on page personal of locator chiefOperatingOfficer is existed
    Then we verify on page personal of locator chiefExcutiveVicePresident is existed
    Then we verify on page personal of locator generalPartner is existed
    Then we verify on page personal of locator limitedPartner is existed
    Then we verify on page personal of locator manager is existed
    Then we verify on page personal of locator managingMember is existed
    Then we verify on page personal of locator member is existed
    Then we verify on page personal of locator owner is existed
    Then we verify on page personal of locator owner is existed
    Then we verify on page personal of locator secretary is existed
    Then we verify on page personal of locator seniorVicePresident is existed
    Then we verify on page personal of locator tresurer is existed
    When we click on page personal locator chiefExecutiveOffer
    Then we verify on page personal of locator ownershipPercentage is existed
    
    When we click on page personal locator saveInPopup

    #Editing the details of second co applicant
    When we click on page personal locator editButton2
    When we enter value on page personal locator emailInPopup element value "<Email>"
    When we enter value on page personal locator mobileInPopup element value "<Phone>"
    Then we click on page personal locator beneficialOwnerChkBox
    Then we click on page personal locator signerChkBox
    Then we click on page personal locator conttrolPersonCheckBox
    When we enter value on page personal locator ownershipPersentage element value "35"
    When we click on page personal locator businessRole
    When we click on page personal locator chiefExecutiveOffer
    When we click on page personal locator saveInPopup

    # Add New CO Applicant
    When we click on page personal locator addCoApp
    When we click on page personal locator saveInPopup
    Then we verify on page personal of locator personal is existed
    Then we verify on page personal of locator fNameError is existed
    Then we verify on page personal of locator LNameError is existed
    Then we verify on page personal of locator EmailError is existed
    Then we verify on page personal of locator MobileError is existed
    Then we verify on page personal of locator OwnershipError is existed
    Then we verify on page personal of locator BusinessRoleError is existed

    When we enter value on page personal locator firstNameInPopup element value "<FName>"
    When we enter value on page personal locator lastNameInPopup element value "<LName>"
    When we enter value on page personal locator emailInPopup element value "<Email>"
    When we enter value on page personal locator mobileInPopup element value "<Phone>"
    Then we click on page personal locator beneficialOwnerChkBox
    Then we click on page personal locator signerChkBox
    Then we click on page personal locator conttrolPersonCheckBox
    When we enter value on page personal locator ownershipPersentage element value "45"
    When we click on page personal locator businessRole
    When we click on page personal locator manager
    When we click on page personal locator saveInPopup

    Then we verify on page personal of locator addCoApp is visible

    When we click on page personal locator coAppSignerNo
    When we click on page personal locator coAppSignerYes

    # Automating FNBSB-353 user story
    When we click on page personal locator coAppOwnershipNo
    When we click on page personal locator coAppOwnershipYes
    When we enter value on page personal locator businessName element value "FNB"
    When we enter value on page personal locator ownershipPercent element value "20"
    When we click on page personal locator crossButton
    When we click on page personal locator addAnotherBusiness
    When we enter value on page personal locator businessName element value "FNB"
    When we enter value on page personal locator ownershipPercent element value "20"
    Then we verify on page personal of locator ownershipQuestion is visible
    
    When we enter value on page personal locator ownershipPercent element value "25"
    Then we verify on page personal of locator ownershipQuestion is existed
    When we click on page personal locator ownershipQuestionYes
    Then we verify on page personal of locator OwnershipQuestionYesMessage is existed
    When we click on page personal locator ownershipQuestionNo
    Then we verify on page personal of locator OwnershipQuestionNoMessage is existed
    When we click on page personal locator addAnotherBusiness
    When we enter value on page personal locator businessName2 element value "FNB"
    When we enter value on page personal locator ownerhsip2 element value "20"
    Then we verify on page personal of locator addAnotherBusiness is visible
    
    Then we verify on page personal of locator additionalBusinessQ is existed
    When we click on page personal locator foreignGovNo
    When we click on page personal locator foreignGovYes
    When we click on page personal locator next_btn



    Examples:
      |  FName  | LName    |MName|  Email                 |Phone| Zip   |  StAddress1   | StAddress2  |City        |State|
      | Suresh | Simhadri | Kumar| simhadri@fnb-corp.com | 412-124-3212|15212 | 30 Isabella St | 6th Floor |Pittsburgh  |PA   |

@regression_352
  Scenario Outline: FNBSB-352_FE:  Implement ability to Add Beneficial Owners from Other Business Owners (Add Beneficial Owners Page)

  Then we verify on page personal of locator BOHeading is existed
  Then we verify on page personal of locator BODescription is existed
  Then we verify on page personal of locator BOSubHeading is existed
  When we click on page personal locator addBO
  When we click on page personal locator saveInPopup
  Then we verify on page personal of locator fNameError is existed
  Then we verify on page personal of locator LNameError is existed
  Then we verify on page personal of locator EmailError is existed
  Then we verify on page personal of locator MobileError is existed
  Then we verify on page personal of locator OwnershipError is existed
  Then we verify on page personal of locator BusinessRoleError is existed

  When we enter value on page personal locator firstNameInPopup element value "<FName>"
  When we enter value on page personal locator lastNameInPopup element value "<LName>"
  When we enter value on page personal locator emailInPopup element value "<Email>"
  When we enter value on page personal locator mobileInPopup element value "<Phone>"
  When we enter value on page personal locator ownershipPersentage element value "25"
  When we click on page personal locator businessRole
  When we click on page personal locator manager
  When we click on page personal locator saveInPopup
  #Editing Beneficial Owner
  When we click on page personal locator editButton
  When we enter value on page personal locator emailInPopup element value "suresh@fnb-corp.com"
  When we enter value on page personal locator mobileInPopup element value "412-112-1151"
  When we enter value on page personal locator ownershipPersentage element value "35"
  When we click on page personal locator businessRole
  When we click on page personal locator chiefExecutiveOffer
  When we click on page personal locator saveInPopup

  #Validating remove button
  When we click on page personal locator removeCoapp
  Then we verify on page personal of locator areYouSure is existed
  When we click on page personal locator cancel
  When we click on page personal locator removeCoapp
  When we click on page personal locator removeBOwner

  #Beneficial Owner1
  When we click on page personal locator addBO
  When we click on page personal locator saveInPopup
  When we enter value on page personal locator firstNameInPopup element value "<FName>"
  When we enter value on page personal locator lastNameInPopup element value "<LName>"
  When we enter value on page personal locator emailInPopup element value "<Email>"
  When we enter value on page personal locator mobileInPopup element value "<Phone>"
  When we enter value on page personal locator ownershipPersentage element value "35"
  When we click on page personal locator businessRole
  When we click on page personal locator manager
  When we click on page personal locator saveInPopup

   #Beneficial Owner2
  When we click on page personal locator addBO
  When we enter value on page personal locator firstNameInPopup element value "<FName>"
  When we enter value on page personal locator lastNameInPopup element value "<LName>"
  When we enter value on page personal locator emailInPopup element value "<Email>"
  When we enter value on page personal locator mobileInPopup element value "<Phone>"
  When we enter value on page personal locator ownershipPersentage element value "25"
  When we click on page personal locator businessRole
  When we click on page personal locator manager
  When we click on page personal locator saveInPopup

  #Beneficial Owner3
  When we click on page personal locator addBO
  When we click on page personal locator saveInPopup
  When we enter value on page personal locator firstNameInPopup element value "<FName>"
  When we enter value on page personal locator lastNameInPopup element value "<LName>"
  When we enter value on page personal locator emailInPopup element value "<Email>"
  When we enter value on page personal locator mobileInPopup element value "<Phone>"
  When we enter value on page personal locator ownershipPersentage element value "25"
  When we click on page personal locator businessRole
  When we click on page personal locator manager
  When we click on page personal locator saveInPopup

  #Beneficial Owner4
  When we click on page personal locator addBO
  When we click on page personal locator saveInPopup
  When we enter value on page personal locator firstNameInPopup element value "<FName>"
  When we enter value on page personal locator lastNameInPopup element value "<LName>"
  When we enter value on page personal locator emailInPopup element value "<Email>"
  When we enter value on page personal locator mobileInPopup element value "<Phone>"
  When we enter value on page personal locator ownershipPersentage element value "45"
  When we click on page personal locator businessRole
  When we click on page personal locator manager
  When we click on page personal locator saveInPopup
  Then we verify on page personal of locator addBO is visible
  When we click on page personal locator foreignGovtYes
  When we click on page personal locator foreignGovtNo
  When we click on page personal locator disclosureChkBox
  When we click on page personal locator next_btn


  Then Stop Execution


  Examples:
    |  FName  | LName    |MName|  Email                 |Phone| Zip   |  StAddress1   | StAddress2  |City        |State|
    | Suresh | Simhadri | Kumar| simhadri@fnb-corp.com | 412-124-3212|15212 | 30 Isabella St | 6th Floor |Pittsburgh  |PA   |

  @regression_282
  Scenario Outline: FNBSB-282_FE:Implement ability for Primary Applicant to enter details of Co-Applicant (Add Co-Applicant)

    Then we verify on page personal of locator coAppHeading is existed
    Then we verify on page personal of locator coAppContent1 is existed
    Then we verify on page personal of locator coAppContent2 is existed
    Then we verify on page personal of locator coAppHeading3 is existed
    Then we verify on page personal of locator coAppContent3 is existed
    Then we verify on page personal of locator coAppHeading4 is existed
    Then we verify on page personal of locator coAppContent4 is existed
    Then we verify on page personal of locator coAppHeading5 is existed

    When we click on page personal locator addCoApp
    When we enter value on page personal locator firstNameInPopup element value "<FName>"
    When we enter value on page personal locator lastNameInPopup element value "<LName>"
    When we enter value on page personal locator emailInPopup element value "<Email>"
    When we enter value on page personal locator mobileInPopup element value "<Phone>"
    Then we click on page personal locator beneficialOwnerChkBox
    Then we click on page personal locator signerChkBox
    Then we click on page personal locator conttrolPersonCheckBox
    When we enter value on page personal locator ownershipPersentage element value "25"
    When we click on page personal locator businessRole
    Then we verify on page personal of locator president is existed
    Then we verify on page personal of locator vicePresident is existed
    Then we verify on page personal of locator assistantVicePresident is existed
    Then we verify on page personal of locator ownershipPersentage is existed
    Then we verify on page personal of locator chairman is existed
    Then we verify on page personal of locator chiefExecutiveOffer is existed
    Then we verify on page personal of locator chiefFinancialOfficer is existed
    Then we verify on page personal of locator chiefOperatingOfficer is existed
    #Then we verify on page personal of locator chiefExcutiveVicePresident
    Then we verify on page personal of locator generalPartner is existed
    Then we verify on page personal of locator limitedPartner is existed
    Then we verify on page personal of locator manager is existed
    Then we verify on page personal of locator managingMember is existed
    Then we verify on page personal of locator member is existed
    Then we verify on page personal of locator owner is existed
    Then we verify on page personal of locator secretary is existed
    Then we verify on page personal of locator seniorVicePresident is existed
    Then we verify on page personal of locator tresurer is existed
    Then we verify on page personal of locator ownershipPercentage is existed
    When we click on page personal locator manager
    When we click on page personal locator saveInPopup

    When we click on page personal locator addCoApp
    When we enter value on page personal locator firstNameInPopup element value "<FName>"
    When we enter value on page personal locator lastNameInPopup element value "<LName>"
    When we enter value on page personal locator emailInPopup element value "<Email>"
    When we enter value on page personal locator mobileInPopup element value "<Phone>"
    Then we click on page personal locator beneficialOwnerChkBox
    Then we click on page personal locator signerChkBox
    Then we click on page personal locator conttrolPersonCheckBox
    When we enter value on page personal locator ownershipPersentage element value "35"
    When we click on page personal locator businessRole
    When we click on page personal locator manager
    When we click on page personal locator saveInPopup

    When we click on page personal locator addCoApp
    When we enter value on page personal locator firstNameInPopup element value "<FName>"
    When we enter value on page personal locator lastNameInPopup element value "<LName>"
    When we enter value on page personal locator emailInPopup element value "<Email>"
    When we enter value on page personal locator mobileInPopup element value "<Phone>"
    Then we click on page personal locator beneficialOwnerChkBox
    Then we click on page personal locator signerChkBox
    Then we click on page personal locator conttrolPersonCheckBox
    When we enter value on page personal locator ownershipPersentage element value "45"
    When we click on page personal locator businessRole
    When we click on page personal locator manager
    When we click on page personal locator saveInPopup

#    When we click on page personal locator addCoApp
#    When we enter value on page personal locator firstNameInPopup element value "<FName>"
#    When we enter value on page personal locator lastNameInPopup element value "<LName>"
#    When we enter value on page personal locator emailInPopup element value "<Email>"
#    When we enter value on page personal locator mobileInPopup element value "<Phone>"
#    Then we click on page personal locator beneficialOwnerChkBox
#    Then we click on page personal locator signerChkBox
#    Then we click on page personal locator conttrolPersonCheckBox
#    When we enter value on page personal locator ownershipPersentage element value "55"
#    When we click on page personal locator businessRole
#    When we click on page personal locator manager
#    When we click on page personal locator saveInPopup

    Then we verify on page personal of locator addCoApp is visible

    When we click on page personal locator coAppSignerNo
    When we click on page personal locator coAppSignerYes

    # Automating FNBSB-353 user story
    When we click on page personal locator coAppOwnershipNo
    When we click on page personal locator coAppOwnershipYes
    When we enter value on page personal locator businessName element value "FNB"
    When we enter value on page personal locator ownershipPercent element value "20"
    When we click on page personal locator crossButton
    When we click on page personal locator addAnotherBusiness
    When we enter value on page personal locator businessName element value "FNB"
    When we enter value on page personal locator ownershipPercent element value "20"
    Then we verify on page personal of locator ownershipQuestion is visible
    When we enter value on page personal locator ownershipPercent element value "25"
    Then we verify on page personal of locator ownershipQuestion is existed
    When we click on page personal locator ownershipQuestionYes
    Then we verify on page personal of locator OwnershipQuestionYesMessage is existed
    When we click on page personal locator ownershipQuestionNo
    Then we verify on page personal of locator OwnershipQuestionNoMessage is existed
    When we click on page personal locator addAnotherBusiness
    When we enter value on page personal locator businessName2 element value "FNB"
    When we enter value on page personal locator ownerhsip2 element value "20"
    #Then we verify the existence if it is visilbe personal locator addBO
    When we click on page personal locator foreignGovNo
    When we click on page personal locator foreignGovYes
    When we click on page personal locator next_btn





    Examples:
      |  FName  | LName    |MName|  Email                 |Phone| Zip   |  StAddress1   | StAddress2  |City        |State|
      | Suresh | Simhadri | Kumar| simhadri@fnb-corp.com | 412-124-3212|15212 | 30 Isabella St | 6th Floor |Pittsburgh  |PA   |

  @regression_508
  Scenario: FNBSB-508_FE: Implement Co-Applicant Declined Page
    Then Stop Execution
    Then we verify on page personal of locator thankYouMessage is existed
    Then we verify on page personal of locator regreretMessage is existed
    Then we verify on page personal of locator contMessage is existed


  @regression_363
  Scenario Outline: FNBSB-363_FE: Implement Co-applicant Getting Started Page, FNBSB-112 -

    Then we verify on page personal of locator coAppGSHeading is existed
    Then we verify on page personal of locator coAppDescAboveProducts is existed
    Then we get list of elements on page personal locator productsDetails
    Then we verify on page personal of locator descBelowProducts is existed
    Then we verify on page personal of locator emailAddressLabel is existed
    Then we verify on page personal of locator phoneNumberLabel is enabled
    Then we verify on page personal of locator emailAddressText is enabled
    Then we verify on page personal of locator phoneNumberText is enabled
    Then we verify on page personal of locator ownershipHeading is enabled
    When we click on page personal locator businessRole
    Then we verify on page personal of locator president is existed
    Then we verify on page personal of locator vicePresident is existed
    Then we verify on page personal of locator assistantVicePresident is existed
    Then we verify on page personal of locator chairman is existed
    Then we verify on page personal of locator chiefExecutiveOffer is existed
    Then we verify on page personal of locator chiefFinancialOfficer is existed
    Then we verify on page personal of locator chiefOperatingOfficer is existed
    #Then we verify on page personal of locator chiefExcutiveVicePresident
    Then we verify on page personal of locator generalPartner is existed
    Then we verify on page personal of locator limitedPartner is existed
    Then we verify on page personal of locator manager is existed
    Then we verify on page personal of locator managingMember is existed
    Then we verify on page personal of locator member is existed
    Then we verify on page personal of locator owner is existed
    Then we verify on page personal of locator secretary is existed
    Then we verify on page personal of locator seniorVicePresident is existed
    Then we verify on page personal of locator tresurer is existed
    When we click on page personal locator chiefExecutiveOffer
    When we enter value on page personal locator dateofBirth element value "09/09/2024"
    Then we verify on page personal of locator prefilling is visible
    When we click on page personal locator prefilling
    Then we verify on page personal of locator dateofBirthError is existed
    When we enter value on page personal locator dateofBirth element value "09/09/1990"
    Then we verify on page personal of locator prefilling is existed
    When we click on page personal locator prefilCheckBox
    Then we verify on page personal of locator chkboxText is existed
    Then we verify on page personal of locator verifyYourIdentity is existed
    Then we verify on page personal of locator disclosureHeading is existed
    Then we verify on page personal of locator disclosureText is existed
    When we click on page personal locator next_btn
    Then we verify on page personal of locator acceptError is existed
    When we click on new page personal locator view_and_accept
    When we click on new page personal locator download
    When we click on new page personal locator canvas
    Then we press on new page personal locator body key "End" keyboard
    When we click on new page personal locator i_accept_btn
    Then we verify on new page personal of locator accepted is existed
    Then we verify on new page personal of locator noticeHeading is existed
    Then we verify on new page personal of locator noticeSubheading1 is existed
    Then we verify on new page personal of locator noticeContent1 is existed
    Then we verify on new page personal of locator noticeSubHeading2 is existed
    Then we verify on new page personal of locator noticeContent2 is existed
    Then we verify on new page personal of locator noticeFinalNote is existed
    Then we verify on new page personal of locator noticeInfo is existed


    Examples:
      |  FName  | LName    |MName|  Email                 |Phone| Zip   |  StAddress1   | StAddress2  |City        |State|
      | Suresh | Simhadri | Kumar| simhadri@fnb-corp.com | 412-124-3212|15212 | 30 Isabella St | 6th Floor |Pittsburgh  |PA   |

  @regression_367
  Scenario Outline: FNBSB-367_FE: Implement Co-Applicant Hard Stop Page
    When we click on new page personal locator FNBLogo
    Then we contain on new page personal of locator HardStopTitle value "<HardStopMsg1>"
    Then we contain on new page personal of locator HardStopText value "<HardStopMsg2>"

    Examples:
      | HardStopMsg1                | HardStopMsg2 |
      | Thank you for your interest | We regret we are unable to continue opening your account(s) online. |


