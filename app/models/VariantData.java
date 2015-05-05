package models;

import io.sphere.client.shop.model.ScaledImage;
import io.sphere.client.shop.model.Variant;

import static io.sphere.client.shop.model.ImageSize.*;
import static utils.PriceUtils.format;

public class VariantData {
    private final Variant variant;

    public VariantData(final Variant variant) {
        this.variant = variant;
    }

    public int id() {
        return variant.getId();
    }

    public String price() {
        if (variant.getPrice() != null) {
            return format(variant.getPrice().getValue());
        } else {
            return "";
        }
    }

    public String boxSize() {
        return variant.getString("box");
    }

    public int quantity() {
        return variant.getInt("quantity");
    }

    public String imageUrl() {
        return variant.getFeaturedImage().getSize(MEDIUM).getUrl();
    }

    public String stampImageUrl() {
        if (variant.getImages().size() > 2) {
            final ScaledImage image = variant.getImages().get(1).getSize(ORIGINAL);
            return "background-image: url('"+ image.getUrl() +"')";
        }
        return "";
    }

    public String addToCartImageUrl() {
        if (variant.getImages().size() > 2) {
            final ScaledImage image = variant.getImages().get(2).getSize(MEDIUM);
            return "background-image: url('"+ image.getUrl() +"')";
        }
        return "";
    }
}
