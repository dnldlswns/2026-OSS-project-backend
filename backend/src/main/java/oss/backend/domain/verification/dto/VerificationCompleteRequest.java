package oss.backend.domain.verification.dto;

import jakarta.validation.constraints.NotBlank;

public record VerificationCompleteRequest(
                @NotBlank String identityVerificationId) {

}
