package oss.backend.domain.user.dto;

import jakarta.validation.constraints.Email;

public record ProfileUpdateRequest(
                String phone,
                @Email(message = "이메일 형식이 올바르지 않습니다.") String email,
                String nationality,
                String penName) {
}
