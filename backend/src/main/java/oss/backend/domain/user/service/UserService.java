package oss.backend.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import oss.backend.domain.user.dto.PhotoUploadResponse;
import oss.backend.domain.user.dto.ProfileUpdateRequest;
import oss.backend.domain.user.entity.Nationality;
import oss.backend.domain.user.entity.User;
import oss.backend.domain.user.repository.UserRepository;
import oss.backend.global.storage.FileStorageService;
import oss.backend.global.storage.FileStorageService.StoredFile;

@Service
@RequiredArgsConstructor
public class UserService {

        private final UserRepository userRepository;
        private final FileStorageService fileStorageService;

        @Transactional
        public void updateProfile(String email, ProfileUpdateRequest request) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                if (request.email() != null && !request.email().equals(user.getEmail())
                                && userRepository.existsByEmail(request.email())) {
                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                }

                user.updateProfile(
                                request.phone(),
                                request.email(),
                                request.nationality() == null ? null : Nationality.fromCode(request.nationality()),
                                request.penName());
        }

        @Transactional
        public PhotoUploadResponse uploadPhoto(String email, MultipartFile photo) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                StoredFile stored = fileStorageService.store("user-photos/" + user.getId(), photo);
                user.updateProfileImage(stored.url());
                return new PhotoUploadResponse(stored.url());
        }
}
