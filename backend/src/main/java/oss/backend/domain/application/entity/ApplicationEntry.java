package oss.backend.domain.application.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "application_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationEntry {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "application_id", nullable = false)
        private Application application;

        @Column(nullable = false, length = 50)
        private String category;

        @Column(length = 200)
        private String title;

        @Column(length = 200)
        private String publisher;

        @Column(length = 50)
        private String date;

        @Column(length = 50)
        private String genre;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private ApplicationStatus entryStatus;

        @Column(length = 1000)
        private String entryReason;

        @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<EntryFile> files = new ArrayList<>();

        @Builder
        public ApplicationEntry(String category,
                        String title,
                        String publisher,
                        String date,
                        String genre,
                        ApplicationStatus entryStatus) {
                this.category = category;
                this.title = title;
                this.publisher = publisher;
                this.date = date;
                this.genre = genre;
                this.entryStatus = entryStatus == null ? ApplicationStatus.REVIEWING : entryStatus;
        }

        void assignApplication(Application application) {
                this.application = application;
        }

        public void addFile(EntryFile file) {
                files.add(file);
                file.assignEntry(this);
        }
}
