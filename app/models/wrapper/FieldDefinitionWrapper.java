package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.LocalizedString;

import static java.util.Objects.requireNonNull;

public class FieldDefinitionWrapper {

    private final AttributeTypeWrapper type;
    private final String name;
    private final LocalizedString label;
    private final TextInputHintWrapper inputHint;
    private final Boolean isRequired;

    public FieldDefinitionWrapper(@JsonProperty("type") final AttributeTypeWrapper type,
                                  @JsonProperty("name") final String name,
                                  @JsonProperty("label") final LocalizedString label,
                                  @JsonProperty("inputHint") final TextInputHintWrapper inputHint,
                                  @JsonProperty("required") final Boolean isRequired) {
        this.type = requireNonNull(type);
        this.name = requireNonNull(name);
        this.label = requireNonNull(label);
        this.inputHint = requireNonNull(inputHint);
        this.isRequired = requireNonNull(isRequired);
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

    public TextInputHintWrapper getInputHint() {
        return inputHint;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }
}
