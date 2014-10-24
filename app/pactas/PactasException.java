package pactas;

public class PactasException extends RuntimeException {

    public PactasException() {
    }

    public PactasException(String s) {
        super(s);
    }

    public PactasException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PactasException(Throwable throwable) {
        super(throwable);
    }

    public PactasException(int status, String body) {
        super(String.format("[%d] %s", status, body));
    }
}
