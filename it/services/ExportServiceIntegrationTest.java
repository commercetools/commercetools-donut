package services;


import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

public class ExportServiceIntegrationTest {

    private Application application;
    private ExportService exportService;

    @Before
    public void setUp() {
        application = new GuiceApplicationBuilder().build();
        exportService = application.injector().instanceOf(ExportService.class);
    }

    @Test
    public void testCreateProductModel() {
        //TODO
    }

    @Test
    public void testCreateProductDraftModel() {
        //TODO
    }
}
