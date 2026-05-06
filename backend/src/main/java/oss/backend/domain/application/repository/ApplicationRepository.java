package oss.backend.domain.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import oss.backend.domain.application.entity.Application;
import oss.backend.domain.user.entity.User;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
        List<Application> findAllByOrderByApplyDateDesc();

        List<Application> findAllByApplicantOrderByApplyDateDesc(User applicant);
}
