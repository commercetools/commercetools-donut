package utils;

import com.google.common.base.Optional;
import io.sphere.client.model.Money;
import io.sphere.client.shop.model.Price;

import java.text.NumberFormat;
import java.util.Locale;

import static java.math.BigDecimal.ROUND_HALF_EVEN;
import static java.util.Currency.getInstance;

public final class PriceUtils {

    private PriceUtils() {
    }

    public static String format(final Money money) {
        final String amount = NumberFormat.getInstance(Locale.GERMANY).format(money.getAmount());
        final String currency = getInstance(money.getCurrencyCode()).getSymbol(Locale.GERMANY);
        return amount + " " + currency;
    }

    public static int monetaryAmount(final Money money) {
        return money.getAmount().setScale(2, ROUND_HALF_EVEN).intValue();
    }

    private static String currencyCode(final Money money) {
        return money.getCurrencyCode();
    }

    public static Optional<String> format(final Price price) {
        if (price != null) {
            return Optional.of(format(price.getValue()));
        } else {
            return Optional.absent();
        }
    }

    public static Optional<Integer> monetaryAmount(final Price price) {
        if (price != null) {
            return Optional.of(monetaryAmount(price.getValue()));
        } else {
            return Optional.absent();
        }
    }

    public static Optional<String> currencyCode(final Price price) {
        if (price != null) {
            return Optional.of(currencyCode(price.getValue()));
        } else {
            return Optional.absent();
        }
    }
}
