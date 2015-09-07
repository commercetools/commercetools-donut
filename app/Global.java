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
import services.*;
import sphere.Sphere;
import utils.CurrencyOperations;

public class Global extends GlobalSettings {

    private Application app;
    private Sphere sphere;
    private Pactas pactas;
    private SphereClient sphereClient;
    private CartService cartService;
    private ProductService productService;
    private OrderService orderService;

    @Override
    public void onStart(final Application app) {
        this.app = app;
        this.sphere = Sphere.getInstance();
        this.pactas = pactas(app.configuration());

        this.sphereClient = sphereClient(app);
        this.cartService = cartService(sphereClient, sphere);
        this.productService = productService(sphereClient);
        this.orderService = orderService(sphereClient);
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
        return new CartServiceImpl(sphereClient, sphere);
    }

    private ProductService productService(final SphereClient sphereClient) {
        return new ProductServiceImpl(sphereClient);
    }

    private OrderService orderService(final SphereClient sphereClient) {
        return new OrderServiceImpl(sphereClient);
    }

    private Pactas pactas(final Configuration configuration) {
        return new PactasImpl(configuration);
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
            result = (A) new ProductController(app.configuration(), productService, cartService);
        } else if (controllerClass.equals(OrderController.class)) {
            result = (A) new OrderController(app.configuration(), cartService);
        } else if (controllerClass.equals(PactasWebhookController.class)) {
            result = (A) new PactasWebhookController(app.configuration(), cartService, orderService,  productService, pactas);
        } else {
            result = super.getControllerInstance(controllerClass);
        }
        return result;
    }
}
