package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductDraftBuilder;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductProjectionType;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.taxcategories.TaxCategory;

import javax.inject.Inject;
import java.util.Locale;

import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;

public class ProductProvider implements Provider<ProductProjection> {

    private final BlockingSphereClient sphereClient;
    private final TaxCategoryProvider taxCategoryProvider;
    private final ProductTypeProvider productTypeProvider;

    @Inject
    public ProductProvider(final BlockingSphereClient sphereClient, final TaxCategoryProvider taxCategoryProvider, final ProductTypeProvider productTypeProvider) {
        this.sphereClient = sphereClient;
        this.taxCategoryProvider = taxCategoryProvider;
        this.productTypeProvider = productTypeProvider;
    }

    @Override
    public ProductProjection get() {
        final TaxCategory taxCategory = taxCategoryProvider.get();
        final ProductType productType = productTypeProvider.get();
        return sphereClient.executeBlocking(ProductProjectionQuery.ofCurrent().bySlug(Locale.ENGLISH, "donut-box"))
                .head()
                .orElseGet(() -> createProduct(taxCategory, productType));
    }

    private ProductProjection createProduct(final TaxCategory taxCategory, final ProductType productType) {
        final ProductDraft productDraft = readObjectFromResource("data/product-draft.json", ProductDraft.class);
        final ProductDraft productDraftWithTaxCategory = ProductDraftBuilder.of(productDraft)
                .taxCategory(taxCategory)
                .publish(true)
                .build();
        return sphereClient.executeBlocking(ProductCreateCommand.of(productDraftWithTaxCategory))
                .toProjection(ProductProjectionType.CURRENT);
    }
}
