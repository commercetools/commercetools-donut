package utils;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.utils.MoneyImpl;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryFormats;
import java.util.Locale;
import java.util.Optional;

public final class PriceUtils {

    private PriceUtils() {
    }

    public static String format(final Price price) {
        return format(price.getValue());
    }

    public static String format(final Cart cart) {
        return format(cart.getTotalPrice());
    }

    private static String format(MonetaryAmount amount) {
        return MonetaryFormats.getAmountFormat(Locale.GERMANY).format(MoneyImpl.of(amount));
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
