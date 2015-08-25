Feature: Get overview of all available donut-boxes from the shop

  I can see a list of available donuts boxes with its individual box size and its prices

  Scenario: select a donut box
    When I go to the home page
    Then I see all available donut boxes with their prices
    And I see "6" donuts for "6.99" €
    And I see "12" donuts for "12.99" €
    And I see "24" donuts for "23.99 €" €
    And I see "36" donuts for "34.99 €" €
    When I click on the donut package
     And click on add to cart
