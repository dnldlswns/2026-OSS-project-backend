package oss.backend.domain.application.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import oss.backend.domain.application.entity.Application;
import oss.backend.domain.application.entity.ApplicationEntry;
import oss.backend.domain.application.entity.EntryFile;

public record ApplicationResponse(
                String id,
                String applyNo,
                String applyDate,
                String type,
                List<String> categories,
                String status,
                String statusDate,
                String reason,
                List<EntryDto> entries,
                String applicantName) {

        private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        public static ApplicationResponse from(Application app) {
                List<String> categories = app.getEntries().stream()
                                .map(ApplicationEntry::getCategory)
                                .distinct()
                                .toList();

                List<EntryDto> entries = app.getEntries().stream()
                                .map(EntryDto::from)
                                .toList();

                return new ApplicationResponse(
                                String.valueOf(app.getId()),
                                app.getApplyNo(),
                                format(app.getApplyDate()),
                                app.getType(),
                                categories,
                                app.getStatus().label(),
                                format(app.getStatusDate()),
                                app.getReason(),
                                entries,
                                app.getApplicant() == null ? null : app.getApplicant().getName());
        }

        private static String format(LocalDate d) {
                return d == null ? null : d.format(FMT);
        }

        public record EntryDto(
                        String category,
                        String title,
                        String publisher,
                        String date,
                        String genre,
                        String entryStatus,
                        String entryReason,
                        List<FileDto> files) {

                public static EntryDto from(ApplicationEntry entry) {
                        List<FileDto> files = entry.getFiles().stream()
                                        .map(FileDto::from)
                                        .collect(Collectors.toList());
                        return new EntryDto(
                                        entry.getCategory(),
                                        entry.getTitle(),
                                        entry.getPublisher(),
                                        entry.getDate(),
                                        entry.getGenre(),
                                        entry.getEntryStatus().label(),
                                        entry.getEntryReason(),
                                        files);
                }
        }

        public record FileDto(String label, String filename) {
                public static FileDto from(EntryFile f) {
                        return new FileDto(f.getLabel(), f.getFilename());
                }
        }
}
