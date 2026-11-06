package com.azeezcodes.attendance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.azeezcodes.attendance_system.model.ClassModel;

public interface ClassRepository extends JpaRepository<ClassModel, Long> {

}
