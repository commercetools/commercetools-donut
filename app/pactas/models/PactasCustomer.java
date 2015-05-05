package pactas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.client.shop.model.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PactasCustomer {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final PostalAddress address;

    private PactasCustomer(@JsonProperty("Id") String id,
                           @JsonProperty("FirstName") String firstName,
                           @JsonProperty("LastName") String lastName,
                           @JsonProperty("Address") PostalAddress address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public PostalAddress getAddress() {
        return address;
    }

    public Address getCompleteAddress() {
        final Address customerAddress = address.getAddress();
        customerAddress.setFirstName(firstName);
        customerAddress.setLastName(lastName);
        return customerAddress;
    }

    @Override
    public String toString() {
        return "PactasCustomer{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PactasCustomer that = (PactasCustomer) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
