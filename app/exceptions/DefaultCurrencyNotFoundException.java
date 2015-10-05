package exceptions;

public class DefaultCurrencyNotFoundException extends RuntimeException {

    public DefaultCurrencyNotFoundException() {
        super("No valid currency defined in configuration file");
    }
}
