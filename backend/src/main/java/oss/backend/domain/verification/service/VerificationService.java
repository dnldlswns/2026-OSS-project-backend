package oss.backend.domain.verification.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import oss.backend.domain.verification.dto.VerificationCompleteRequest;
import oss.backend.domain.verification.dto.VerificationCompleteResponse;
import oss.backend.domain.verification.entity.Verification;
import oss.backend.domain.verification.repository.VerificationRepository;
import oss.backend.infra.portone.PortOneClient;
import oss.backend.infra.portone.dto.PortOneVerificationResponse;
import oss.backend.infra.portone.dto.PortOneVerifiedCustomer;

@Service
@RequiredArgsConstructor
public class VerificationService {
        private final PortOneClient portOneClient;
        private final VerificationRepository verificationRepository;

        public VerificationCompleteResponse complete(VerificationCompleteRequest request) {
                PortOneVerificationResponse response = portOneClient.getVerification(request.identityVerificationId());

                if (response == null || !"VERIFIED".equals(response.status())) {
                        throw new IllegalStateException("본인인증이 완료되지 않았습니다.");
                }

                PortOneVerifiedCustomer customer = response.verifiedCustomer();
                if (customer == null || customer.ci() == null || customer.name() == null) {
                        throw new IllegalStateException("검증된 본인정보가 없습니다.");
                }

                Verification verification = Verification.builder()
                                .verificationId(UUID.randomUUID().toString())
                                .identityVerificationId(request.identityVerificationId())
                                .ci(customer.ci())
                                .di(customer.di())
                                .name(customer.name())
                                .birthDate(customer.birthDate() == null ? null : LocalDate.parse(customer.birthDate()))
                                .phoneNumber(customer.phoneNumber())
                                .verifiedAt(LocalDateTime.now())
                                .build();

                Verification saved = verificationRepository.save(verification);

                return new VerificationCompleteResponse(
                                saved.getVerificationId(),
                                saved.getName(),
                                saved.getBirthDate() == null ? null : saved.getBirthDate().toString(),
                                saved.getPhoneNumber());
        }
}
