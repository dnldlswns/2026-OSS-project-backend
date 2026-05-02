package oss.backend.domain.verification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import oss.backend.domain.verification.entity.Verification;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
        boolean existsByCi(String ci);

        boolean existsByIdentityVerificationId(String identityVerificationId);

        Optional<Verification> findByVerificationId(String verificationId);
}