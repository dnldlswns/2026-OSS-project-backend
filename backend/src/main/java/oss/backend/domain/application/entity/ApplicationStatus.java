package oss.backend.domain.application.entity;

public enum ApplicationStatus {
        REVIEWING("심사중"),
        APPROVED("승인"),
        REJECTED("반려");

        private final String label;

        ApplicationStatus(String label) {
                this.label = label;
        }

        public String label() {
                return label;
        }

        public static ApplicationStatus fromLabel(String label) {
                if (label == null) {
                        throw new IllegalArgumentException("상태 값은 필수입니다.");
                }
                for (ApplicationStatus s : values()) {
                        if (s.label.equals(label) || s.name().equalsIgnoreCase(label)) {
                                return s;
                        }
                }
                throw new IllegalArgumentException("올바르지 않은 상태 값입니다: " + label);
        }
}
