package oss.backend.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oss.backend.domain.auth.dto.LoginRequest;
import oss.backend.domain.auth.dto.LoginResponse;
import oss.backend.domain.auth.dto.PasswordChangeRequest;
import oss.backend.domain.auth.dto.SignupRequest;
import oss.backend.domain.auth.dto.SignupResponse;
import oss.backend.domain.auth.service.AuthService;
import oss.backend.global.response.ApiResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/signup")
        public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
                return ResponseEntity.ok(ApiResponse.ok(authService.signup(request)));
        }

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
                return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logout() {
                return ResponseEntity.noContent().build();
        }

        @PatchMapping("/password")
        public ResponseEntity<Void> changePassword(
                        @AuthenticationPrincipal String email,
                        @Valid @RequestBody PasswordChangeRequest request) {
                authService.changePassword(email, request);
                return ResponseEntity.noContent().build();
        }
}
