package oss.backend.domain.user.dto;

public record UserLoginRequest(
                String userId,
                String userPassword) {

}
