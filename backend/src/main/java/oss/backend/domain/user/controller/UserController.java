package oss.backend.domain.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import oss.backend.domain.user.dto.UserLoginRequest;
import oss.backend.domain.user.dto.UserLoginResponse;
import oss.backend.domain.user.dto.UserRegisterRequest;
import oss.backend.domain.user.dto.UserRegisterResponse;
import oss.backend.domain.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;

        @PostMapping("/register")
        public ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegisterRequest request) {
                return ResponseEntity.ok(userService.register(request));
        }

        @PostMapping("/login")
        public ResponseEntity<UserLoginResponse> login(@RequestParam UserLoginRequest request) {
                return ResponseEntity.ok(userService.login(request));
        }

}
