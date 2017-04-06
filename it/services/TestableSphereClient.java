package services;

import io.sphere.sdk.client.*;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public final class TestableSphereClient implements BlockingSphereClient {

    private static final String IT_PREFIX = "DONUT_IT_";
    private static final String IT_CTP_PROJECT_KEY = IT_PREFIX + "CTP_PROJECT_KEY";
    private static final String IT_CTP_CLIENT_SECRET = IT_PREFIX + "CTP_CLIENT_SECRET";
    private static final String IT_CTP_CLIENT_ID = IT_PREFIX + "CTP_CLIENT_ID";

    private final BlockingSphereClient delegate;

    public TestableSphereClient() {
        this.delegate = provideSphereClient();
    }

    @Override
    public <T> CompletionStage<T> execute(final SphereRequest<T> sphereRequest) {
        return delegate.execute(sphereRequest);
    }

    @Override
    public <T> T executeBlocking(final SphereRequest<T> sphereRequest) {
        return delegate.executeBlocking(sphereRequest);
    }

    @Override
    public <T> T executeBlocking(final SphereRequest<T> sphereRequest, final long timeout, final TimeUnit unit) {
        return delegate.executeBlocking(sphereRequest, timeout, unit);
    }

    @Override
    public <T> T executeBlocking(final SphereRequest<T> sphereRequest, final Duration duration) {
        return delegate.executeBlocking(sphereRequest, duration);
    }

    @Override
    public void close() {
        delegate.close();
    }

    private static BlockingSphereClient provideSphereClient() {
        final SphereClient client = SphereClientFactory.of(SphereAsyncHttpClientFactory::create)
                .createClient(projectKey(), clientId(), clientSecret());
        return BlockingSphereClient.of(client, Duration.ofSeconds(20));
    }

    private static String projectKey() {
        return getValueForEnvVar(IT_CTP_PROJECT_KEY);
    }

    private static String clientId() {
        return getValueForEnvVar(IT_CTP_CLIENT_ID);
    }

    private static String clientSecret() {
        return getValueForEnvVar(IT_CTP_CLIENT_SECRET);
    }

    private static String getValueForEnvVar(final String key) {
        return Optional.ofNullable(System.getenv(key))
                .orElseThrow(() -> new RuntimeException(
                        "Missing environment variable " + key + ", please provide the following environment variables for the integration test:\n" +
                                "export " + IT_CTP_PROJECT_KEY + "=\"Your CTP project key\"\n" +
                                "export " + IT_CTP_CLIENT_ID + "=\"Your CTP client ID\"\n" +
                                "export " + IT_CTP_CLIENT_SECRET + "=\"Your CTP client secret\"\n"));
    }
}