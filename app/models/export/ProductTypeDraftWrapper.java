package models.export;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.models.LocalizedString;
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

    public ProductTypeDraftWrapper(@JsonProperty("name") final String name, @JsonProperty("description") final String description,
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

        return AttributeDefinitionBuilder.of(quantityAttributeWrapper.name, quantityAttributeWrapper.label,
                mapAttributeType(quantityAttributeWrapper.type))
                .isRequired(quantityAttributeWrapper.isRequired)
                .isSearchable(quantityAttributeWrapper.isSearchable)
                .attributeConstraint(mapAttributeConstraint(quantityAttributeWrapper.attributeConstraint))
                .inputHint(mapTextInputHint(quantityAttributeWrapper.inputHint))
                .build();
    }

    private AttributeDefinition quantityAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "quantity".equals(attributeDefinitionWrapper.name));
    }

    private AttributeDefinition boxAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "box".equals(attributeDefinitionWrapper.name));
    }

    private AttributeDefinition pactasMonthlyAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "pactas1".equals(attributeDefinitionWrapper.name));
    }

    private AttributeDefinition pactasTwoWeeklyAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "pactas2".equals(attributeDefinitionWrapper.name));
    }

    private AttributeDefinition pactasWeeklyAttributeDefinition() {
        return getAttributeDefinition(attributeDefinitionWrapper -> "pactas4".equals(attributeDefinitionWrapper.name));
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
        switch (attributeTypeWrapper.name) {
            case "text":
                result = StringType.of();
                break;
            case "number":
                result = NumberType.of();
                break;
            default:
                throw new RuntimeException("Unknown AttributeType: " + attributeTypeWrapper.name);
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

    public static class AttributeDefinitionWrapper extends Base {

        private final AttributeTypeWrapper type;
        private final String name;
        private final LocalizedString label;
        private final Boolean isRequired;
        private final AttributeConstraintWrapper attributeConstraint;
        private final Boolean isSearchable;
        private final TextInputHintWrapper inputHint;

        public AttributeDefinitionWrapper(@JsonProperty("type") AttributeTypeWrapper type, @JsonProperty("name") String name,
                                          @JsonProperty("label") LocalizedString label, @JsonProperty("isRequired") Boolean isRequired,
                                          @JsonProperty("attributeConstraint") AttributeConstraintWrapper attributeConstraint,
                                          @JsonProperty("isSearchable") Boolean isSearchable,
                                          @JsonProperty("inputHint") TextInputHintWrapper inputHint) {
            this.type = type;
            this.name = name;
            this.label = label;
            this.isRequired = isRequired;
            this.attributeConstraint = attributeConstraint;
            this.isSearchable = isSearchable;
            this.inputHint = inputHint;
        }
    }

    public static class AttributeTypeWrapper {
        private final String name;

        public AttributeTypeWrapper(@JsonProperty("name") final String name) {
            this.name = name;
        }
    }

    enum AttributeConstraintWrapper {
        NONE,
        UNIQUE,
        COMBINATION_UNIQUE,
        SAME_FOR_ALL;
    }


    enum TextInputHintWrapper {
        SINGLE_LINE,
        MULTI_LINE;
    }
}
