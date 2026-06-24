@api
Feature: API Test for Blog Post Management on JSONPlaceholder

  Scenario: Create, retrieve, and delete a new blog post
    # Part 1: Create a new blog post using POST
    Given I set the request header "Content-type" to "application/json; charset=UTF-8"
    And I set the request body to
      """
      {
        "title": "My Awesome Post",
        "body": "This is the body of the new post.",
        "userId": 1
      }
      """
    When I send a "jsonplaceholder_requests.create_post" request to the "jsonplaceholder" service
    Then the response code should be 201
    And the value of the JSON path "$.title" should be "My Awesome Post"
    And the value of the JSON path "$.id" should be "101"

    # Part 2: Verify retrieval on an existing post (JSONPlaceholder does not persist POST data)
    Given I set the path parameter "postId" to "1"
    When I send a "jsonplaceholder_requests.get_single_post" request to the "jsonplaceholder" service
    Then the response code should be 200
    And the value of the JSON path "$.userId" should be "1"

    # Part 3: Clean up using DELETE (mock API always returns 200)
    Given I set the path parameter "postId" to "101"
    When I send a "jsonplaceholder_requests.delete_post" request to the "jsonplaceholder" service
    Then the response code should be 200
