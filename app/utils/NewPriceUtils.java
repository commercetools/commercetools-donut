package utils;

import io.sphere.sdk.products.Price;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public final class NewPriceUtils {

    private NewPriceUtils() {
    }

    public static String format(final Price price) {
        final String amount = NumberFormat.getInstance(Locale.GERMANY).format(price.getValue().getNumber().doubleValueExact());
        final String currency = price.getValue().getCurrency().getCurrencyCode();
        return String.format("%s %s", amount, currency);
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

    public static java.util.Optional<String> currencyCode(final Optional<Price> price) {
        if (price.isPresent()) {
            return Optional.of(currencyCode(price.get()));
        } else {
            return Optional.empty();
        }
    }
}
