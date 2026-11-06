package com.azeezcodes.attendance_system.service;

import com.azeezcodes.attendance_system.dto.AttendanceDto;
import com.azeezcodes.attendance_system.exception.ResourceNotFoundException;
import com.azeezcodes.attendance_system.model.AttendanceModel;
import com.azeezcodes.attendance_system.model.StudentModel;
import com.azeezcodes.attendance_system.model.TeacherModel;
import com.azeezcodes.attendance_system.repository.AttendanceRepository;
import com.azeezcodes.attendance_system.repository.StudentRepository;
import com.azeezcodes.attendance_system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    // POST /api/attendance
    @Transactional
    public AttendanceDto.Response markAttendance(AttendanceDto.Request request, Long teacherId) {
        StudentModel student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        TeacherModel teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        AttendanceModel saved = attendanceRepository.save(AttendanceModel.builder()
                .student(student)
                .teacher(teacher)
                .date(request.getDate())
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .build());

        return toResponse(saved);
    }

    // POST /api/attendance/bulk
    @Transactional
    public List<AttendanceDto.Response> markBulkAttendance(AttendanceDto.BulkRequest request, Long teacherId) {
        TeacherModel teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        List<AttendanceModel> records = request.getRecords().stream()
                .map(entry -> {
                    StudentModel student = studentRepository.findById(entry.getStudentId())
                            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + entry.getStudentId()));

                    return AttendanceModel.builder()
                            .student(student)
                            .teacher(teacher)
                            .date(request.getDate())
                            .status(entry.getStatus())
                            .createdAt(LocalDateTime.now())
                            .build();
                })
                .toList();

        return attendanceRepository.saveAll(records).stream()
                .map(this::toResponse)
                .toList();
    }

    // GET /api/attendance
    public List<AttendanceDto.Response> getAllAttendance(Long studentId, Long classId, LocalDate date, AttendanceModel.AttendanceStatus status) {
        if (studentId != null) {
            return attendanceRepository.findByStudent_Id(studentId).stream().map(this::toResponse).toList();
        }
        if (classId != null && date != null) {
            return attendanceRepository.findByStudent_SchoolClass_IdAndDate(classId, date).stream().map(this::toResponse).toList();
        }
        if (classId != null) {
            return attendanceRepository.findByStudent_SchoolClass_Id(classId).stream().map(this::toResponse).toList();
        }
        if (date != null) {
            return attendanceRepository.findByDate(date).stream().map(this::toResponse).toList();
        }
        if (status != null) {
            return attendanceRepository.findByStatus(status).stream().map(this::toResponse).toList();
        }
        return attendanceRepository.findAll().stream().map(this::toResponse).toList();
    }

    // GET /api/attendance/{id}
    public AttendanceDto.Response getAttendanceById(Long id) {
        AttendanceModel record = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));
        return toResponse(record);
    }

    // PUT /api/attendance/{id}
    @Transactional
    public AttendanceDto.Response updateAttendance(Long id, AttendanceDto.Request request, Long teacherId) {
        AttendanceModel record = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        StudentModel student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        TeacherModel teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        record.setStudent(student);
        record.setTeacher(teacher);
        record.setDate(request.getDate());
        record.setStatus(request.getStatus());

        return toResponse(attendanceRepository.save(record));
    }

    // DELETE /api/attendance/{id}
    @Transactional
    public void deleteAttendance(Long id) {
        AttendanceModel record = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));
        attendanceRepository.delete(record);
    }

    // GET /api/attendance/student/{studentId}
    public List<AttendanceDto.Response> getStudentAttendance(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return attendanceRepository.findByStudent_Id(studentId).stream().map(this::toResponse).toList();
    }

    // GET /api/attendance/class/{classId}?date=
    public List<AttendanceDto.Response> getClassAttendanceByDate(Long classId, LocalDate date) {
        return attendanceRepository.findByStudent_SchoolClass_IdAndDate(classId, date).stream()
                .map(this::toResponse)
                .toList();
    }

    // GET /api/attendance/report?classId=&startDate=&endDate=
    public List<AttendanceDto.ReportResponse> getAttendanceReport(Long classId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceModel> records = attendanceRepository.findByStudent_SchoolClass_IdAndDateBetween(classId, startDate, endDate);

        Map<Long, List<AttendanceModel>> byStudent = records.stream()
                .collect(Collectors.groupingBy(r -> r.getStudent().getId()));

        return byStudent.values().stream()
                .map(this::toReportResponse)
                .toList();
    }

    // GET /api/attendance/export?classId=&startDate=&endDate=
    public String exportAttendanceReportCsv(Long classId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceDto.ReportResponse> report = getAttendanceReport(classId, startDate, endDate);

        StringBuilder csv = new StringBuilder("Student ID,First Name,Last Name,Present,Absent,Late,Total Days\n");
        for (AttendanceDto.ReportResponse row : report) {
            csv.append(row.getStudentId()).append(",")
                    .append(row.getStudentFirstName()).append(",")
                    .append(row.getStudentLastName()).append(",")
                    .append(row.getPresentCount()).append(",")
                    .append(row.getAbsentCount()).append(",")
                    .append(row.getLateCount()).append(",")
                    .append(row.getTotalDays()).append("\n");
        }
        return csv.toString();
    }

    private AttendanceDto.ReportResponse toReportResponse(List<AttendanceModel> studentRecords) {
        StudentModel student = studentRecords.get(0).getStudent();
        long present = studentRecords.stream().filter(r -> r.getStatus() == AttendanceModel.AttendanceStatus.PRESENT).count();
        long absent = studentRecords.stream().filter(r -> r.getStatus() == AttendanceModel.AttendanceStatus.ABSENT).count();
        long late = studentRecords.stream().filter(r -> r.getStatus() == AttendanceModel.AttendanceStatus.LATE).count();

        AttendanceDto.ReportResponse response = new AttendanceDto.ReportResponse();
        response.setStudentId(student.getId());
        response.setStudentFirstName(student.getFirstName());
        response.setStudentLastName(student.getLastName());
        response.setPresentCount(present);
        response.setAbsentCount(absent);
        response.setLateCount(late);
        response.setTotalDays(studentRecords.size());
        return response;
    }

    private AttendanceDto.Response toResponse(AttendanceModel record) {
        AttendanceDto.Response response = new AttendanceDto.Response();
        response.setId(record.getId());
        response.setStudentId(record.getStudent().getId());
        response.setStudentFirstName(record.getStudent().getFirstName());
        response.setStudentLastName(record.getStudent().getLastName());
        response.setTeacherId(record.getTeacher().getId());
        response.setTeacherFirstName(record.getTeacher().getFirstName());
        response.setTeacherLastName(record.getTeacher().getLastName());
        response.setDate(record.getDate());
        response.setStatus(record.getStatus().name());
        response.setCreatedAt(record.getCreatedAt());
        return response;
    }
}
