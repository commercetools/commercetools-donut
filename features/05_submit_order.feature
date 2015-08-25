Feature: Finishing the order

  Scenario: To make the order complete, I have to provide my billing and shipping information in the order details page
    Given I have selected a donut box und provided my personal billing and shipping information
    When I press the Confirm orde" button in the "Order details" page
    Then the order will be transfered to sphere.io
    And the billing information will be send to Pactas
    And I will receive a email with my order details