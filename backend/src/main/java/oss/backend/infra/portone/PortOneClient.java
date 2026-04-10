package oss.backend.infra.portone;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import oss.backend.infra.portone.dto.PortOneVerificationResponse;

@Component
@RequiredArgsConstructor
public class PortOneClient {
        private final RestClient restClient;
        private final PortOneProperties properties;

        public PortOneVerificationResponse getVerification(String identityVerificationId) {
                return restClient.get()
                                .uri(properties.baseUrl() + "/identity-verifications/{id}", identityVerificationId)
                                .header("Authorization", "PortOne " + properties.apiSecret())
                                .retrieve()
                                .body(PortOneVerificationResponse.class);
        }
}
