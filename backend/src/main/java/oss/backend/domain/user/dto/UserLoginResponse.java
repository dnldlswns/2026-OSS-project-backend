package oss.backend.domain.user.dto;

public record UserLoginResponse(String token, String name, String verificationId) {

}
