package oss.backend.domain.verification.dto;

public record VerificationCompleteResponse(
                String verificationId,
                String name,
                String birthDate,
                String phoneNumber) {

}
