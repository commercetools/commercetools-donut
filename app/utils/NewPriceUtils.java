package utils;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.Price;

import javax.money.MonetaryAmount;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public final class NewPriceUtils {

    private NewPriceUtils() {
    }

    public static String format(final Price price) {
        final MonetaryAmount amount = price.getValue();
        return doFormat(amount);
    }

    public static String format(final Cart cart) {
        final MonetaryAmount amount = cart.getTotalPrice();
        return doFormat(amount);
    }

    private static String doFormat(MonetaryAmount amount) {
        final String am = NumberFormat.getInstance(Locale.GERMANY).format(amount.getNumber().doubleValueExact());
        final String currency = amount.getCurrency().getCurrencyCode();
        return String.format("%s %s", am, currency);
    }

    public static double monetaryAmount(final Price price) {
        return price.getValue().getNumber().doubleValue();
    }

    private static String currencyCode(final Price price) {
        return price.getValue().getCurrency().getCurrencyCode();
    }

    public static Optional<Double> monetaryAmount(final Optional<Price> price) {
        if (price.isPresent()) {
            return Optional.of(monetaryAmount(price.get()));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> currencyCode(final Optional<Price> price) {
        if (price.isPresent()) {
            return Optional.of(currencyCode(price.get()));
        } else {
            return Optional.empty();
        }
    }
}
