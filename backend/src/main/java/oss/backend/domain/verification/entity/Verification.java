package oss.backend.domain.verification.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

        @Column(nullable = false, unique = true, length = 100)
        private String identityVerificationId;

        @Column(nullable = false, unique = true, length = 100)
        private String ci;

        @Column(length = 200)
        private String di;

        @Column(nullable = false, length = 50)
        private String name;

        @Column(nullable = false, length = 20)
        private LocalDate birthDate;

        @Column(length = 20)
        private String phoneNumber;

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
                        LocalDateTime verifiedAt) {
                this.verificationId = verificationId;
                this.identityVerificationId = identityVerificationId;
                this.ci = ci;
                this.di = di;
                this.name = name;
                this.birthDate = birthDate;
                this.phoneNumber = phoneNumber;
                this.verifiedAt = verifiedAt;
        }
}
