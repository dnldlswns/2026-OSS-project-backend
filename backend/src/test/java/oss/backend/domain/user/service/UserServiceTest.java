package oss.backend.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import oss.backend.domain.user.dto.UserLoginRequest;
import oss.backend.domain.user.dto.UserLoginResponse;
import oss.backend.domain.user.dto.UserRegisterRequest;
import oss.backend.domain.user.dto.UserRegisterResponse;

@SpringBootTest
@Transactional
public class UserServiceTest {
        @Autowired
        private UserService userService;

        @Test
        @DisplayName("회원가입 기능")
        void registerTest() {
                UserRegisterRequest request = new UserRegisterRequest(
                                "verify-001",
                                "홍길동",
                                LocalDate.of(2000, 1, 1).toString(),
                                "01012341234",
                                "testuser",
                                "1234");
                UserRegisterResponse response = userService.register(request);
                assertThat(response.name()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("로그인 기능")
        void loginTest() {
                userService.register(new UserRegisterRequest(
                                "verify-002",
                                "홍길동",
                                LocalDate.of(2000, 1, 1).toString(),
                                "01012341234",
                                "loginuser",
                                "1234"));
                UserLoginResponse response = userService.login(new UserLoginRequest("loginuser", "1234"));
                assertThat(response.name()).isEqualTo("홍길동");
                assertThat(response.token()).isNotNull();
        }
}
