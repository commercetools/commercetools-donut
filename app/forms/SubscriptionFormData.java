package forms;

import io.sphere.sdk.models.Base;
import play.data.validation.Constraints;

public class SubscriptionFormData extends Base {

    @Constraints.Required(message = "Product required")
    private int variantId;

    @Constraints.Required(message = "Frequency required")
    @Constraints.Min(value = 1, message = "Frequency cannot be less than weekly")
    @Constraints.Max(value = 4, message = "Frequency cannot be more than monthly")
    private int howOften;

    public SubscriptionFormData() {
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(final int variantId) {
        this.variantId = variantId;
    }

    public int getHowOften() {
        return howOften;
    }

    public void setHowOften(final int howOften) {
        this.howOften = howOften;
    }
}
