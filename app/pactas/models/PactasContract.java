package pactas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PactasContract {
    private final List<ContractPhases> phases;
    private final String customerId;

    private PactasContract(@JsonProperty("Phases") List<ContractPhases> phases,
                           @JsonProperty("CustomerId") String customerId) {
        this.phases = phases;
        this.customerId = customerId;
    }

    public List<ContractPhases> getPhases() {
        return phases;
    }

    public String getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "PactasContract{" +
                "phases=" + phases +
                ", customerId='" + customerId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PactasContract that = (PactasContract) o;

        if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) return false;
        if (phases != null ? !phases.equals(that.phases) : that.phases != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = phases != null ? phases.hashCode() : 0;
        result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
        return result;
    }
}
