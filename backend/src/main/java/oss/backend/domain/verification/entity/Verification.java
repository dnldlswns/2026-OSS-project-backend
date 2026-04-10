package oss.backend.domain.verification.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Verification {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 100)
        private String verificationId;

        @Column(nullable = false, unique = true, length = 150)
        private String identityVerificationId;

        @Column(nullable = false, unique = true, length = 200)
        private String ci;

        @Column(length = 200)
        private String di;

        @Column(nullable = false, length = 50)
        private String name;

        private LocalDate birthDate;

        @Column(length = 20)
        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private VerificationStatus status;

        @Column(nullable = false)
        private LocalDateTime verifiedAt;

        @Builder
        public Verification(
                        String verificationId,
                        String identityVerificationId,
                        String ci,
                        String di,
                        String name,
                        LocalDate birthDate,
                        String phoneNumber,
                        VerificationStatus status,
                        LocalDateTime verifiedAt) {
                this.verificationId = verificationId;
                this.identityVerificationId = identityVerificationId;
                this.ci = ci;
                this.di = di;
                this.name = name;
                this.birthDate = birthDate;
                this.phoneNumber = phoneNumber;
                this.status = status;
                this.verifiedAt = verifiedAt;
        }

        public void markFailed() {
                this.status = VerificationStatus.FAILED;
        }
}
