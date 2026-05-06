package oss.backend.domain.auth.dto;

import oss.backend.domain.user.dto.UserResponse;

public record LoginResponse(UserResponse user, String token) {
}
