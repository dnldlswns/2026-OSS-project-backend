package oss.backend.domain.user.dto;

public record UserRegisterRequest(
                String verificationId,
                String name,
                String birthDate,
                String phoneNumber,
                String id,
                String password) {

}
