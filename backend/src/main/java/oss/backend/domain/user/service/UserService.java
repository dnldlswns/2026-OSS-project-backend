package oss.backend.domain.user.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import oss.backend.domain.user.dto.UserLoginRequest;
import oss.backend.domain.user.dto.UserLoginResponse;
import oss.backend.domain.user.dto.UserRegisterRequest;
import oss.backend.domain.user.dto.UserRegisterResponse;

@Service
@RequiredArgsConstructor
public class UserService {

        public UserRegisterResponse register(UserRegisterRequest request) {

                return new UserRegisterResponse();
        }

        public UserLoginResponse login(UserLoginRequest request) {

                return new UserLoginResponse();
        }
}
