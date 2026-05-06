package oss.backend.domain.user.dto;

import oss.backend.domain.user.entity.User;

public record UserResponse(
                String name,
                String birth,
                String gender,
                String phone,
                String email,
                boolean isVerified,
                String nationality,
                String penName,
                String role) {

        public static UserResponse from(User user) {
                return new UserResponse(
                                user.getName(),
                                formatBirth(user.getBirthDate().toString()),
                                user.getGender().label(),
                                user.getPhoneNumber(),
                                user.getEmail(),
                                user.isVerified(),
                                user.getNationality().code(),
                                user.getPenName() == null ? "" : user.getPenName(),
                                user.getRole().code());
        }

        private static String formatBirth(String iso) {
                return iso == null ? null : iso.replace('-', '.');
        }
}
