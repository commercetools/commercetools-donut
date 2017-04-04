package exceptions;

public final class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super("Product not found");
    }

    public ProductNotFoundException(final Throwable cause) {
        super(cause);
    }
}
