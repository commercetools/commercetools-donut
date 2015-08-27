package models;

import com.google.common.base.Optional;
import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.Price;
import io.sphere.client.shop.model.Variant;
import io.sphere.sdk.products.ProductVariant;

import static utils.PriceUtils.currencyCode;
import static utils.PriceUtils.monetaryAmount;
import static utils.PriceUtils.format;

public class OrderPageData {
    private final Variant selectedVariant;
    private final int selectedFrequency;
    private final Cart cart;

    private final ProductVariant selectedProductVariant;

    public OrderPageData(final Variant selectedVariant, final int selectedFrequency, final Cart cart, final ProductVariant selectedProductVariant) {
        this.selectedVariant = selectedVariant;
        this.selectedFrequency = selectedFrequency;
        this.cart = cart;
        this.selectedProductVariant = selectedProductVariant;
    }

    public VariantData selectedVariant() {
        return new VariantData(selectedVariant, selectedProductVariant);
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
        return currencyCode(price()).or("");
    }

    public double priceAmount() {
        return monetaryAmount(price()).or(0d);
    }

    private Optional<Price> price() {
        return Optional.fromNullable(selectedVariant.getPrice());
    }

}
