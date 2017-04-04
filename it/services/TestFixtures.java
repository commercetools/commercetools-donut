package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.CartDraftDsl;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.ConcurrentModificationException;
import io.sphere.sdk.client.NotFoundException;
import io.sphere.sdk.client.SphereRequest;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.ResourceIdentifiable;
import io.sphere.sdk.models.Versioned;
import io.sphere.sdk.products.*;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.commands.ProductDeleteCommand;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.ProductTypeDraftBuilder;
import io.sphere.sdk.producttypes.ProductTypeDraftDsl;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.commands.ProductTypeDeleteCommand;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class TestFixtures {

    private static final int DEFAULT_DELETE_TTL = 5;
    private static final Random random = new Random();

    public static String randomKey() {
        return  "random-slug-" + random.nextInt();
    }

    public static String randomString() {
        return "random string " + random.nextInt() + System.currentTimeMillis();
    }

    public static LocalizedString randomLocalizedString() {
        return LocalizedString.ofEnglish(randomString());
    }

    public static void withProductType(final BlockingSphereClient client, final ProductTypeDraft productTypeDraft, final Function<ProductType, ProductType> test) {
        final ProductType productType = client.executeBlocking(ProductTypeCreateCommand.of(productTypeDraft));
        final ProductType productTypeAfterTest = test.apply(productType);
        deleteProductTypeWithRetry(client, productTypeAfterTest);
    }

    public static void withProduct(final BlockingSphereClient client, final ProductDraft productDraft, final Function<ProductProjection, ProductProjection> test) {
        final Product product = client.executeBlocking(ProductCreateCommand.of(productDraft));
        final ProductProjection productAfterTest = test.apply(product.toProjection(ProductProjectionType.STAGED));
        deleteProductWithRetry(client, productAfterTest);
    }

    public static void withDefaultProduct(final BlockingSphereClient client, final Function<ProductProjection, ProductProjection> test) {
        withProductType(client, productTypeDraft(), productType -> {
            withProduct(client, productDraft(productType, singletonList(productVariantDraft())), test);
            return productType;
        });
    }

    public static void withCart(final BlockingSphereClient client, final CartDraft cartDraft, final Function<Cart, Cart> test) {
        final Cart cart = client.executeBlocking(CartCreateCommand.of(cartDraft));
        final Cart cartAfterTest = test.apply(cart);
        deleteCartWithRetry(client, cartAfterTest);
    }

    public static CartDraftDsl cartDraft() {
        return CartDraft.of(DefaultCurrencyUnits.EUR);
    }

    public static ProductTypeDraftDsl productTypeDraft() {
        return ProductTypeDraftBuilder.of(randomKey(), randomString(), randomString(), emptyList()).build();
    }

    public static ProductDraft productDraft(final ResourceIdentifiable<ProductType> productType, final List<ProductVariantDraft> productVariantDrafts) {
        return ProductDraftBuilder.of(productType, randomLocalizedString(), randomLocalizedString(), productVariantDrafts).build();
    }

    public static ProductVariantDraft productVariantDraft() {
        return ProductVariantDraftBuilder.of().build();
    }

    public static void deleteProductTypeWithRetry(final BlockingSphereClient client, final ProductType productTypeAfterTest) {
        deleteWithRetry(client, productTypeAfterTest, ProductTypeDeleteCommand::of, DEFAULT_DELETE_TTL);
    }

    public static void deleteProductWithRetry(final BlockingSphereClient client, final ProductProjection productAfterTest) {
        deleteWithRetry(client, productAfterTest, ProductDeleteCommand::of, DEFAULT_DELETE_TTL);
    }

    public static void deleteCartWithRetry(final BlockingSphereClient client, final Cart cartAfterTest) {
        deleteWithRetry(client, cartAfterTest, CartDeleteCommand::of, DEFAULT_DELETE_TTL);
    }

    private static <T> void deleteWithRetry(final BlockingSphereClient client, final Versioned<T> resource,
                                            final Function<Versioned<T>, SphereRequest<T>> deleteFunction, final int ttl) {
        if (ttl > 0) {
            try {
                client.executeBlocking(deleteFunction.apply(resource));
            } catch (final ConcurrentModificationException e) {
                if (e.getCurrentVersion() != null) {
                    final Versioned<T> resourceWithCurrentVersion = Versioned.of(resource.getId(), e.getCurrentVersion());
                    deleteWithRetry(client, resourceWithCurrentVersion, deleteFunction, ttl - 1);
                }
            } catch (final NotFoundException e) {
                // mission indirectly accomplished
            }
        } else {
            throw new RuntimeException("Could not delete resource due to too many concurrent updates, resource: " + resource);
        }
    }
}
