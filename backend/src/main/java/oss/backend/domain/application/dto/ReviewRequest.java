package oss.backend.domain.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
                @NotBlank(message = "상태 값은 필수입니다.") String status,
                String reason) {
}
