package utils;

import io.sphere.sdk.products.Price;

import java.text.NumberFormat;
import java.util.Locale;

import static java.util.Currency.getInstance;

public final class NewPriceUtils {

    private NewPriceUtils() {
    }

    public static String format(final Price price) {
        final String amount = NumberFormat.getInstance(Locale.GERMANY).format(price.getValue().getNumber().doubleValue());
        final String currency = getInstance(price.getCountry().getCurrency().getCurrencyCode()).getSymbol(Locale.GERMANY);
        return amount + " " + currency;
    }

    public static double monetaryAmount(final Price price) {
        return price.getValue().getNumber().doubleValue();
    }

    private static String currencyCode(final Price price) {
        return price.getCountry().getCurrency().getCurrencyCode();
    }

    public static java.util.Optional<Double> monetaryAmount(final java.util.Optional<Price> price) {
        if (price.isPresent()) {
            return java.util.Optional.of(monetaryAmount(price.get()));
        } else {
            return java.util.Optional.empty();
        }
    }

    public static java.util.Optional<String> currencyCode(final java.util.Optional<Price> price) {
        if (price.isPresent()) {
            return java.util.Optional.of(currencyCode(price.get()));
        } else {
            return java.util.Optional.empty();
        }
    }
}