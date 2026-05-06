package oss.backend.domain.auth.dto;

import oss.backend.domain.user.dto.UserResponse;

public record SignupResponse(UserResponse user) {
}
