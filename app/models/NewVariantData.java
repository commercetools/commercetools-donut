package models;

import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;

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
            return price.getValue().toString();
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
        return ""; //TODO
    }

    public String stampImageUrl() {
        return ""; //TODO
    }


    public String addToCartImageUrl() {
        return ""; //TODO
    }
}
