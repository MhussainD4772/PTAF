@db
Feature: User data management

  Scenario: Create and then delete a new user in the database
    Given the database does not contain a record for query "users.get_user_by_email" with parameters "new.user@test.com"
    When I insert a new record using query "users.insert_new_user" with parameters "newuser, new.user@test.com, test_password_hash"
    Then I verify the database contains a record for query "users.get_user_by_email" with parameters "new.user@test.com"
    When I delete 1 records using query "users.delete_user_by_email" with parameters "new.user@test.com"
    Then I verify the database does not contain a record for query "users.get_user_by_email" with parameters "new.user@test.com"