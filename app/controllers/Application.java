package controllers;

import forms.AddToCart;
import io.sphere.client.shop.model.*;
import play.data.Form;
import play.mvc.Result;
import sphere.ShopController;
import utils.Util;
import utils.pactas.Contract;
import utils.pactas.Customer;
import utils.pactas.Pactas;
import views.html.index;
import views.html.order;
import views.html.success;

import static play.data.Form.form;
import static utils.Util.*;

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
        Variant variant = getVariant(addToCart.variantId);
        if (variant == null) {
            flash("error", "Product not found. Please try again.");
            return showProduct();
        }
        // Case correct submit
        addLineItem(variant, addToCart.howOften);
        return redirect(routes.Application.showOrder());
    }

    public static Result showOrder() {
        // Case no product selected
        Cart cart = sphere().currentCart().fetch();
        if (cart.getLineItems().size() < 1) {
            return showProduct();
        }
        // Case missing frequency
        int frequency = getFrequency(cart.getId());
        if (frequency < 1) {
            flash("error", "Missing frequency of delivery. Please try selecting it again.");
            return showProduct();
        }
        // Case product in cart
        LineItem item = cart.getLineItems().get(0);
        return ok(order.render(cart, item, frequency));
    }

    public static Result submitOrder() {
        clearCart();
        return ok(success.render());
    }

    public static Result clearOrder() {
        clearCart();
        return redirect(routes.Application.showProduct());
    }

    /* Method called by Pactas every time an order must be placed (weekly, monthly...) */
    public static Result executeSubscription() {
        play.Logger.debug("------ Execute transaction");
        clearCart();
        String contractId = Pactas.getContractId(request());
        return subscribe(contractId);
    }

    public static Result subscribe(String contractId) {
        // Case contract does not exist in Pactas backend
        Contract contract = Contract.get(contractId);
        if (contract == null) {
            return badRequest("Given contract ID is not valid");
        }

        // Case customer does not exist in Pactas backend
        Customer customer = Customer.get(contract.getCustomerId());
        if (customer == null) {
            return badRequest("Customer does not exist");
        }

        // Case variant from Pactas does not exist in Sphere backend
        Variant variant = contract.getVariant();
        if (variant == null) {
            return badRequest("Requested variant does not exist");
        }
        sphere().currentCart().addLineItem(Util.getProduct().getId(), variant.getId(), 1);

        // Case postal address from Pactas is invalid
        Address address = customer.getAddress();
        if (address == null) {
            return badRequest("Given postal address is invalid");
        }
        sphere().currentCart().setShippingAddress(address);

        // Case order can be created
        sphere().currentCart().createOrder(PaymentState.Paid);

        return ok("Order created!");
    }
}
