package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.models.LocalizedString;

import static java.util.Objects.requireNonNull;

public class AttributeDefinitionWrapper extends Base {

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
        this.type = requireNonNull(type);
        this.name = requireNonNull(name);
        this.label = requireNonNull(label);
        this.isRequired = requireNonNull(isRequired);
        this.attributeConstraint = requireNonNull(attributeConstraint);
        this.isSearchable = requireNonNull(isSearchable);
        this.inputHint = requireNonNull(inputHint);
    }

    public AttributeTypeWrapper getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public AttributeConstraintWrapper getAttributeConstraint() {
        return attributeConstraint;
    }

    public Boolean getIsSearchable() {
        return isSearchable;
    }

    public TextInputHintWrapper getInputHint() {
        return inputHint;
    }
}
