package models.wrapper;

import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.products.attributes.AttributeConstraint;
import io.sphere.sdk.products.attributes.AttributeDefinition;
import io.sphere.sdk.products.attributes.NumberType;
import io.sphere.sdk.products.attributes.StringType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import org.junit.Before;
import org.junit.Test;
import utils.JsonUtils;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ProductTypeDraftWrapperTest {

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";

    private ProductTypeDraftWrapper productTypeDraftWrapper;

    @Before
    public void setUp() {
        productTypeDraftWrapper = JsonUtils.readObjectFromResource(PRODUCT_TYPE_JSON_RESOURCE, ProductTypeDraftWrapper.class);
        assertThat(productTypeDraftWrapper).isNotNull();
    }

    @Test
    public void testCreateProductTypeDraft() throws Exception {
        final ProductTypeDraft productTypeDraft = productTypeDraftWrapper.createProductTypeDraft();
        assertThat(productTypeDraft).isNotNull();
        assertThat(productTypeDraft.getName()).isEqualTo("Donuts box");
        assertThat(productTypeDraft.getDescription()).isEqualTo("A box with delicious donuts");
        assertThat(productTypeDraft.getAttributes().size()).isEqualTo(5);
    }

    @Test
    public void testQuantityAttributeDefinitions() {
        final ProductTypeDraft productTypeDraft = productTypeDraftWrapper.createProductTypeDraft();
        final Optional<AttributeDefinition> optionalAttributeDefinition = productTypeDraft.getAttributes().stream()
                .filter(attributeDefinition -> "quantity".equals(attributeDefinition.getName()))
                .findFirst();
        assertThat(optionalAttributeDefinition.isPresent());
        final AttributeDefinition attributeDefinition = optionalAttributeDefinition.get();

        assertThat(attributeDefinition.getName()).isEqualTo("quantity");
        assertThat(attributeDefinition.getLabel()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "Quantity"));
        assertThat(attributeDefinition.getAttributeType()).isEqualTo(NumberType.of());
        assertThat(attributeDefinition.getAttributeConstraint()).isEqualTo(AttributeConstraint.COMBINATION_UNIQUE);
        assertThat(attributeDefinition.getInputHint()).isEqualTo(TextInputHint.SINGLE_LINE);
        assertThat(attributeDefinition.getIsRequired()).isEqualTo(Boolean.TRUE);
        assertThat(attributeDefinition.getIsSearchable()).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void testBoxAttributeDefinitions() {
        final ProductTypeDraft productTypeDraft = productTypeDraftWrapper.createProductTypeDraft();
        final Optional<AttributeDefinition> optionalAttributeDefinition = productTypeDraft.getAttributes().stream()
                .filter(attributeDefinition -> "box".equals(attributeDefinition.getName()))
                .findFirst();
        assertThat(optionalAttributeDefinition.isPresent());
        final AttributeDefinition attributeDefinition = optionalAttributeDefinition.get();

        assertThat(attributeDefinition.getName()).isEqualTo("box");
        assertThat(attributeDefinition.getLabel()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "Box name"));
        assertThat(attributeDefinition.getAttributeType()).isEqualTo(StringType.of());
        assertThat(attributeDefinition.getAttributeConstraint()).isEqualTo(AttributeConstraint.COMBINATION_UNIQUE);
        assertThat(attributeDefinition.getInputHint()).isEqualTo(TextInputHint.SINGLE_LINE);
        assertThat(attributeDefinition.getIsRequired()).isEqualTo(Boolean.TRUE);
        assertThat(attributeDefinition.getIsSearchable()).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void testPactasWeeklyAttributeDefinitions() {
        final ProductTypeDraft productTypeDraft = productTypeDraftWrapper.createProductTypeDraft();
        final Optional<AttributeDefinition> optionalAttributeDefinition = productTypeDraft.getAttributes().stream()
                .filter(attributeDefinition -> "pactas1".equals(attributeDefinition.getName()))
                .findFirst();
        assertThat(optionalAttributeDefinition.isPresent());
        final AttributeDefinition attributeDefinition = optionalAttributeDefinition.get();

        assertThat(attributeDefinition.getName()).isEqualTo("pactas1");
        assertThat(attributeDefinition.getLabel()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "Pactas ID weekly"));
        assertThat(attributeDefinition.getAttributeType()).isEqualTo(StringType.of());
        assertThat(attributeDefinition.getAttributeConstraint()).isEqualTo(AttributeConstraint.UNIQUE);
        assertThat(attributeDefinition.getInputHint()).isEqualTo(TextInputHint.SINGLE_LINE);
        assertThat(attributeDefinition.getIsRequired()).isEqualTo(Boolean.TRUE);
        assertThat(attributeDefinition.getIsSearchable()).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void testPactasTwoWeeklyAttributeDefinitions() {
        final ProductTypeDraft productTypeDraft = productTypeDraftWrapper.createProductTypeDraft();
        final Optional<AttributeDefinition> optionalAttributeDefinition = productTypeDraft.getAttributes().stream()
                .filter(attributeDefinition -> "pactas2".equals(attributeDefinition.getName()))
                .findFirst();
        assertThat(optionalAttributeDefinition.isPresent());
        final AttributeDefinition attributeDefinition = optionalAttributeDefinition.get();

        assertThat(attributeDefinition.getName()).isEqualTo("pactas2");
        assertThat(attributeDefinition.getLabel()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "Pactas ID two weeks"));
        assertThat(attributeDefinition.getAttributeType()).isEqualTo(StringType.of());
        assertThat(attributeDefinition.getAttributeConstraint()).isEqualTo(AttributeConstraint.UNIQUE);
        assertThat(attributeDefinition.getInputHint()).isEqualTo(TextInputHint.SINGLE_LINE);
        assertThat(attributeDefinition.getIsRequired()).isEqualTo(Boolean.TRUE);
        assertThat(attributeDefinition.getIsSearchable()).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void testPactasMonthlyAttributeDefinitions() {
        final ProductTypeDraft productTypeDraft = productTypeDraftWrapper.createProductTypeDraft();
        final Optional<AttributeDefinition> optionalAttributeDefinition = productTypeDraft.getAttributes().stream()
                .filter(attributeDefinition -> "pactas4".equals(attributeDefinition.getName()))
                .findFirst();
        assertThat(optionalAttributeDefinition.isPresent());
        final AttributeDefinition attributeDefinition = optionalAttributeDefinition.get();

        assertThat(attributeDefinition.getName()).isEqualTo("pactas4");
        assertThat(attributeDefinition.getLabel()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "Pactas ID monthly"));
        assertThat(attributeDefinition.getAttributeType()).isEqualTo(StringType.of());
        assertThat(attributeDefinition.getAttributeConstraint()).isEqualTo(AttributeConstraint.UNIQUE);
        assertThat(attributeDefinition.getInputHint()).isEqualTo(TextInputHint.SINGLE_LINE);
        assertThat(attributeDefinition.getIsRequired()).isEqualTo(Boolean.TRUE);
        assertThat(attributeDefinition.getIsSearchable()).isEqualTo(Boolean.TRUE);
    }
}
