Feature: Get detailed information about the items of my shopping-cart

  Scenario: After I finished my donut box selection, I get an detailed overview of my order, with information about the selected product and the delivery subscription
    Given I have selected a donut box and choosed a delivery interval
    When I press the View cart button
    Then I get an "Order details" page
    And I have the possibility to provide required shipping an billing information