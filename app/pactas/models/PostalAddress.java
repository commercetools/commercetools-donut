package pactas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.shop.model.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostalAddress {
    private String addressLine1;
    private String addressLine2;
    private String street;
    private String houseNumber;
    private String postalCode;
    private String city;
    private String country;

    private PostalAddress(@JsonProperty("AddressLine1") String addressLine1,
                          @JsonProperty("AddressLine2") String addressLine2,
                          @JsonProperty("Street") String street,
                          @JsonProperty("HouseNumber") String houseNumber,
                          @JsonProperty("PostalCode") String postalCode,
                          @JsonProperty("City") String city,
                          @JsonProperty("Country") String country) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public CountryCode getCountry() {
        return CountryCode.valueOf(country);
    }

    public Address getAddress() {
        Address address = new Address(getCountry());
        if (addressLine1 != null) {
            address.setStreetName(addressLine1);
        }
        if (addressLine2 != null) {
            address.setStreetNumber(addressLine2);
        }
        if (postalCode != null) {
            address.setPostalCode(postalCode);
        }
        if (city != null) {
            address.setCity(city);
        }
        return address;
    }

    @Override
    public String toString() {
        return "PactasRecipientAddress{" +
                "addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostalAddress that = (PostalAddress) o;

        if (addressLine1 != null ? !addressLine1.equals(that.addressLine1) : that.addressLine1 != null) return false;
        if (addressLine2 != null ? !addressLine2.equals(that.addressLine2) : that.addressLine2 != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (houseNumber != null ? !houseNumber.equals(that.houseNumber) : that.houseNumber != null) return false;
        if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) return false;
        if (street != null ? !street.equals(that.street) : that.street != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = addressLine1 != null ? addressLine1.hashCode() : 0;
        result = 31 * result + (addressLine2 != null ? addressLine2.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (houseNumber != null ? houseNumber.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
}
