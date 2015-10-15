package models.wrapper;

import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.products.attributes.*;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import org.junit.Before;
import org.junit.Test;
import utils.JsonUtils;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ProductTypeDraftWrapperTest {

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";

    private ProductTypeDraft productTypeDraft;

    @Before
    public void setUp() {
        final ProductTypeDraftWrapper productTypeDraftWrapper =
                JsonUtils.readObjectFromResource(PRODUCT_TYPE_JSON_RESOURCE, ProductTypeDraftWrapper.class);
        assertThat(productTypeDraftWrapper).isNotNull();
        productTypeDraft = productTypeDraftWrapper.createProductTypeDraft();
    }

    @Test
    public void testCreateProductTypeDraft() throws Exception {
        assertThat(productTypeDraft).isNotNull();
        assertThat(productTypeDraft.getName()).isEqualTo("Donuts box");
        assertThat(productTypeDraft.getDescription()).isEqualTo("A box with delicious donuts");
        assertThat(productTypeDraft.getAttributes().size()).isEqualTo(5);
    }

    @Test
    public void testQuantityAttributeDefinitions() {
        checkAttributeAssertions("quantity", "Quantity", NumberType.of(), AttributeConstraint.COMBINATION_UNIQUE,
                TextInputHint.SINGLE_LINE, Boolean.TRUE, Boolean.TRUE);
    }

    @Test
    public void testBoxAttributeDefinitions() {
        checkAttributeAssertions("box", "Box name", StringType.of(), AttributeConstraint.COMBINATION_UNIQUE,
                TextInputHint.SINGLE_LINE, Boolean.TRUE, Boolean.TRUE);
    }

    @Test
    public void testPactasWeeklyAttributeDefinitions() {
        checkAttributeAssertions("pactas1", "Pactas ID weekly", StringType.of(), AttributeConstraint.UNIQUE,
                TextInputHint.SINGLE_LINE, Boolean.TRUE, Boolean.TRUE);
    }

    @Test
    public void testPactasTwoWeeklyAttributeDefinitions() {
        checkAttributeAssertions("pactas2", "Pactas ID two weeks", StringType.of(), AttributeConstraint.UNIQUE,
                TextInputHint.SINGLE_LINE, Boolean.TRUE, Boolean.TRUE);
    }

    @Test
    public void testPactasMonthlyAttributeDefinitions() {
        checkAttributeAssertions("pactas4", "Pactas ID monthly", StringType.of(), AttributeConstraint.UNIQUE,
                TextInputHint.SINGLE_LINE, Boolean.TRUE, Boolean.TRUE);
    }


    private void checkAttributeAssertions(final String assertName, final String assertLabel,
                                          final AttributeType assertType, final AttributeConstraint assertConstraint,
                                          final TextInputHint assertTextInputHint, final Boolean assertIsRequired,
                                          final Boolean assertIsSearchable) {
        final Optional<AttributeDefinition> optionalAttributeDefinition = productTypeDraft.getAttributes().stream()
                .filter(attributeDefinition -> assertName.equals(attributeDefinition.getName()))
                .findFirst();
        assertThat(optionalAttributeDefinition.isPresent());
        final AttributeDefinition attributeDefinition = optionalAttributeDefinition.get();
        assertThat(attributeDefinition.getLabel()).isEqualTo(LocalizedString.of(Locale.ENGLISH, assertLabel));
        assertThat(attributeDefinition.getAttributeType()).isEqualTo(assertType);
        assertThat(attributeDefinition.getAttributeConstraint()).isEqualTo(assertConstraint);
        assertThat(attributeDefinition.getInputHint()).isEqualTo(assertTextInputHint);
        assertThat(attributeDefinition.getIsRequired()).isEqualTo(assertIsRequired);
        assertThat(attributeDefinition.getIsSearchable()).isEqualTo(assertIsSearchable);
    }
}
