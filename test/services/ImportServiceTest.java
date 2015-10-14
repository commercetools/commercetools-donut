package services;


import io.sphere.sdk.client.PlayJavaSphereClient;
import org.junit.Before;
import play.Configuration;

import static org.mockito.Mockito.mock;

public class ImportServiceTest {

    private PlayJavaSphereClient playJavaSphereClient;
    private Configuration configuration;
    private ImportService importService;

    @Before
    public void setUp() {
        playJavaSphereClient = mock(PlayJavaSphereClient.class);
        configuration = mock(Configuration.class);
        importService = new ImportServiceImpl(playJavaSphereClient, configuration);
    }
}
