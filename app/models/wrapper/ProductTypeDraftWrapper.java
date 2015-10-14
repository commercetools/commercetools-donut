package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.products.attributes.*;
import io.sphere.sdk.producttypes.ProductTypeDraft;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class ProductTypeDraftWrapper extends Base {

    private final String name;
    private final String description;
    private final List<AttributeDefinitionWrapper> attributes;

    public ProductTypeDraftWrapper(@JsonProperty("name") final String name,
                                   @JsonProperty("description") final String description,
                                   @JsonProperty("attributes") final List<AttributeDefinitionWrapper> attributes) {
        this.name = requireNonNull(name);
        this.description = requireNonNull(description);
        this.attributes = requireNonNull(attributes);
    }

    public ProductTypeDraft createProductTypeDraft() {
        return ProductTypeDraft.of(name, description, Arrays.asList(quantityAttributeDefinition(), boxAttributeDefinition(),
                pactasMonthlyAttributeDefinition(), pactasTwoWeeklyAttributeDefinition(), pactasWeeklyAttributeDefinition()));
    }

    private AttributeDefinition getAttributeDefinition(final Predicate<AttributeDefinitionWrapper> predicate) {
        final AttributeDefinitionWrapper quantityAttributeWrapper =
                attributes.stream().filter(predicate).findAny().orElseThrow(() -> new RuntimeException());

        return AttributeDefinitionBuilder.of(quantityAttributeWrapper.getName(), quantityAttributeWrapper.getLabel(),
                mapAttributeType(quantityAttributeWrapper.getType()))
                .isRequired(quantityAttributeWrapper.getIsRequired())
                .isSearchable(quantityAttributeWrapper.getIsSearchable())
                .attributeConstraint(mapAttributeConstraint(quantityAttributeWrapper.getAttributeConstraint()))
                .inputHint(mapTextInputHint(quantityAttributeWrapper.getInputHint()))
                .build();
    }

    private AttributeDefinition quantityAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "quantity".equals(attributeDefinitionWrapper.getName()));
    }

    private AttributeDefinition boxAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "box".equals(attributeDefinitionWrapper.getName()));
    }

    private AttributeDefinition pactasMonthlyAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "pactas1".equals(attributeDefinitionWrapper.getName()));
    }

    private AttributeDefinition pactasTwoWeeklyAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "pactas2".equals(attributeDefinitionWrapper.getName()));
    }

    private AttributeDefinition pactasWeeklyAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "pactas4".equals(attributeDefinitionWrapper.getName()));
    }

    private AttributeConstraint mapAttributeConstraint(final AttributeConstraintWrapper attributeConstraintWrapper) {
        final AttributeConstraint result;
        switch (attributeConstraintWrapper) {
            case NONE:
                result = AttributeConstraint.NONE;
                break;
            case COMBINATION_UNIQUE:
                result = AttributeConstraint.COMBINATION_UNIQUE;
                break;
            case UNIQUE:
                result = AttributeConstraint.UNIQUE;
                break;
            case SAME_FOR_ALL:
                result = AttributeConstraint.SAME_FOR_ALL;
                break;
            default:
                throw new RuntimeException("Unknown AttributeConstraint: " + attributeConstraintWrapper);
        }
        return result;
    }

    private AttributeType mapAttributeType(final AttributeTypeWrapper attributeTypeWrapper) {
        final AttributeType result;
        switch (attributeTypeWrapper.name()) {
            case "text":
                result = StringType.of();
                break;
            case "number":
                result = NumberType.of();
                break;
            default:
                throw new RuntimeException("Unknown AttributeType: " + attributeTypeWrapper.name());
        }
        return result;
    }

    private TextInputHint mapTextInputHint(final TextInputHintWrapper textInputHintWrapper) {
        final TextInputHint result;
        switch (textInputHintWrapper) {
            case SINGLE_LINE:
                result = TextInputHint.SINGLE_LINE;
                break;
            case MULTI_LINE:
                result = TextInputHint.MULTI_LINE;
                break;
            default:
                throw new RuntimeException("Unknown TextInputHint: " + textInputHintWrapper);
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<AttributeDefinitionWrapper> getAttributes() {
        return attributes;
    }
}
