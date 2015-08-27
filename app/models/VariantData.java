package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sphere.client.shop.model.ScaledImage;
import io.sphere.client.shop.model.Variant;
import io.sphere.sdk.products.Image;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import utils.PriceUtils;

import javax.money.MonetaryAmount;

import java.util.List;

import static io.sphere.client.shop.model.ImageSize.*;
import static utils.PriceUtils.format;

public class VariantData {
    private final Variant variant;
    private final ProductVariant productVariant;

    public VariantData(final Variant variant, final ProductVariant productVariant) {
        this.variant = variant;
        this.productVariant = productVariant;
    }

    public int id() {
        return variant.getId();
    }

    public int _id() {
        return productVariant.getId();
    }

    public String price() {
        if (variant.getPrice() != null) {
            return format(variant.getPrice().getValue());
        } else {
            return "";
        }
    }

    public String _price() {
        final Price price = productVariant.getPrices().get(0);
        if(price != null) {
            return price.getValue().toString();
        } else {
            return "";
        }
    }

    public String boxSize() {
        return variant.getString("box");
    }

    public String _boxSize() {
        return productVariant.getAttribute("box").getValue(AttributeAccess.ofString());
    }

    public int quantity() {
        return variant.getInt("quantity");
    }

    public int _quantity() {
        //FIXME this is just a workaround because there's no AttributeAccess for int values
        final JsonNode quantity = productVariant.getAttribute("quantity").getValue(AttributeAccess.ofJsonNode());
        return quantity.asInt();
    }

    public String imageUrl() {
        return variant.getFeaturedImage().getSize(MEDIUM).getUrl();
    }

//    public String _imageUrl() {
//        return productVariant.getImages().get(0).getSize(MEDIUM).getUrl();
//    }



    public String stampImageUrl() {
        if (variant.getImages().size() > 2) {
            final ScaledImage image = variant.getImages().get(1).getSize(ORIGINAL);
            return "background-image: url('"+ image.getUrl() +"')";
        }
        return "";
    }

//    public String _stampImageUrl() {
//        if (productVariant.getImages().size() > 2) {
//            final ScaledImage image = productVariant.getImages().get(1).getSize(ORIGINAL);
//            return "background-image: url('"+ image.getUrl() +"')";
//        }
//        return "";
//    }

    public String addToCartImageUrl() {
        if (variant.getImages().size() > 2) {
            final ScaledImage image = variant.getImages().get(2).getSize(MEDIUM);
            return "background-image: url('"+ image.getUrl() +"')";
        }
        return "";
    }

//    public String _addToCartImageUrl() {
//        if (productVariant.getImages().size() > 2) {
//            final ScaledImage image = productVariant.getImages().get(2).getSize(MEDIUM);
//            return "background-image: url('"+ image.getUrl() +"')";
//        }
//        return "";
//    }
}
