package com.azeezcodes.attendance_system.repository;

import com.azeezcodes.attendance_system.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleModel, Long> {

    Optional<RoleModel> findByName(RoleModel.RoleName name);
}
