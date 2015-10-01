package models;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.Attribute;
import io.sphere.sdk.products.attributes.AttributeAccess;
import utils.PriceUtils;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class OrderPageData extends Base {

    private final ProductVariant selectedVariant;
    private final int selectedFrequency;

    public OrderPageData(final ProductVariant selectedVariant, final int selectedFrequency) {
        this.selectedVariant = requireNonNull(selectedVariant);
        this.selectedFrequency = requireNonNull(selectedFrequency);
    }

    public VariantData selectedVariant() {
        return new VariantData(selectedVariant);
    }

    public String totalPrice() {
        return PriceUtils.format(selectedVariant.getPrices().get(0));
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
        final Attribute attribute = selectedVariant.getAttribute("pactas" + selectedFrequency);
        if(attribute == null) {
            throw new RuntimeException("Unable to access Pactas frequency Attribute");
        }
        final String pactasId = attribute.getValue(AttributeAccess.ofString());
        return pactasId;
    }

    public String currency() {
        return PriceUtils.currencyCode(price()).orElse("");
    }

    public double priceAmount() {
        return PriceUtils.monetaryAmount(price()).orElse(0d);
    }

    private Optional<Price> price() {
        return Optional.ofNullable(selectedVariant.getPrices().get(0));
    }
}
