package oss.backend.domain.user.entity;

import java.time.LocalDate;

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
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 100)
        private String verificationId;

        @Column(length = 20)
        private String name;

        @Column(length = 20)
        private LocalDate birthDate;

        @Column(length = 20)
        private String phoneNumber;

        @Column(nullable = false, unique = true, length = 100)
        private String userId;

        @Column(nullable = false, length = 100)
        private String userPassword;

        @Builder
        public User(String verificationId,
                        String name,
                        LocalDate birthDate,
                        String phoneNumber,
                        String userId,
                        String userPassword) {
                this.verificationId = verificationId;
                this.name = name;
                this.birthDate = birthDate;
                this.phoneNumber = phoneNumber;
                this.userId = userId;
                this.userPassword = userPassword;
        }

}
