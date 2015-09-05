import controllers.OrderController;
import controllers.PactasWebhookController;
import controllers.ProductController;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.http.ApacheHttpClientAdapter;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import pactas.Pactas;
import pactas.PactasImpl;
import play.Application;
import play.Configuration;
import play.GlobalSettings;
import services.CartService;
import services.DefaultCartService;
import services.PactasPaymentService;
import services.PaymentService;
import sphere.Sphere;
import utils.CurrencyOperations;

public class Global extends GlobalSettings {

    private Application app;
    private Sphere sphere;
    private Pactas pactas;
    private SphereClient sphereClient;
    private CartService cartService;
    private PaymentService paymentService;

    @Override
    public void onStart(final Application app) {
        this.app = app;
        this.sphere = Sphere.getInstance();
        this.pactas = new PactasImpl(app.configuration());

        this.sphereClient = sphereClient(app);
        this.cartService = cartService(sphereClient, sphere);
        this.paymentService = paymentService(sphereClient, sphere);
        checkProjectCurrency(app);
        super.onStart(app);
    }

    private SphereClient sphereClient(final Application app) {
        final Configuration configuration = app.configuration();
        final String projectKey = configuration.getString("sphere.project");
        final String clientId = configuration.getString("sphere.clientId");
        final String clientSecret = configuration.getString("sphere.clientSecret");
        final SphereClientFactory factory = SphereClientFactory.of(() -> ApacheHttpClientAdapter.of(HttpAsyncClients.createDefault()));
        return factory.createClient(projectKey, clientId, clientSecret);
    }

    private CartService cartService(final SphereClient sphereClient, final Sphere sphere) {
        final CartService cartService = new DefaultCartService(sphereClient, sphere);
        return cartService;
    }

    private PaymentService paymentService(final SphereClient sphereClient, final Sphere sphere) {
        final PaymentService paymentService = new PactasPaymentService(sphereClient, sphere);
        return paymentService;
    }

    @Override
    public void onStop(final Application app) {
        super.onStop(app);
        if (sphereClient != null) {
            sphereClient.close();
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
            result = (A) new ProductController(cartService, paymentService, app.configuration());
        } else if (controllerClass.equals(OrderController.class)) {
            result = (A) new OrderController(cartService, paymentService, app.configuration());
        } else if (controllerClass.equals(PactasWebhookController.class)) {
            result = (A) new PactasWebhookController(cartService, paymentService, app.configuration(), pactas);
        } else {
            result = super.getControllerInstance(controllerClass);
        }
        return result;
    }
}
