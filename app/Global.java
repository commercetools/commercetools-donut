import controllers.GlobalOperations;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

    @Override
    public void onStart(final Application app) {
        checkProjectCurrency(app);
        checkProjectCountry(app);
        super.onStart(app);
    }

    private void checkProjectCurrency(Application app) {
        GlobalOperations.of(app.configuration()).currency();
    }

    private void checkProjectCountry(Application app) {
        GlobalOperations.of(app.configuration()).country();
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return super.getControllerInstance(controllerClass);
    }
}
