package oss.backend.domain.user.entity;

public enum Gender {
        MALE("M", "남성"),
        FEMALE("F", "여성");

        private final String code;
        private final String label;

        Gender(String code, String label) {
                this.code = code;
                this.label = label;
        }

        public String code() {
                return code;
        }

        public String label() {
                return label;
        }

        public static Gender fromCode(String code) {
                if (code == null) {
                        throw new IllegalArgumentException("성별은 필수입니다.");
                }
                for (Gender g : values()) {
                        if (g.code.equalsIgnoreCase(code) || g.name().equalsIgnoreCase(code)) {
                                return g;
                        }
                }
                throw new IllegalArgumentException("올바르지 않은 성별 값입니다: " + code);
        }
}
