package oss.backend.domain.user.entity;

public enum Role {
        USER, ADMIN;

        public String code() {
                return name().toLowerCase();
        }
}
