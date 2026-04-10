package oss.backend.global.response;

public record ApiResponse<T>(
                boolean success,
                T data,
                String message) {
        public static <T> ApiResponse<T> ok(T data) {
                return new ApiResponse<T>(true, data, null);
        }

        public static <T> ApiResponse<T> fail(String message) {
                return new ApiResponse<T>(false, null, message);
        }
}
