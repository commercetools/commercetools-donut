package pactas.models;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static pactas.PactasJsonUtils.readObjectFromResource;

public class PactasAuthorizationTest {

    private static final Authorization AUTHORIZATION = readObjectFromResource("pactas-authorization.json", Authorization.class);

    @Test
    public void parsesAuthorizationInformation() throws Exception {
        assertThat(AUTHORIZATION.getAccessToken()).isEqualTo("sxi9rcJl-YlPU2so74dEcmIulO0RQBmT_4ZpN1x_J1uYA==");
        assertThat(AUTHORIZATION.getExpires()).isEqualTo("0");
        assertThat(AUTHORIZATION.getTokenType()).isEqualTo("bearer");
    }
}
