package com.azeezcodes.attendance_system.controller;

import com.azeezcodes.attendance_system.dto.AttendanceDto;
import com.azeezcodes.attendance_system.exception.ApiResponse;
import com.azeezcodes.attendance_system.exception.BadRequestException;
import com.azeezcodes.attendance_system.model.AttendanceModel;
import com.azeezcodes.attendance_system.model.UserModel;
import com.azeezcodes.attendance_system.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
@Tag(name = "Attendance", description = "API for managing attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    // POST /api/attendance
    @Operation(summary = "Mark attendance", description = "Mark attendance for a single student")
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> markAttendance(
            @Valid @RequestBody AttendanceDto.Request request,
            @AuthenticationPrincipal UserModel currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Attendance marked successfully", HttpStatus.CREATED.value(),
                        attendanceService.markAttendance(request, resolveTeacherId(currentUser))));
    }

    // POST /api/attendance/bulk
    @Operation(summary = "Mark bulk attendance", description = "Mark attendance for multiple students at once")
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> markBulkAttendance(
            @Valid @RequestBody AttendanceDto.BulkRequest request,
            @AuthenticationPrincipal UserModel currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bulk attendance marked successfully", HttpStatus.CREATED.value(),
                        attendanceService.markBulkAttendance(request, resolveTeacherId(currentUser))));
    }

    // GET /api/attendance?studentId=&classId=&date=&status=
    @Operation(summary = "Get attendance records", description = "List records filtered by studentId, classId, date, or status")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> getAllAttendance(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AttendanceModel.AttendanceStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Attendance records retrieved successfully", HttpStatus.OK.value(),
                attendanceService.getAllAttendance(studentId, classId, date, status)));
    }

    // GET /api/attendance/{id}
    @Operation(summary = "Get attendance by ID", description = "Returns a single attendance record")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Attendance record retrieved successfully", HttpStatus.OK.value(), attendanceService.getAttendanceById(id)));
    }

    // PUT /api/attendance/{id}
    @Operation(summary = "Update attendance", description = "Correct an existing attendance record")
    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceDto.Request request,
            @AuthenticationPrincipal UserModel currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", HttpStatus.OK.value(),
                attendanceService.updateAttendance(id, request, resolveTeacherId(currentUser))));
    }

    // DELETE /api/attendance/{id}
    @Operation(summary = "Delete attendance", description = "Delete an attendance record")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance record deleted successfully", HttpStatus.OK.value(), null));
    }

    // GET /api/attendance/student/{studentId}
    @Operation(summary = "Get student attendance history", description = "Returns all attendance records for a specific student")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> getStudentAttendance(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("Student attendance retrieved successfully", HttpStatus.OK.value(), attendanceService.getStudentAttendance(studentId)));
    }

    // GET /api/attendance/class/{classId}?date=
    @Operation(summary = "Get class attendance by date", description = "Returns attendance records for a class on a specific date")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> getClassAttendanceByDate(
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success("Class attendance retrieved successfully", HttpStatus.OK.value(),
                attendanceService.getClassAttendanceByDate(classId, date)));
    }

    // GET /api/attendance/report?classId=&startDate=&endDate=
    @Operation(summary = "Attendance summary report", description = "Per-student present/absent/late counts for a class within a date range")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/report")
    public ResponseEntity<ApiResponse<List<AttendanceDto.ReportResponse>>> getAttendanceReport(
            @RequestParam Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success("Attendance report generated successfully", HttpStatus.OK.value(),
                attendanceService.getAttendanceReport(classId, startDate, endDate)));
    }

    // GET /api/attendance/export?classId=&startDate=&endDate=&format=csv
    @Operation(summary = "Export attendance report", description = "Downloads the attendance summary report as a CSV file")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAttendanceReport(
            @RequestParam Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "csv") String format) {
        if (!"csv".equalsIgnoreCase(format)) {
            throw new BadRequestException("Unsupported export format: " + format + ". Only 'csv' is currently supported.");
        }

        byte[] csv = attendanceService.exportAttendanceReportCsv(classId, startDate, endDate).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    private Long resolveTeacherId(UserModel currentUser) {
        if (currentUser.getTeacher() == null) {
            throw new BadRequestException("No teacher profile linked to this account");
        }
        return currentUser.getTeacher().getId();
    }
}
