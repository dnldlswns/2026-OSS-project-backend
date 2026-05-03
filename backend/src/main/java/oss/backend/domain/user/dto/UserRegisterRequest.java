package oss.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(
                @NotBlank(message = "본인인증 ID는 필수입니다.") String verificationId,
                @NotBlank(message = "이름은 필수입니다.") String name,
                @NotBlank(message = "생년월일은 필수입니다.") String birthDate,
                @NotBlank(message = "전화번호는 필수입니다.") String phoneNumber,
                @NotBlank(message = "아이디는 필수입니다.") String userId,
                @NotBlank(message = "비밀번호는 필수입니다.") String userPassword) {

}
