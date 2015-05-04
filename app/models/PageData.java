package models;

import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.LineItem;

public abstract class PageData {
    protected final Cart cart;
    protected final int frequency;

    PageData(final Cart cart, final int frequency) {
        this.cart = cart;
        this.frequency = frequency;
    }

    public VariantData variant() {
        return new VariantData(lineItem().getVariant());
    }

    public int frequency() {
        return frequency;
    }

    protected LineItem lineItem() {
        return cart.getLineItems().get(0);
    }
}
