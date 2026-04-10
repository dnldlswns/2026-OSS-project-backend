package oss.backend.infra.portone;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
public class PortOneClientIntegrationTest {
        @Autowired
        PortOneClient portOneClient;

        @Test
        @DisplayName("PortOne 조회 시 존재하지 않는 id면 404가 발생한다")
        void getVerification_not_found_is_success() {
                String identityVerificationId = "id";

                assertThrows(HttpClientErrorException.NotFound.class, () -> {
                        portOneClient.getVerification(identityVerificationId);
                });
        }
}
