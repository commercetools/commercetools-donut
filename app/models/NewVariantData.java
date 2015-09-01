package models;

import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.Image;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import utils.NewPriceUtils;

public class NewVariantData extends Base {
    private final ProductVariant productVariant;

    public NewVariantData(final ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public int id() {
        return productVariant.getId();
    }

    public String price() {
        final Price price = productVariant.getPrices().get(0); //
        if(price != null) {
            final String p = NewPriceUtils.format(price);
            return p;
        } else {
            return "";
        }
    }

    public String boxSize() {
        return productVariant.getAttribute("box").getValue(AttributeAccess.ofString());
    }

    public int quantity() {
        //FIXME this is just a workaround because there's no AttributeAccess for int values
        final JsonNode quantity = productVariant.getAttribute("quantity").getValue(AttributeAccess.ofJsonNode());
        return quantity.asInt();
    }


    public String imageUrl() {
        return productVariant.getImages().get(0).getUrl();
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
        return "";    }
}
