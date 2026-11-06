package com.azeezcodes.attendance_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.azeezcodes.attendance_system.model.StudentModel;

public interface StudentRepository extends JpaRepository<StudentModel, Long> {

    List<StudentModel> findBySchoolClass_Id(Long classId);

    @Query("SELECT s FROM StudentModel s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.admissionNo) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<StudentModel> search(@Param("query") String query);
}
