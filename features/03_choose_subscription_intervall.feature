Feature: Choose the delivery interval of the previously selected donut box

  After I have made a selection for a donut box, I have to choose between a
  delivery interval "once per week", "every two weeks" or "once a month"

  Scenario: I have selected a donut box and have to provide information about the interval,
    in which the donut box will be continuously delivered to me
    Given there's at least one donut box in my shopping cart
    When I select a delivery interval "once a week"
    Then this information will be stored in the cart, donuts will be delivered once a week
    When I select a a delivery interval "every two weeks"
    Then this information will be stored in the cart, donuts will be delivered every two weeks
    When I select a a delivery interval "every two weeks"
    Then this information will be stored in the cart, donuts will be delivered once a month