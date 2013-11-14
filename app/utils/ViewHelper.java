package utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.model.Money;
import io.sphere.client.shop.model.*;
import org.apache.commons.lang3.text.WordUtils;
import play.mvc.Http;
import sphere.Sphere;

import static java.util.Currency.*;

public class ViewHelper {

	/**
	 * Returns the current Cart in session.
	 */
	public static Cart getCurrentCart() {
		return Sphere.getInstance().currentCart().fetch();
	}

    public static int getOften(Cart cart) {
        if (cart.getLineItems().size() < 1) {
            return 0;
        }
        return cart.getLineItems().get(0).getQuantity();
    }

    public static int getVariant(Cart cart) {
        if (cart.getLineItems().size() < 1) {
            return 0;
        }
        return cart.getLineItems().get(0).getVariant().getId();
    }

    public static String getFrequencyName(int frequency) {
        switch (frequency) {
            case 1: return "ONCE A MONTH";
            case 2: return "EVERY TWO WEEKS";
            case 4: return "ONCE A WEEK";
        }
        return "UNKNOWN FREQUENCY";
    }

    public static String getPactasID(LineItem item, int frequency) {
        return item.getVariant().getString("pactas" + frequency);
    }

    public static String getProductStampImage(Variant variant) {
        if (variant.getImages().size() > 2) {
            return "background-image: url('"+ variant.getImages().get(1).getSize(ImageSize.ORIGINAL).getUrl() +"')";
        }
        return "";
    }

    public static String getAddToCartImage(Variant variant) {
        if (variant.getImages().size() > 2) {
            return "background-image: url('"+ variant.getImages().get(2).getSize(ImageSize.ORIGINAL).getUrl() +"')";
        }
        return "";
    }

    public static Customer getCurrentCustomer() {
        Customer customer = null;
        if (Sphere.getInstance().isLoggedIn()) {
            customer = Sphere.getInstance().currentCustomer().fetch();
        }
        return customer;
    }

    public static boolean isLoggedIn() {
        return Sphere.getInstance().isLoggedIn();
    }

	/**
	 * Returns the list of root categories
	 */
	public static List<Category> getRootCategories() {
        return Sphere.getInstance().categories().getRoots();
	}

    public static String getReturnUrl() {
        return Http.Context.current().session().get("returnUrl");
    }

    public static String capitalizeInitials(String text) {
        return WordUtils.capitalizeFully(text);
    }

    public static String getCountryName(String code) {
        try {
            return CountryCode.getByCode(code).getName();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getPrice(Money money) {
        String price = NumberFormat.getInstance(Locale.GERMANY).format(money.getAmount());
        String currency = getInstance(money.getCurrencyCode()).getSymbol(Locale.GERMANY);
        return price + " " + currency;
    }

    public static BigDecimal getPercentage(double amount) {
        return BigDecimal.valueOf(amount * 100).stripTrailingZeros();
    }

	/**
	 * Compares the categories and returns the 'active' class if are the same.
	 * 
	 * @param category
     * @param currentCategory
	 * @return 'active' if categories are the same, otherwise an empty string.
	 */
	public static String getActiveClass(Category category, Category currentCategory) {
        String active = "";
        if (currentCategory != null && currentCategory.getPathInTree().contains(category)) {
            active = "active";
        }
		return active;
	}

    public static boolean isCustomerAddressSet(Cart cart) {
        return cart.getShippingAddress() != null;
    }

    public static Money getShippingCost() {
        // TODO Implement correct shipping cost
        return new Money(BigDecimal.valueOf(10), "EUR");
    }
}
