package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductProjection;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public interface CartService {

    Cart createOrGet(HttpSession session);

    Cart clearCart(final Cart cart);

    Optional<ProductProjection> getProduct();
}

