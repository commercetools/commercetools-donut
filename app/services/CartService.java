package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.VariantIdentifier;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.libs.F;
import play.mvc.Http;

import java.util.Optional;

/**
 * Business service that provides access to the SPHERE.IO Cart API.
 */
public interface CartService {

    /**
     * If there is no {@code SessionKeys.CART_ID} in the existing session, a new {@code Cart} will be created in the
     * Sphere backend and its {@code SessionKeys.CART_ID} will be stored in the users {@code Http.Session session}.
     * Otherwise it retrieves an existing Cart via its {@code SessionKeys.CART_ID}, but never null.
     *
     * @param session the Play session object, must not be null
     * @return the newly created, or fetched {@code Cart} object, must not be null
     */
    F.Promise<Cart> getOrCreateCart(Http.Session session);

    /**
     * Resets an existing {@code Cart}.
     * It performs two API calls, one to remove the Carts LineItems and another one to clear the {@code CustomObject}
     * that holds the selected frequency of the product.
     *
     * @param cart the {@code Cart} object to clear, must not be null
     * @return the updated {@code Cart} object as result from the update command to Sphere API, must not be null
     */
    F.Promise<Cart> clearCart(final Cart cart);

    /**
     * Adds the user selected {@code ProductVariant} of the {@code ProductProjection} with a given frequency to the
     * users {@code Cart}.
     *
     * @param cart the user's current {@code Cart} object, must not be null
     * @param variantIdentifier the {@code VariantIdentifier} to add, must not be null
     * @param frequency the selected delivery frequency
     */
    F.Promise<Cart> setProductToCart(final Cart cart, final VariantIdentifier variantIdentifier,
                                     final Integer frequency);

    /**
     * Returns the value of the {@code CustomObject} named PactasKeys.FREQUENCY, that is bound to the {@code Cart}
     * with the given cardId.
     *
     * @param cartId the identifier of the {@code Cart} object, the {@code CustomObject} is bound to
     * @return the value of the {@code CustomObject}. If no {@code CustomObject} found, it returns 0
     */
    F.Promise<Integer> getFrequency(final String cartId);

    /**
     * Gets an optional, selected {@code ProductVariant} from the users {@code Cart}
     *
     * @param cart the {@code Cart} object to get the selected variant from, must not be null
     * @return optional {@code ProductVariant}, maybe empty if there's no selection made
     */
    Optional<ProductVariant> getSelectedVariantFromCart(final Cart cart);

    /**
     * Creates a {@code Cart} object with the required data from Pactas.
     *
     * @param product the selected  {@code ProductProjection}
     * @param contract the {@code PactasContract}
     * @param customer the {@code PactasCustomer}
     * @return
     */
    F.Promise<Cart> createCartWithPactasInfo(final ProductProjection product, final PactasContract contract, final PactasCustomer customer);

    F.Promise<Cart> deleteCart(final Cart cart);
}