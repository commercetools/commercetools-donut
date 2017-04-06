package pactas;

import play.Configuration;

import javax.inject.Inject;
import java.util.Optional;

public class PactasConfiguration {

    private final String publicKey;

    @Inject
    private PactasConfiguration(final Configuration configuration) {
        this.publicKey = Optional.ofNullable(configuration.getString("pactas.publicKey")).orElseThrow(() -> new IllegalArgumentException("Missing Pactas public key"));
    }

    public PactasConfiguration(final String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
