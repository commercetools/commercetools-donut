package pactas.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.client.shop.model.Address;

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
}
