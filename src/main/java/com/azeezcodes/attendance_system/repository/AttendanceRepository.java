package com.azeezcodes.attendance_system.repository;

import com.azeezcodes.attendance_system.model.AttendanceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceModel, Long> {

    List<AttendanceModel> findByStudent_Id(Long studentId);

    List<AttendanceModel> findByStudent_SchoolClass_IdAndDate(Long classId, LocalDate date);

    List<AttendanceModel> findByDate(LocalDate date);

    List<AttendanceModel> findByStudent_SchoolClass_Id(Long classId);

    List<AttendanceModel> findByStatus(AttendanceModel.AttendanceStatus status);

    List<AttendanceModel> findByStudent_SchoolClass_IdAndDateBetween(Long classId, LocalDate startDate, LocalDate endDate);
}
