package oss.backend.domain.user.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import oss.backend.domain.user.dto.UserLoginRequest;
import oss.backend.domain.user.dto.UserLoginResponse;
import oss.backend.domain.user.dto.UserRegisterRequest;
import oss.backend.domain.user.dto.UserRegisterResponse;
import oss.backend.domain.user.entity.User;
import oss.backend.domain.user.repository.UserRepository;
import oss.backend.global.response.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class UserService {

        private final UserRepository userRepository;
        private final JwtTokenProvider jwtTokenProvider;
        private final PasswordEncoder passwordEncoder;

        public UserRegisterResponse register(UserRegisterRequest request) {
                if (userRepository.existsByUserId(request.userId())) {
                        throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
                }

                User user = User.builder()
                                .verificationId(request.verificationId())
                                .name(request.name())
                                .birthDate(LocalDate.parse(request.birthDate()))
                                .phoneNumber(request.phoneNumber())
                                .userId(request.userId())
                                .userPassword(passwordEncoder.encode(request.userPassword()))
                                .build();

                User savedUser = userRepository.save(user);

                return new UserRegisterResponse(
                                savedUser.getVerificationId(), savedUser.getName(), savedUser.getUserId());
        }

        public UserLoginResponse login(UserLoginRequest request) {
                User user = userRepository.findByUserId(request.userId())
                                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

                if (!passwordEncoder.matches(request.userPassword(), user.getUserPassword())) {
                        throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
                }

                String token = jwtTokenProvider.createAccessToken(user);
                return new UserLoginResponse(token, user.getName(), user.getVerificationId());
        }
}
