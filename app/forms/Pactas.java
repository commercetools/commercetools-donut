package forms;

import play.data.validation.Constraints;

public class Pactas {

    @Constraints.Required(message = "Product required")
    public String productId;

    @Constraints.Required(message = "Variant required")
    public String variantId;

    @Constraints.Required(message = "Quantity required")
    @Constraints.Min(1)
    public int quantity;

    @Constraints.Required(message = "Customer required")
    public String customerId;


    public Pactas() {
    }

}
