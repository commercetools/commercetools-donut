package utils;

import java.text.NumberFormat;
import java.util.*;

import io.sphere.client.model.Money;
import io.sphere.client.shop.model.*;

import static java.util.Currency.*;

public class ViewHelper {

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
            return "background-image: url('"+ variant.getImages().get(2).getSize(ImageSize.MEDIUM).getUrl() +"')";
        }
        return "";
    }

    public static String getPrice(Money money) {
        String price = NumberFormat.getInstance(Locale.GERMANY).format(money.getAmount());
        String currency = getInstance(money.getCurrencyCode()).getSymbol(Locale.GERMANY);
        return price + " " + currency;
    }

}
