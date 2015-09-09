import io.sphere.sdk.client.SphereClient;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import utils.CurrencyOperations;

import javax.inject.Inject;

public class Global extends GlobalSettings {

    private static final Logger.ALogger LOG = Logger.of(Global.class);

    @Inject
    private SphereClient sphereClient;

    @Override
    public void onStart(final Application app) {
        LOG.debug("Donut-Shop application starts...");
        checkProjectCurrency(app);
        super.onStart(app);
    }

    private void checkProjectCurrency(final Application app) {
        CurrencyOperations.of(app.configuration()).currency();
    }
}
