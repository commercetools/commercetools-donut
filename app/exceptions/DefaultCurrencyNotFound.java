package exceptions;

public class DefaultCurrencyNotFound extends RuntimeException {

    public DefaultCurrencyNotFound() {
        super("No valid currency defined in configuration file");
    }
}
