package com.azeezcodes.attendance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.azeezcodes.attendance_system.model.UserModel;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserModel> findByResetToken(String resetToken);
}
