package oss.backend.domain.verification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oss.backend.domain.verification.dto.VerificationCompleteRequest;
import oss.backend.domain.verification.dto.VerificationCompleteResponse;
import oss.backend.domain.verification.service.VerificationService;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

        private final VerificationService verificationService;

        @PostMapping("/complete")
        public ResponseEntity<VerificationCompleteResponse> complete(
                        @Valid @RequestBody VerificationCompleteRequest request) {
                return ResponseEntity.ok(verificationService.complete(request));
        }
}
