package controllers;

import forms.SubscriptionFormData;
import models.ProductPageDataFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import services.SubscriptionInSession;
import views.html.index;

import javax.inject.Inject;

public class ProductController extends Controller {

    private final FormFactory formFactory;
    private final ProductPageDataFactory productPageDataFactory;
    private final SubscriptionInSession subscriptionInSession;

    @Inject
    public ProductController(final FormFactory formFactory, final ProductPageDataFactory productPageDataFactory,
                             final SubscriptionInSession subscriptionInSession) {
        this.formFactory = formFactory;
        this.productPageDataFactory = productPageDataFactory;
        this.subscriptionInSession = subscriptionInSession;
    }

    public Result show() {
        return ok(index.render(productPageDataFactory.create()));
    }

    public Result submit() {
        final Form<SubscriptionFormData> boundForm = formFactory.form(SubscriptionFormData.class).bindFromRequest();
        if (!boundForm.hasErrors()) {
            final SubscriptionFormData formData = boundForm.get();
            subscriptionInSession.store(formData.getVariantId(), formData.getHowOften());
            return redirect(controllers.routes.OrderController.show());
        } else {
            flash("error", "Please select a box and how often you want it.");
            return badRequest(index.render(productPageDataFactory.create()));
        }
    }
}
