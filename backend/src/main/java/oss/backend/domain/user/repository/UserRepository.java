package oss.backend.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import oss.backend.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
        boolean existsByUserId(String userId);

        Optional<User> findByUserId(String userId);
}
