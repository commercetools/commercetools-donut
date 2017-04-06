package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.TaxCategoryDraft;
import io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand;
import io.sphere.sdk.taxcategories.queries.TaxCategoryQuery;

import javax.inject.Inject;

import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;

public class TaxCategoryProvider implements Provider<TaxCategory> {

    private final BlockingSphereClient sphereClient;

    @Inject
    public TaxCategoryProvider(final BlockingSphereClient sphereClient) {
        this.sphereClient = sphereClient;
    }

    @Override
    public TaxCategory get() {
        return sphereClient.executeBlocking(TaxCategoryQuery.of().byName("standard"))
                .head()
                .orElseGet(this::createTaxCategory);
    }

    private TaxCategory createTaxCategory() {
        final TaxCategoryDraft taxCategoryDraft = readObjectFromResource("data/tax-category-draft.json", TaxCategoryDraft.class);
        return sphereClient.executeBlocking(TaxCategoryCreateCommand.of(taxCategoryDraft));
    }
}
