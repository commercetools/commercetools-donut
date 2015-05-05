package models;

import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.Variant;

import static utils.PriceUtils.currencyCode;
import static utils.PriceUtils.monetaryAmount;
import static utils.PriceUtils.format;

public class OrderPageData {
    private final Variant selectedVariant;
    private final int selectedFrequency;
    private final Cart cart;

    public OrderPageData(final Variant selectedVariant, final int selectedFrequency, final Cart cart) {
        this.selectedVariant = selectedVariant;
        this.selectedFrequency = selectedFrequency;
        this.cart = cart;
    }

    public VariantData selectedVariant() {
        return new VariantData(selectedVariant);
    }

    public String totalPrice() {
        return format(cart.getTotalPrice());
    }

    public String frequencyName() {
        final String name;
        switch (selectedFrequency) {
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
        return selectedVariant.getString("pactas" + selectedFrequency);
    }

    public String currency() {
        return currencyCode(selectedVariant.getPrice()).or("");
    }

    public int priceAmount() {
        return monetaryAmount(selectedVariant.getPrice()).or(0);
    }

}
