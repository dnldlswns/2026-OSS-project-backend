package oss.backend.domain.application.dto;

import java.util.List;
import java.util.Map;

public record SubmitApplicationRequest(
                String type,
                List<CategorySubmission> categories) {

        public record CategorySubmission(
                        String name,
                        List<Map<String, String>> entries) {
        }
}
