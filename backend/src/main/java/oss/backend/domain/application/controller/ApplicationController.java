package oss.backend.domain.application.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oss.backend.domain.application.dto.ApplicationResponse;
import oss.backend.domain.application.dto.ReviewRequest;
import oss.backend.domain.application.dto.SubmitApplicationResponse;
import oss.backend.domain.application.service.ApplicationService;
import oss.backend.global.response.ApiResponse;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

        private final ApplicationService applicationService;

        @GetMapping
        public ResponseEntity<ApiResponse<List<ApplicationResponse>>> list(
                        @AuthenticationPrincipal String email) {
                return ResponseEntity.ok(ApiResponse.ok(applicationService.findAccessible(email)));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<SubmitApplicationResponse>> submit(
                        @AuthenticationPrincipal String email,
                        @RequestPart("data") String data,
                        MultipartHttpServletRequest request) {
                Map<String, MultipartFile> files = collectFiles(request);
                return ResponseEntity.ok(ApiResponse.ok(applicationService.submit(email, data, files)));
        }

        @PatchMapping("/{id}/review")
        public ResponseEntity<Void> review(
                        @AuthenticationPrincipal String email,
                        @PathVariable("id") Long id,
                        @Valid @RequestBody ReviewRequest request) {
                applicationService.review(email, id, request);
                return ResponseEntity.noContent().build();
        }

        private Map<String, MultipartFile> collectFiles(MultipartHttpServletRequest request) {
                Map<String, MultipartFile> result = new HashMap<>(request.getFileMap());
                result.remove("data");
                return result;
        }
}
