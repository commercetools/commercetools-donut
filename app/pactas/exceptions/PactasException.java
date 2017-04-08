package pactas.exceptions;

public class PactasException extends RuntimeException {

    public PactasException(int status, String body) {
        super(String.format("[%d] %s", status, body));
    }
}
