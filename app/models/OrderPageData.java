package models;

import io.sphere.client.shop.model.Cart;

import static java.math.BigDecimal.ROUND_HALF_EVEN;
import static utils.PriceUtils.format;

public class OrderPageData extends PageData {

    public OrderPageData(final Cart cart, final int frequency) {
        super(cart, frequency);
    }

    public String totalPrice() {
        return format(cart.getTotalPrice());
    }

    public String frequencyName() {
        final String name;
        switch (frequency) {
            case 1: name = "ONCE A MONTH";
                break;
            case 2: name = "EVERY TWO WEEKS";
                break;
            case 4: name = "ONCE A WEEK";
                break;
            default: name = "UNKNOWN FREQUENCY";
        }
        return name;
    }

    public String pactasVariantId() {
        return lineItem().getVariant().getString("pactas" + frequency);
    }

    public String currency() {
        return lineItem().getPrice().getValue().getCurrencyCode();
    }

    public int priceAmount() {
        return lineItem().getPrice().getValue().getAmount().setScale(2, ROUND_HALF_EVEN).intValue();
    }

}
