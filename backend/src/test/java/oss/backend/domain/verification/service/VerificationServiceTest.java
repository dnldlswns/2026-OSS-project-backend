package oss.backend.domain.verification.service;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import oss.backend.domain.verification.dto.VerificationCompleteRequest;
import oss.backend.domain.verification.entity.Verification;
import oss.backend.domain.verification.repository.VerificationRepository;
import oss.backend.infra.portone.PortOneClient;
import oss.backend.infra.portone.dto.PortOneVerificationResponse;
import oss.backend.infra.portone.dto.PortOneVerifiedCustomer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class VerificationServiceTest {
        private PortOneClient portOneClient;
        private VerificationRepository verificationRepository;
        private VerificationService verificationService;

        @BeforeEach
        void setUp() {
                portOneClient = mock(PortOneClient.class);
                verificationRepository = mock(VerificationRepository.class);
                verificationService = new VerificationService(portOneClient, verificationRepository);
        }

        @Test
        @DisplayName("본인인증 성공")
        void complete_success() {
                given(portOneClient.getVerification("identity-verification-123"))
                                .willReturn(new PortOneVerificationResponse("VERIFIED",
                                                new PortOneVerifiedCustomer(
                                                                "ci-value",
                                                                "di-value",
                                                                "홍길동",
                                                                "MALE",
                                                                "2000-01-01",
                                                                "01012341234",
                                                                false)));
                given(verificationRepository.existsByCi("ci-value")).willReturn(false);
                given(verificationRepository.save(any(Verification.class)))
                                .willAnswer(invocation -> invocation.getArgument(0));

                var result = verificationService.complete(
                                new VerificationCompleteRequest("identity-verification-123"));

                assertThat(result.name()).isEqualTo("홍길동");
                assertThat(result.birthDate()).isEqualTo("2000-01-01");
                assertThat(result.phoneNumber()).isEqualTo("01012341234");
        }
}
