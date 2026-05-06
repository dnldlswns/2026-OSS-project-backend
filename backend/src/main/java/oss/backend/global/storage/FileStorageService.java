package oss.backend.global.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

        private final Path root;
        private final String publicBaseUrl;

        public FileStorageService(
                        @Value("${app.upload.root:uploads}") String uploadRoot,
                        @Value("${app.upload.public-base-url:/files}") String publicBaseUrl) {
                this.root = Paths.get(uploadRoot).toAbsolutePath().normalize();
                this.publicBaseUrl = publicBaseUrl.endsWith("/")
                                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                                : publicBaseUrl;
                try {
                        Files.createDirectories(this.root);
                } catch (IOException e) {
                        throw new IllegalStateException("업로드 디렉터리를 생성할 수 없습니다.", e);
                }
        }

        public StoredFile store(String subDir, MultipartFile file) {
                if (file == null || file.isEmpty()) {
                        throw new IllegalArgumentException("업로드할 파일이 비어 있습니다.");
                }
                String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
                String ext = extractExtension(original);
                String stored = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

                Path dir = root.resolve(subDir).normalize();
                if (!dir.startsWith(root)) {
                        throw new IllegalArgumentException("잘못된 저장 경로입니다.");
                }
                try {
                        Files.createDirectories(dir);
                        Path target = dir.resolve(stored);
                        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                        String url = publicBaseUrl + "/" + subDir + "/" + stored;
                        return new StoredFile(original, stored, target.toString(), url);
                } catch (IOException e) {
                        throw new IllegalStateException("파일 저장에 실패했습니다.", e);
                }
        }

        private static String extractExtension(String filename) {
                int idx = filename.lastIndexOf('.');
                return idx < 0 ? "" : filename.substring(idx + 1);
        }

        public record StoredFile(String originalFilename, String storedFilename, String storedPath, String url) {
        }
}
