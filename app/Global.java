import controllers.GlobalOperations;
import controllers.OrderController;
import controllers.PactasWebhookController;
import controllers.ProductController;
import pactas.Pactas;
import pactas.PactasImpl;
import play.Application;
import play.GlobalSettings;
import sphere.Sphere;

public class Global extends GlobalSettings {
    private Application app;
    private Sphere sphere;
    private Pactas pactas;

    @Override
    public void onStart(final Application app) {
        this.app = app;
        this.sphere = Sphere.getInstance();
        this.pactas = new PactasImpl(app.configuration());
        checkProjectCurrency(app);
        super.onStart(app);
    }

    private void checkProjectCurrency(final Application app) {
        GlobalOperations.of(app.configuration()).currency();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> A getControllerInstance(final Class<A> controllerClass) throws Exception {
        final A result;
        if (controllerClass.equals(ProductController.class)) {
            result = (A) new ProductController(sphere, app.configuration());
        } else if (controllerClass.equals(OrderController.class)) {
            result = (A) new OrderController(sphere, app.configuration());
        } else if (controllerClass.equals(PactasWebhookController.class)) {
            result = (A) new PactasWebhookController(sphere, app.configuration(), pactas);
        } else {
            result = super.getControllerInstance(controllerClass);
        }
        return result;
    }
}
