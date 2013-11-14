package forms;

import play.data.validation.Constraints;

public class AddToCart {

    @Constraints.Required(message = "Product required")
    public int variantId;

    @Constraints.Required(message = "Frequency required")
    @Constraints.Min(value = 1, message = "Frequency cannot be less than weekly")
    @Constraints.Max(value = 4, message = "Frequency cannot be more than monthly")
    public int howOften;


    public AddToCart() {

    }

}
