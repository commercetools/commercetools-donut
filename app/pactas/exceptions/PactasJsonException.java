package pactas.exceptions;

/**
 * Exception concerning JSON.
 */
public class PactasJsonException extends RuntimeException {
    private static final long serialVersionUID = 0L;

    public PactasJsonException(final Throwable cause) {
        super(cause);
    }

    public PactasJsonException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
