package models;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.Image;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.Attribute;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryFormats;
import java.util.Locale;
import java.util.Optional;

public class SubscriptionViewModel extends Base {

    private final ProductVariant productVariant;

    SubscriptionViewModel(final ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public int id() {
        return productVariant.getId();
    }

    public String price() {
        return findMonetaryAmount()
                .map(money -> MonetaryFormats.getAmountFormat(Locale.GERMANY).format(money))
                .orElse("");
    }

    public String currency() {
        return findMonetaryAmount()
                .map(money -> money.getCurrency().getCurrencyCode())
                .orElse("");
    }

    public double priceAmount() {
        return findMonetaryAmount()
                .map(money -> money.getNumber().doubleValue())
                .orElse(0d);
    }

    public String boxSize() {
        return Optional.ofNullable(productVariant.getAttribute("box"))
                .map(Attribute::getValueAsString)
                .orElse("");
    }

    public int quantity() {
        return Optional.ofNullable(productVariant.getAttribute("quantity"))
                .map(Attribute::getValueAsInteger)
                .orElse(0);
    }

    public String imageUrl() {
        return productVariant.getImages().stream()
                .findFirst()
                .map(Image::getUrl)
                .orElse("");
    }

    public String stampImageUrl() {
        if (productVariant.getImages().size() > 2) {
            final Image image = productVariant.getImages().get(1);
            return "background-image: url('"+ image.getUrl() +"')";
        }
        return "";
    }


    public String addToCartImageUrl() {
        if (productVariant.getImages().size() > 2) {
            final Image image = productVariant.getImages().get(2);
            return "background-image: url('"+ image.getUrl() +"')";
        }
        return "";
    }

    private Optional<MonetaryAmount> findMonetaryAmount() {
        return productVariant.getPrices().stream()
                .findFirst()
                .map(Price::getValue);
    }
}
