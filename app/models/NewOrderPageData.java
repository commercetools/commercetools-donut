package models;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;

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
        return selectedVariant.getAttribute("pactas" + selectedFrequency).getValue(AttributeAccess.ofString());
    }

    public String currency() {
        return price().get().getCountry().getCurrency().getCurrencyCode(); //TODO do that in PriceUtils
        //return currencyCode(price()).or("");
    }

    public double priceAmount() {
        return price().get().getValue().getNumber().doubleValue(); //TODO do that in PriceUtils
        //return monetaryAmount(price()).or(0d);
    }


    private java.util.Optional<Price> price() {
        return Optional.ofNullable(selectedVariant.getPrices().get(0));
    }

}
