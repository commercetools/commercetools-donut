package controllers;

import forms.*;
import io.sphere.client.model.CustomObject;
import io.sphere.client.shop.model.*;
import org.codehaus.jackson.JsonNode;
import play.data.Form;
import play.mvc.Result;
import sphere.ShopController;
import utils.Util;
import utils.pactas.Contract;
import utils.pactas.Customer;
import views.html.index;
import views.html.order;
import views.html.success;

import static play.data.Form.form;

public class Application extends ShopController {

    final static Form<AddToCart> addToCartForm = form(AddToCart.class);


    public static Result showProduct() {
        Cart cart = sphere().currentCart().fetch();
        return ok(index.render(cart, Util.getProduct()));
    }

    public static Result submitProduct() {
        // Case missing or invalid product form
        Form<AddToCart> cartForm = addToCartForm.bindFromRequest();
        if (cartForm.hasErrors()) {
            flash("error", "Please select a box and how often you want it.");
            return showProduct();
        }
        // Case invalid product
        AddToCart addToCart = cartForm.get();
        Variant variant = Util.getProduct().getVariants().byId(addToCart.variantId).orNull();
        if (variant == null) {
            flash("error", "Product not found. Please try again.");
            return showProduct();
        }
        /* Clean cart because we only allow a single product */
        Cart cart = sphere().currentCart().fetch();
        for (LineItem item : cart.getLineItems()) {
            sphere().currentCart().removeLineItem(item.getId());
        }
        cart = sphere().currentCart().addLineItem(Util.getProduct().getId(), variant.getId(), 1);
        /* Store frequency in a custom object related to current cart */
        sphere().customObjects().set("cart-frequency", cart.getId(), addToCart.howOften);
        return redirect(routes.Application.showOrder());
    }

    public static Result showOrder() {
        Cart cart = sphere().currentCart().fetch();
        // Case no product selected
        if (cart.getLineItems().size() < 1) {
            return showProduct();
        }
        // Case missing frequency
        CustomObject frequency = sphere().customObjects().get("cart-frequency", cart.getId()).fetch().orNull();
        if (frequency == null) {
            flash("error", "Missing frequency of delivery. Please try selecting it again.");
            return showProduct();
        }
        // Case product in cart
        LineItem item = cart.getLineItems().get(0);
        return ok(order.render(cart, item, frequency.getValue().asInt()));
    }

    public static Result clearOrder() {
        Util.clearCart();
        return redirect(routes.Application.showProduct());
    }

    public static Result success() {
        String text = "Order is done. Please keep in mind that this shop is for demonstration only." +
                "Therefore we don't ship donuts in reality. Don't worry, no payments will be charged." +
                "If we ship donuts someday in the future you'll be the first that will be informed.";
        return ok(success.render(true, text));
    }

    public static Result failure() {
        String text = "Something went wrong. Please try again later.";
        return ok(success.render(false, text));
    }

    /* Method called by Pactas every time an order must be placed (weekly, monthly...) */
    public static Result executeSubscription() {
        play.Logger.debug("------ Execute transaction");

        // Clear previous cart
        Util.clearCart();

        // Read webhook data from Pactas
        String contractId = null;
        if (request().body().asJson() != null) {
            JsonNode webhook = request().body().asJson();
            if (webhook.get("Event").getTextValue().equals("AccountCreated")) {
                contractId = webhook.get("ContractId").getTextValue();
            }
        }

        // Get additional information from Pactas
        Contract contract = new Contract(contractId);
        Customer customer = new Customer(contract.getCustomerId());

        // Set cart with subscription data
        sphere().currentCart().addLineItem(Util.getProduct().getId(), contract.getVariant().getId(), 1);
        sphere().currentCart().setShippingAddress(customer.getAddress());

        // Create order
        String cartSnapshot = sphere().currentCart().createCartSnapshotId();
        while (!Util.isValidCartSnapshot(cartSnapshot)) { }
        sphere().currentCart().createOrder(cartSnapshot, PaymentState.Paid);

        return ok("Order created!");
    }
}
