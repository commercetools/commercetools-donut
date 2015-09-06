package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import play.mvc.Http;

import java.util.Optional;

public interface CartService {

    Cart createOrGet(Http.Session session);

    Cart clearCart(final Cart cart);

    void setProductToCart(final Cart cart, final ProductProjection product, final ProductVariant variant, final int frequency);

    int getFrequency(final String cartId);

    Optional<ProductProjection> getProduct();


}

