package oss.backend.domain.application.entity;

import java.time.LocalDate;
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
import oss.backend.domain.user.entity.User;

@Entity
@Table(name = "applications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 30)
        private String applyNo;

        @Column(nullable = false)
        private LocalDate applyDate;

        @Column(nullable = false, length = 50)
        private String type;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private ApplicationStatus status;

        @Column(nullable = false)
        private LocalDate statusDate;

        @Column(length = 1000)
        private String reason;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "applicant_id", nullable = false)
        private User applicant;

        @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ApplicationEntry> entries = new ArrayList<>();

        @Builder
        public Application(String applyNo,
                        LocalDate applyDate,
                        String type,
                        ApplicationStatus status,
                        LocalDate statusDate,
                        User applicant) {
                this.applyNo = applyNo;
                this.applyDate = applyDate;
                this.type = type;
                this.status = status;
                this.statusDate = statusDate;
                this.applicant = applicant;
        }

        public void addEntry(ApplicationEntry entry) {
                entries.add(entry);
                entry.assignApplication(this);
        }

        public void review(ApplicationStatus newStatus, String reason, LocalDate at) {
                this.status = newStatus;
                this.statusDate = at;
                this.reason = newStatus == ApplicationStatus.REJECTED ? reason : null;
        }
}
