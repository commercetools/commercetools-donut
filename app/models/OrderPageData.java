package models;

import donut.exceptions.PlanVariantNotFoundException;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.Attribute;

import java.util.Optional;

public class OrderPageData extends Base {

    private final ProductVariant variant;
    private final int frequency;
    private final String pactasPublicKey;

    OrderPageData(final ProductVariant variant, final int frequency, final String pactasPublicKey) {
        this.variant = variant;
        this.frequency = frequency;
        this.pactasPublicKey = pactasPublicKey;
    }

    public SubscriptionViewModel subscription() {
        return new SubscriptionViewModel(variant);
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
        final String planId = "pactas" + frequency;
        return Optional.ofNullable(variant.getAttribute(planId))
                .map(Attribute::getValueAsString)
                .orElseThrow(() -> new PlanVariantNotFoundException(planId));
    }

    public String pactasPublicKey() {
        return pactasPublicKey;
    }
}
