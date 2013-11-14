package forms;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.shop.model.Address;
import io.sphere.client.shop.model.Customer;
import play.data.validation.Constraints;

import java.util.ArrayList;
import java.util.List;

public class SetAddress {

    @Constraints.Required(message = "First name required")
    public String firstName;

    @Constraints.Required(message = "Last name required")
    public String lastName;

    @Constraints.Required(message = "Street address required")
    public String street;

    @Constraints.Required(message = "Postal code required")
    public String postalCode;

    @Constraints.Required(message = "City required")
    public String city;

    @Constraints.Required(message = "Country required")
    @Constraints.Pattern(value = "DE", message = "Invalid value for country")
    public String country;

    public SetAddress() {

    }

    public SetAddress(io.sphere.client.shop.model.Address address) {
        if (address != null) {
            this.firstName = address.getFirstName();
            this.lastName = address.getLastName();
            this.street = address.getStreetName();
            this.postalCode = address.getPostalCode();
            this.city = address.getCity();
            this.country = address.getCountry().getAlpha2();
        } else {
            this.firstName = "Chief";
            this.lastName = "Wiggum";
            this.street = "Evergreen Terrace, 13";
            this.postalCode = "58331";
            this.city = "Springfield";
            this.country = "DE";
        }
    }

    public SetAddress(Customer customer) {
        this(customer.getName().getFirstName(), customer.getName().getLastName(), null, null, null, null);
    }

    public SetAddress(String firstName, String lastName, String street,
                      String postalCode, String city, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }

    public Address getAddress() {
        Address address = new Address(getCountryCode());
        address.setFirstName(firstName);
        address.setLastName(lastName);
        address.setStreetName(street);
        address.setPostalCode(postalCode);
        address.setCity(city);
        return address;
    }

    public CountryCode getCountryCode() {
        return CountryCode.getByCode(this.country);
    }

    public static List<CountryCode> getCountryCodes() {
        List<CountryCode> countries = new ArrayList<CountryCode>();
        countries.add(CountryCode.DE);
        return countries;
    }
}
