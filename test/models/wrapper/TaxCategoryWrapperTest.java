package models.wrapper;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.taxcategories.TaxCategoryDraft;
import io.sphere.sdk.taxcategories.TaxRate;
import org.junit.Test;
import utils.JsonUtils;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class TaxCategoryWrapperTest {

    private static final String TAX_CATEGORY_JSON_RESOURCE = "data/tax-category-draft.json";

    @Test
    public void testCreateTaxCategoryDraft() {
        final TaxCategoryWrapper taxCategoryWrapper = JsonUtils.readObjectFromResource(TAX_CATEGORY_JSON_RESOURCE,
                TaxCategoryWrapper.class);
        assertThat(taxCategoryWrapper).isNotNull();
        final TaxCategoryDraft taxCategoryDraft = taxCategoryWrapper.createTaxCategoryDraft();
        assertThat(taxCategoryDraft).isNotNull();
        assertThat(taxCategoryDraft.getName()).isEqualTo("Standard tax category");
        assertThat(taxCategoryDraft.getTaxRates().size()).isEqualTo(1);
        final TaxRate taxRate = taxCategoryDraft.getTaxRates().get(0);
        assertThat(taxRate.getAmount()).isEqualTo(0.19);
        assertThat(taxRate.getName()).isEqualTo("19% MwSt");
        assertThat(taxRate.getCountry()).isEqualTo(CountryCode.DE);
    }
}
