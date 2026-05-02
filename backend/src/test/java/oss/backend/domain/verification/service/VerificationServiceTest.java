package oss.backend.domain.verification.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import oss.backend.domain.verification.dto.VerificationCompleteRequest;
import oss.backend.domain.verification.dto.VerificationCompleteResponse;
import oss.backend.infra.portone.PortOneClient;
import oss.backend.infra.portone.dto.PortOneVerificationResponse;
import oss.backend.infra.portone.dto.PortOneVerifiedCustomer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
public class VerificationServiceTest {
        @Autowired
        private VerificationService verificationService;
        @MockitoBean
        private PortOneClient portOneClient;

        @Test
        @DisplayName("본인인증 서비스~레포 저장 로직 성공")
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

                VerificationCompleteResponse result = verificationService.complete(
                                new VerificationCompleteRequest("identity-verification-123"));

                assertThat(result.name()).isEqualTo("홍길동");
                assertThat(result.birthDate()).isEqualTo("2000-01-01");
                assertThat(result.phoneNumber()).isEqualTo("01012341234");
        }
}
