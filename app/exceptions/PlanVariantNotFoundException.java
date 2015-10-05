package exceptions;

public class PlanVariantNotFoundException extends RuntimeException {

    public PlanVariantNotFoundException() {
        super("undefined");
    }

    public PlanVariantNotFoundException(final String planVariantId) {
        super("Not found plan variant ID in product: " + planVariantId);
    }
}
