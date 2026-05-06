package oss.backend.domain.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import oss.backend.domain.application.dto.ApplicationResponse;
import oss.backend.domain.application.dto.ReviewRequest;
import oss.backend.domain.application.dto.SubmitApplicationRequest;
import oss.backend.domain.application.dto.SubmitApplicationRequest.CategorySubmission;
import oss.backend.domain.application.dto.SubmitApplicationResponse;
import oss.backend.domain.application.entity.Application;
import oss.backend.domain.application.entity.ApplicationEntry;
import oss.backend.domain.application.entity.ApplicationStatus;
import oss.backend.domain.application.entity.EntryFile;
import oss.backend.domain.application.repository.ApplicationRepository;
import oss.backend.domain.user.entity.Role;
import oss.backend.domain.user.entity.User;
import oss.backend.domain.user.repository.UserRepository;
import oss.backend.global.storage.FileStorageService;
import oss.backend.global.storage.FileStorageService.StoredFile;

@Service
@RequiredArgsConstructor
public class ApplicationService {

        private final ApplicationRepository applicationRepository;
        private final UserRepository userRepository;
        private final FileStorageService fileStorageService;

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
        private static final Random RNG = new Random();

        @Transactional(readOnly = true)
        public List<ApplicationResponse> findAccessible(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                List<Application> apps = user.getRole() == Role.ADMIN
                                ? applicationRepository.findAllByOrderByApplyDateDesc()
                                : applicationRepository.findAllByApplicantOrderByApplyDateDesc(user);

                return apps.stream().map(ApplicationResponse::from).toList();
        }

        @Transactional
        public SubmitApplicationResponse submit(String email, String dataJson, Map<String, MultipartFile> files) {
                User applicant = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                SubmitApplicationRequest payload = parsePayload(dataJson);
                if (payload.type() == null || payload.type().isBlank()) {
                        throw new IllegalArgumentException("신청 유형은 필수입니다.");
                }
                if (payload.categories() == null || payload.categories().isEmpty()) {
                        throw new IllegalArgumentException("카테고리는 한 개 이상 필요합니다.");
                }

                LocalDate today = LocalDate.now();
                Application application = Application.builder()
                                .applyNo(generateApplyNo(today))
                                .applyDate(today)
                                .type(payload.type())
                                .status(ApplicationStatus.REVIEWING)
                                .statusDate(today)
                                .applicant(applicant)
                                .build();

                for (CategorySubmission category : payload.categories()) {
                        if (category.name() == null || category.name().isBlank()) {
                                throw new IllegalArgumentException("카테고리 이름은 필수입니다.");
                        }
                        if (category.entries() == null) {
                                continue;
                        }
                        for (int idx = 0; idx < category.entries().size(); idx++) {
                                Map<String, String> raw = category.entries().get(idx);
                                ApplicationEntry entry = toEntry(category.name(), raw);
                                application.addEntry(entry);

                                attachFiles(application, entry, category.name(), idx, files);
                        }
                }

                Application saved = applicationRepository.save(application);
                return new SubmitApplicationResponse(String.valueOf(saved.getId()));
        }

        @Transactional
        public void review(String email, Long applicationId, ReviewRequest request) {
                User actor = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                if (actor.getRole() != Role.ADMIN) {
                        throw new IllegalArgumentException("관리자만 검토할 수 있습니다.");
                }

                Application application = applicationRepository.findById(applicationId)
                                .orElseThrow(() -> new IllegalArgumentException("신청 건을 찾을 수 없습니다."));

                ApplicationStatus newStatus = ApplicationStatus.fromLabel(request.status());
                if (newStatus == ApplicationStatus.REVIEWING) {
                        throw new IllegalArgumentException("승인 또는 반려 상태만 지정할 수 있습니다.");
                }
                application.review(newStatus, request.reason(), LocalDate.now());
        }

        private SubmitApplicationRequest parsePayload(String dataJson) {
                if (dataJson == null || dataJson.isBlank()) {
                        throw new IllegalArgumentException("data 필드가 비어 있습니다.");
                }
                try {
                        return OBJECT_MAPPER.readValue(dataJson, SubmitApplicationRequest.class);
                } catch (JsonProcessingException e) {
                        throw new IllegalArgumentException("data 필드의 JSON 형식이 올바르지 않습니다.");
                }
        }

        private ApplicationEntry toEntry(String category, Map<String, String> raw) {
                String title = firstNonBlank(raw, "title", "programTitle");
                String publisher = firstNonBlank(raw, "publisher", "venue", "broadcaster", "company");
                String date = firstNonBlank(raw, "date", "publishDate");
                String genre = firstNonBlank(raw, "genre", "method", "role", "programType");

                return ApplicationEntry.builder()
                                .category(category)
                                .title(title)
                                .publisher(publisher)
                                .date(date)
                                .genre(genre)
                                .entryStatus(ApplicationStatus.REVIEWING)
                                .build();
        }

        private void attachFiles(Application application, ApplicationEntry entry, String categoryName,
                        int entryIdx, Map<String, MultipartFile> files) {
                if (files == null || files.isEmpty()) {
                        return;
                }
                String prefix = categoryName + "[" + entryIdx + "].";
                for (Map.Entry<String, MultipartFile> e : files.entrySet()) {
                        if (!e.getKey().startsWith(prefix)) {
                                continue;
                        }
                        String slot = e.getKey().substring(prefix.length());
                        MultipartFile mf = e.getValue();
                        if (mf == null || mf.isEmpty()) {
                                continue;
                        }
                        StoredFile stored = fileStorageService.store(
                                        "applications/" + application.getApplicant().getId() + "/" + application.getApplyNo(), mf);
                        EntryFile file = EntryFile.builder()
                                        .slot(slot)
                                        .label(slotLabel(slot))
                                        .filename(stored.originalFilename())
                                        .storedPath(stored.storedPath())
                                        .url(stored.url())
                                        .build();
                        entry.addFile(file);
                }
        }

        private static String slotLabel(String slot) {
                return switch (slot) {
                        case "workImage" -> "작품 이미지";
                        case "detailPage1" -> "세부 1";
                        case "detailPage2" -> "세부 2";
                        case "income" -> "소득 증빙";
                        case "other" -> "기타";
                        default -> slot;
                };
        }

        private static String firstNonBlank(Map<String, String> map, String... keys) {
                for (String k : keys) {
                        String v = map.get(k);
                        if (v != null && !v.isBlank()) {
                                return v;
                        }
                }
                return "";
        }

        private static String generateApplyNo(LocalDate date) {
                int n = RNG.nextInt(1_000_000);
                return String.format("%d-ART-%06d", date.getYear(), n);
        }
}
