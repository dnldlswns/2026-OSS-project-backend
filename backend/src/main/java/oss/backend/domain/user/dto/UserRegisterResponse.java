package oss.backend.domain.user.dto;

public record UserRegisterResponse(
                String verificationId,
                String name,
                String userId) {
}
