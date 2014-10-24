package exceptions;

public class SubscriptionProductNotFound extends RuntimeException {

    public SubscriptionProductNotFound() {
        super("Not found subscription product defined in configuration file");
    }
}
