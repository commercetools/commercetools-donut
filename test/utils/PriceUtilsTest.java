package utils;

import io.sphere.sdk.products.Price;
import org.javamoney.moneta.Money;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.PriceUtils.*;

public class PriceUtilsTest {
    public static final Optional<Price> PRICE = price(10.56, "EUR");
    public static final Optional<Price> NO_PRICE = Optional.empty();

    @Test
    public void getsMonetaryAmount() throws Exception {
        assertThat(monetaryAmount(PRICE).get()).isEqualTo(10.56);
    }

    @Test
    public void absentMonetaryAmountWhenNoPrice() throws Exception {
        assertThat(monetaryAmount(NO_PRICE).isPresent()).isFalse();
    }

    @Test
    public void getsCurrencyCode() throws Exception {
        assertThat(currencyCode(PRICE).get()).isEqualTo("EUR");
    }

    @Test
    public void absentCurrencyCodeWhenNoPrice() throws Exception {
        assertThat(currencyCode(NO_PRICE).isPresent()).isFalse();
    }

    @Test
    public void formatsPrice() throws Exception {
        assertThat(format(PRICE.get())).isEqualTo("10,56 EUR");
    }

    @Test
    public void formatsMonetaryAmount() throws Exception {
        assertThat(format(PRICE.get())).isEqualTo("10,56 EUR");
    }

    private static Optional<Price> price(final double amount, final String currency) {
        return Optional.of(Price.of(Money.of(BigDecimal.valueOf(amount), currency)));
    }
}
