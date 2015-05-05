package utils;

import com.google.common.base.Optional;
import io.sphere.client.model.Money;
import io.sphere.client.shop.model.Price;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static utils.PriceUtils.currencyCode;
import static utils.PriceUtils.format;
import static utils.PriceUtils.monetaryAmount;

public class PriceUtilsTest {
    public static final Optional<Price> PRICE = price(10.56, "EUR");
    public static final Optional<Price> NO_PRICE = Optional.absent();

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
        assertThat(format(PRICE).get()).isEqualTo("10,56 €");
    }

    @Test
    public void absentFormatWhenNoPrice() throws Exception {
        assertThat(format(NO_PRICE).isPresent()).isFalse();
    }

    private static Optional<Price> price(final double amount, final String currency) {
        final Money money = new Money(BigDecimal.valueOf(amount), currency);
        return Optional.of(new Price(money, null, null));
    }
}
