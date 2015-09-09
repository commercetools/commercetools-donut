import io.sphere.sdk.client.SphereClient;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import utils.CurrencyOperations;

import javax.inject.Inject;

public class Global extends GlobalSettings {

    @Inject
    private SphereClient sphereClient;

    @Override
    public void onStart(final Application app) {
        Logger.debug("Donut-Shop application starts...");
        checkProjectCurrency(app);
        super.onStart(app);
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
}
