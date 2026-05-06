package oss.backend.domain.user.entity;

import java.time.LocalDate;

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
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 150)
        private String email;

        @Column(nullable = false, length = 200)
        private String password;

        @Column(nullable = false, length = 30)
        private String name;

        @Column(nullable = false)
        private LocalDate birthDate;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 10)
        private Gender gender;

        @Column(nullable = false, length = 20)
        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 10)
        private Nationality nationality;

        @Column(length = 50)
        private String penName;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 10)
        private Role role;

        @Column(nullable = false)
        private boolean verified;

        @Column(length = 100)
        private String verificationId;

        @Column(length = 500)
        private String profileImageUrl;

        @Builder
        public User(String email,
                        String password,
                        String name,
                        LocalDate birthDate,
                        Gender gender,
                        String phoneNumber,
                        Nationality nationality,
                        String penName,
                        Role role,
                        boolean verified,
                        String verificationId,
                        String profileImageUrl) {
                this.email = email;
                this.password = password;
                this.name = name;
                this.birthDate = birthDate;
                this.gender = gender;
                this.phoneNumber = phoneNumber;
                this.nationality = nationality == null ? Nationality.KOREAN : nationality;
                this.penName = penName;
                this.role = role == null ? Role.USER : role;
                this.verified = verified;
                this.verificationId = verificationId;
                this.profileImageUrl = profileImageUrl;
        }

        public void updateProfile(String phoneNumber, String email, Nationality nationality, String penName) {
                if (phoneNumber != null) {
                        this.phoneNumber = phoneNumber;
                }
                if (email != null) {
                        this.email = email;
                }
                if (nationality != null) {
                        this.nationality = nationality;
                }
                if (penName != null) {
                        this.penName = penName;
                }
        }

        public void changePassword(String newEncodedPassword) {
                this.password = newEncodedPassword;
        }

        public void updateProfileImage(String url) {
                this.profileImageUrl = url;
        }

        public void markVerified(String verificationId) {
                this.verified = true;
                this.verificationId = verificationId;
        }
}
