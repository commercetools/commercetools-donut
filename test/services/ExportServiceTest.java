package services;


import io.sphere.sdk.client.PlayJavaSphereClient;
import org.junit.Before;
import org.mockito.Mockito;

public class ExportServiceTest {

    private PlayJavaSphereClient playJavaSphereClient;
    private ImportService importService;

    @Before
    public void setUp() {
        playJavaSphereClient = Mockito.mock(PlayJavaSphereClient.class);
        importService = new ImportServiceImpl(playJavaSphereClient);
    }
}
