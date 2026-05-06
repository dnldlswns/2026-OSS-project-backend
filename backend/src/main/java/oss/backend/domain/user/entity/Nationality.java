package oss.backend.domain.user.entity;

public enum Nationality {
        KOREAN, FOREIGN;

        public String code() {
                return name().toLowerCase();
        }

        public static Nationality fromCode(String code) {
                if (code == null) {
                        return KOREAN;
                }
                try {
                        return Nationality.valueOf(code.toUpperCase());
                } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("올바르지 않은 국적 값입니다: " + code);
                }
        }
}
