package models;

import com.google.common.base.Optional;
import io.sphere.client.shop.model.Price;
import io.sphere.client.shop.model.Variant;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;

import static utils.PriceUtils.*;

public class NewOrderPageData {
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
        //return currencyCode(price()).or(""); //TODO
        return "";
    }

    public double priceAmount() {
        return 1d;
        //return monetaryAmount(price()).or(0d); //TODO
    }


    private java.util.Optional<io.sphere.sdk.products.Price> price() {
        return java.util.Optional.ofNullable(selectedVariant.getPrices().get(0));
    }

}
