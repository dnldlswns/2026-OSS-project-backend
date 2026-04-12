package oss.backend.domain.user.dto;

public record UserRegisterRequest(
                String verificationId,
                String name,
                String birthDate,
                String phoneNumber,
                String userId,
                String userPassword) {

}
