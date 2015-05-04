package utils;

import io.sphere.client.model.Money;

import java.text.NumberFormat;
import java.util.Locale;

import static java.util.Currency.getInstance;

public class PriceUtils {

    private PriceUtils() {
    }

    public static String format(final Money money) {
        final String amount = NumberFormat.getInstance(Locale.GERMANY).format(money.getAmount());
        final String currency = getInstance(money.getCurrencyCode()).getSymbol(Locale.GERMANY);
        return amount + " " + currency;
    }
}
