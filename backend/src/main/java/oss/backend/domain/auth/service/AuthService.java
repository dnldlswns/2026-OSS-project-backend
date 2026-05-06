package oss.backend.domain.auth.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import oss.backend.domain.auth.dto.LoginRequest;
import oss.backend.domain.auth.dto.LoginResponse;
import oss.backend.domain.auth.dto.PasswordChangeRequest;
import oss.backend.domain.auth.dto.SignupRequest;
import oss.backend.domain.auth.dto.SignupResponse;
import oss.backend.domain.user.dto.UserResponse;
import oss.backend.domain.user.entity.Gender;
import oss.backend.domain.user.entity.Nationality;
import oss.backend.domain.user.entity.Role;
import oss.backend.domain.user.entity.User;
import oss.backend.domain.user.repository.UserRepository;
import oss.backend.global.response.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final JwtTokenProvider jwtTokenProvider;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public SignupResponse signup(SignupRequest request) {
                if (userRepository.existsByEmail(request.email())) {
                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                }

                User user = User.builder()
                                .email(request.email())
                                .password(passwordEncoder.encode(request.password()))
                                .name(request.name())
                                .birthDate(parseBirth(request.birth()))
                                .gender(Gender.fromCode(request.gender()))
                                .phoneNumber(request.phone())
                                .nationality(Nationality.KOREAN)
                                .role(Role.USER)
                                .verified(request.verificationId() != null && !request.verificationId().isBlank())
                                .verificationId(emptyToNull(request.verificationId()))
                                .build();

                User saved = userRepository.save(user);
                return new SignupResponse(UserResponse.from(saved));
        }

        public LoginResponse login(LoginRequest request) {
                User user = userRepository.findByEmail(request.email())
                                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

                if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                        throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
                }

                String token = jwtTokenProvider.createAccessToken(user);
                return new LoginResponse(UserResponse.from(user), token);
        }

        @Transactional
        public void changePassword(String email, PasswordChangeRequest request) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                        throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
                }

                user.changePassword(passwordEncoder.encode(request.newPassword()));
        }

        private static LocalDate parseBirth(String birth) {
                if (birth == null || birth.isBlank()) {
                        throw new IllegalArgumentException("생년월일은 필수입니다.");
                }
                String normalized = birth.replace('.', '-').replace('/', '-');
                try {
                        return LocalDate.parse(normalized);
                } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("생년월일 형식이 올바르지 않습니다. (예: 1990-05-15)");
                }
        }

        private static String emptyToNull(String s) {
                return (s == null || s.isBlank()) ? null : s;
        }
}
