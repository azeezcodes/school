package com.azeezcodes.attendance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.azeezcodes.attendance_system.model.TeacherModel;

public interface TeacherRepository extends JpaRepository<TeacherModel, Long> {
    
}
