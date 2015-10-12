package services;


import io.sphere.sdk.client.PlayJavaSphereClient;
import org.junit.Before;
import org.mockito.Mockito;

public class ExportServiceTest {

    private PlayJavaSphereClient playJavaSphereClient;
    private ExportService exportService;

    @Before
    public void setUp() {
        playJavaSphereClient = Mockito.mock(PlayJavaSphereClient.class);
        exportService = new ExportServiceImpl(playJavaSphereClient);
    }
}
