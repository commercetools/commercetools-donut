Feature: Add a donut box to my shopping cart

  I can select out of available donut boxes

  Scenario: choose a donut box
    When I select a donut box with "6" items for "6.99"
    Then a box with "6" items for "6.99" is stored in my cart
    When I select a donut box with "12" items for "12.99"
    Then a box with "12" items for "12.99" is stored in my cart
    When I select a donut box with "24" items for "23.99"
    Then a box with "24" items for "23.99" is stored in my cart
    When I select a donut box with "36" items for "34.99"
    Then a box with "36" items for "34.99" is stored iin my cart