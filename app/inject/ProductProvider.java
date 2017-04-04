package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductDraftBuilder;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductProjectionType;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.taxcategories.TaxCategory;

import javax.inject.Inject;
import java.util.Locale;

import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;

class ProductProvider implements Provider<ProductProjection> {

    private final BlockingSphereClient sphereClient;
    private final TaxCategory taxCategory;

    @Inject
    public ProductProvider(final BlockingSphereClient sphereClient, final TaxCategory taxCategory) {
        this.sphereClient = sphereClient;
        this.taxCategory = taxCategory;
    }

    @Override
    public ProductProjection get() {
        return sphereClient.executeBlocking(ProductProjectionQuery.ofCurrent().bySlug(Locale.ENGLISH, "donut-box"))
                .head()
                .orElseGet(this::createProduct);
    }

    private ProductProjection createProduct() {
        final ProductDraft productDraft = readObjectFromResource("data/product-draft.json", ProductDraft.class);
        final ProductDraft productDraftWithTaxCategory = ProductDraftBuilder.of(productDraft)
                .taxCategory(taxCategory)
                .publish(true)
                .build();
        return sphereClient.executeBlocking(ProductCreateCommand.of(productDraftWithTaxCategory))
                .toProjection(ProductProjectionType.CURRENT);
    }
}
