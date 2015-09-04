import controllers.CurrencyOperations;
import controllers.OrderController;
import controllers.PactasWebhookController;
import controllers.ProductController;
import exceptions.SubscriptionProductNotFound;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.http.ApacheHttpClientAdapter;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import pactas.Pactas;
import pactas.PactasImpl;
import play.Application;
import play.Configuration;
import play.GlobalSettings;
import sphere.Sphere;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class Global extends GlobalSettings {

    private Application app;
    private Sphere sphere;
    private Pactas pactas;
    private SphereClient sphereClient;
    private ProductProjection productProjection;

    @Override
    public void onStart(final Application app) {
        this.app = app;
        this.sphere = Sphere.getInstance();
        this.pactas = new PactasImpl(app.configuration());
        sphereClient = createSphereClient(app);
        productProjection = fetchProductProjection();
        checkProjectCurrency(app);
        super.onStart(app);
    }

    private SphereClient createSphereClient(final Application app) {
        final Configuration configuration = app.configuration();
        final String projectKey = configuration.getString("sphere.project");
        final String clientId = configuration.getString("sphere.clientId");
        final String clientSecret = configuration.getString("sphere.clientSecret");
        final SphereClientFactory factory = SphereClientFactory.of(() -> ApacheHttpClientAdapter.of(HttpAsyncClients.createDefault()));
        return factory.createClient(projectKey, clientId, clientSecret);
    }

    private ProductProjection fetchProductProjection() {
        final ProductProjectionQuery request = ProductProjectionQuery.ofCurrent();
        final CompletionStage<PagedQueryResult<ProductProjection>> resultCompletionStage =
                sphereClient.execute(request);
        final PagedQueryResult<ProductProjection> queryResult = resultCompletionStage.toCompletableFuture().join();
        final Optional<ProductProjection> product = queryResult.head();
        return product.orElseThrow(SubscriptionProductNotFound::new);
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
            result = (A) new ProductController(sphere, app.configuration(), productProjection, sphereClient);
        } else if (controllerClass.equals(OrderController.class)) {
            result = (A) new OrderController(sphere, app.configuration(), productProjection, sphereClient);
        } else if (controllerClass.equals(PactasWebhookController.class)) {
            result = (A) new PactasWebhookController(sphere, app.configuration(), pactas, productProjection, sphereClient);
        } else {
            result = super.getControllerInstance(controllerClass);
        }
        return result;
    }
}
