package exceptions;

public class PlanVariantNotFound extends RuntimeException {

    public PlanVariantNotFound() {
        super("undefined");
    }

    public PlanVariantNotFound(final String planVariantId) {
        super("Not found plan variant ID in product: " + planVariantId);
    }
}
