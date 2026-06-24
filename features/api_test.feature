Feature: JSONPlaceholder API

  Scenario: Retrieve a specific blog post and verify its title
    Given I set the path parameter "postId" to "1"
    When I send a "jsonplaceholder_requests.get_single_post" request to the "jsonplaceholder" service
    Then the response code should be 200
    And the response header "content-type" should be "application/json; charset=utf-8"
    And the value of the JSON path "$.title" should be "sunt aut facere repellat provident occaecati excepturi optio reprehenderit"
    And the value of the JSON path "$.userId" should be "1"