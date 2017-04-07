package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.TaxCategoryDraft;
import io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand;
import io.sphere.sdk.taxcategories.queries.TaxCategoryQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;

import static io.sphere.sdk.client.SphereClientUtils.blockingWait;
import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;

public class TaxCategoryProvider implements Provider<TaxCategory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxCategoryProvider.class);
    private static final TaxCategoryDraft TAX_CATEGORY_DRAFT = readObjectFromResource("data/tax-category-draft.json", TaxCategoryDraft.class);

    private final SphereClient sphereClient;

    @Inject
    public TaxCategoryProvider(final SphereClient sphereClient) {
        this.sphereClient = sphereClient;
    }

    @Override
    public TaxCategory get() {
        LOGGER.debug("Providing tax category...");
        final TaxCategoryQuery query = TaxCategoryQuery.of().byName(TAX_CATEGORY_DRAFT.getName());
        return blockingWait(sphereClient.execute(query), Duration.ofSeconds(30))
                .head()
                .orElseGet(this::createTaxCategory);
    }

    private TaxCategory createTaxCategory() {
        LOGGER.debug("Tax category not found, creating tax category...");
        final TaxCategoryCreateCommand command = TaxCategoryCreateCommand.of(TAX_CATEGORY_DRAFT);
        return blockingWait(sphereClient.execute(command), Duration.ofSeconds(30));
    }
}
