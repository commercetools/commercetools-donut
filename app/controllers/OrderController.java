package controllers;

import models.OrderPageData;
import models.OrderPageDataFactory;
import donut.services.SubscriptionInSession;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.order;
import views.html.success;

import javax.inject.Inject;

public class OrderController extends Controller {

    private final OrderPageDataFactory orderPageDataFactory;
    private final SubscriptionInSession subscriptionInSession;

    @Inject
    public OrderController(final OrderPageDataFactory orderPageDataFactory, final SubscriptionInSession subscriptionInSession) {
        this.orderPageDataFactory = orderPageDataFactory;
        this.subscriptionInSession = subscriptionInSession;
    }

    public Result show() {
        return subscriptionInSession.findVariant()
                .flatMap(variant -> subscriptionInSession.findFrequency()
                        .map(frequency -> {
                            final OrderPageData orderPageData = orderPageDataFactory.create(variant, frequency);
                            return ok(order.render(orderPageData));
                        })
                ).orElseGet(() -> {
                    flash("error", "Please select a box and how often you want it.");
                    return redirect(routes.ProductController.show());
                });
    }

    public Result submit() {
        subscriptionInSession.remove();
        return ok(success.render());
    }
}
