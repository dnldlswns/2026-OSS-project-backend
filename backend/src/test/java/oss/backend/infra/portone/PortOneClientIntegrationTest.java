package oss.backend.infra.portone;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "PORTONE_API_SECRET", matches = ".+", disabledReason = "PortOne 자격 증명이 없는 환경에서는 실제 API 응답을 보장할 수 없어 비활성화")
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
