package oss.backend.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oss.backend.domain.user.dto.PhotoUploadResponse;
import oss.backend.domain.user.dto.ProfileUpdateRequest;
import oss.backend.domain.user.service.UserService;
import oss.backend.global.response.ApiResponse;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;

        @PatchMapping("/profile")
        public ResponseEntity<Void> updateProfile(
                        @AuthenticationPrincipal String email,
                        @Valid @RequestBody ProfileUpdateRequest request) {
                userService.updateProfile(email, request);
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/photo")
        public ResponseEntity<ApiResponse<PhotoUploadResponse>> uploadPhoto(
                        @AuthenticationPrincipal String email,
                        @RequestPart("photo") MultipartFile photo) {
                return ResponseEntity.ok(ApiResponse.ok(userService.uploadPhoto(email, photo)));
        }
}
