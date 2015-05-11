import com.google.common.base.Optional;
import controllers.CurrencyOperations;
import controllers.OrderController;
import controllers.PactasWebhookController;
import controllers.ProductController;
import exceptions.SubscriptionProductNotFound;
import io.sphere.client.shop.model.Product;
import pactas.Pactas;
import pactas.PactasImpl;
import play.Application;
import play.GlobalSettings;
import sphere.Sphere;

public class Global extends GlobalSettings {
    private final static String PRODUCT_SLUG = "donut-box";
    private Application app;
    private Sphere sphere;
    private Pactas pactas;
    private Product product;

    @Override
    public void onStart(final Application app) {
        this.app = app;
        this.sphere = Sphere.getInstance();
        this.pactas = new PactasImpl(app.configuration());
        this.product = fetchProduct();
        checkProjectCurrency(app);
        super.onStart(app);
    }

    private Product fetchProduct() {
        final Optional<Product> product = sphere.products().bySlug(PRODUCT_SLUG).fetch();
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new SubscriptionProductNotFound();
        }
    }

    private void checkProjectCurrency(final Application app) {
        CurrencyOperations.of(app.configuration()).currency();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> A getControllerInstance(final Class<A> controllerClass) throws Exception {
        final A result;
        if (controllerClass.equals(ProductController.class)) {
            result = (A) new ProductController(sphere, app.configuration(), product);
        } else if (controllerClass.equals(OrderController.class)) {
            result = (A) new OrderController(sphere, app.configuration(), product);
        } else if (controllerClass.equals(PactasWebhookController.class)) {
            result = (A) new PactasWebhookController(sphere, app.configuration(), product, pactas);
        } else {
            result = super.getControllerInstance(controllerClass);
        }
        return result;
    }
}
