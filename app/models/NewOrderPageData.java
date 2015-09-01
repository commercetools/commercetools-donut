package models;

import io.sphere.client.shop.model.Cart;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import utils.NewPriceUtils;

import java.util.Optional;

public class NewOrderPageData extends Base {
    private final ProductVariant selectedVariant;
    private final int selectedFrequency;
    private final Cart cart;

    public NewOrderPageData(final ProductVariant selectedVariant, final int selectedFrequency, final Cart cart) {
        this.selectedVariant = selectedVariant;
        this.selectedFrequency = selectedFrequency;
        this.cart = cart;
    }

    public NewVariantData selectedVariant() {
        return new NewVariantData(selectedVariant);
    }

    public String totalPrice() {
        return cart.getTotalPrice().toString();
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
        final String pactasId = selectedVariant.getAttribute("pactas" + selectedFrequency).getValue(AttributeAccess.ofString());
        return pactasId;
    }

    public String currency() {
        final String currencyCode = NewPriceUtils.currencyCode(price()).orElse("");
        return currencyCode;
    }

    public double priceAmount() {
        final double monetaryAmount = NewPriceUtils.monetaryAmount(price()).orElse(0d);
        return monetaryAmount;
    }

    private java.util.Optional<Price> price() {
        final java.util.Optional<Price> price = Optional.ofNullable(selectedVariant.getPrices().get(0));
        return price;
    }

}
