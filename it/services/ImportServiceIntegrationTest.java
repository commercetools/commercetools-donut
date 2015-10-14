package services;


import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

public class ImportServiceIntegrationTest {

    private Application application;
    private ImportService importService;

    @Before
    public void setUp() {
        application = new GuiceApplicationBuilder().build();
        importService = application.injector().instanceOf(ImportService.class);
    }

    @Test
    public void testExportCustomType() {
        //TODO
    }

    @Test
    public void testExportProductModel() {
        //TODO
    }
}
