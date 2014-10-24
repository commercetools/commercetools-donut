package pactas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractPhases {
    private final String type;
    private final String planVariantId;
    private final String planId;

    private ContractPhases(@JsonProperty("Type") String type,
                           @JsonProperty("PlanVariantId") String planVariantId,
                           @JsonProperty("PlanId") String planId) {
        this.type = type;
        this.planVariantId = planVariantId;
        this.planId = planId;
    }

    public String getType() {
        return type;
    }

    public String getPlanVariantId() {
        return planVariantId;
    }

    public String getPlanId() {
        return planId;
    }

    @Override
    public String toString() {
        return "PactasPhases{" +
                "type='" + type + '\'' +
                ", planVariantId='" + planVariantId + '\'' +
                ", planId='" + planId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContractPhases that = (ContractPhases) o;

        if (planId != null ? !planId.equals(that.planId) : that.planId != null) return false;
        if (planVariantId != null ? !planVariantId.equals(that.planVariantId) : that.planVariantId != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (planVariantId != null ? planVariantId.hashCode() : 0);
        result = 31 * result + (planId != null ? planId.hashCode() : 0);
        return result;
    }
}
