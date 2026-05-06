package oss.backend.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import oss.backend.domain.auth.dto.LoginRequest;
import oss.backend.domain.auth.dto.LoginResponse;
import oss.backend.domain.auth.dto.SignupRequest;
import oss.backend.domain.auth.dto.SignupResponse;

@SpringBootTest
@Transactional
public class AuthServiceTest {

        @Autowired
        private AuthService authService;

        @Test
        @DisplayName("회원가입 성공")
        void signupSuccess() {
                SignupResponse response = authService.signup(new SignupRequest(
                                "홍길동", "1990-05-15", "M", "010-1234-5678",
                                "demo@example.com", "password1!", null));

                assertThat(response.user().email()).isEqualTo("demo@example.com");
                assertThat(response.user().name()).isEqualTo("홍길동");
                assertThat(response.user().gender()).isEqualTo("남성");
                assertThat(response.user().role()).isEqualTo("user");
        }

        @Test
        @DisplayName("이메일 중복 시 회원가입 실패")
        void signupDuplicateEmail() {
                authService.signup(new SignupRequest(
                                "홍길동", "1990-05-15", "M", "010-1234-5678",
                                "dup@example.com", "password1!", null));

                assertThatThrownBy(() -> authService.signup(new SignupRequest(
                                "김길동", "1991-01-01", "F", "010-9999-9999",
                                "dup@example.com", "password1!", null)))
                                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("로그인 성공 시 토큰 발급")
        void loginSuccess() {
                authService.signup(new SignupRequest(
                                "홍길동", "1990-05-15", "M", "010-1234-5678",
                                "login@example.com", "password1!", null));

                LoginResponse response = authService.login(new LoginRequest("login@example.com", "password1!"));
                assertThat(response.token()).isNotBlank();
                assertThat(response.user().email()).isEqualTo("login@example.com");
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인 실패")
        void loginWrongPassword() {
                authService.signup(new SignupRequest(
                                "홍길동", "1990-05-15", "M", "010-1234-5678",
                                "wrongpw@example.com", "password1!", null));

                assertThatThrownBy(() -> authService.login(new LoginRequest("wrongpw@example.com", "wrong-password")))
                                .isInstanceOf(IllegalArgumentException.class);
        }
}
