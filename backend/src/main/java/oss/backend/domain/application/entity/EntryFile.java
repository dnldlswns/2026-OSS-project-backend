package oss.backend.domain.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entry_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntryFile {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "entry_id", nullable = false)
        private ApplicationEntry entry;

        @Column(nullable = false, length = 50)
        private String slot;

        @Column(nullable = false, length = 100)
        private String label;

        @Column(nullable = false, length = 255)
        private String filename;

        @Column(length = 500)
        private String storedPath;

        @Column(length = 500)
        private String url;

        @Builder
        public EntryFile(String slot, String label, String filename, String storedPath, String url) {
                this.slot = slot;
                this.label = label;
                this.filename = filename;
                this.storedPath = storedPath;
                this.url = url;
        }

        void assignEntry(ApplicationEntry entry) {
                this.entry = entry;
        }
}
