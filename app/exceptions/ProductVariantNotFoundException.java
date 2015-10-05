package exceptions;

public final class ProductVariantNotFoundException extends RuntimeException {

    public ProductVariantNotFoundException() {
        super("ProductVariant not found");
    }
}
