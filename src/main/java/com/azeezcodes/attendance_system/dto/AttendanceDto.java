package com.azeezcodes.attendance_system.dto;

import com.azeezcodes.attendance_system.model.AttendanceModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AttendanceDto {

    private AttendanceDto() {}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AttendanceRequest")
    public static class Request {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Date is required")
        private LocalDate date;

        @NotNull(message = "Status is required")
        private AttendanceModel.AttendanceStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "BulkAttendanceRequest")
    public static class BulkRequest {
        @NotNull(message = "Class ID is required")
        private Long classId;

        @NotNull(message = "Date is required")
        private LocalDate date;

        @NotEmpty(message = "Records list must not be empty")
        private List<StudentAttendanceEntry> records;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class StudentAttendanceEntry {
            @NotNull(message = "Student ID is required")
            private Long studentId;

            @NotNull(message = "Status is required")
            private AttendanceModel.AttendanceStatus status;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AttendanceResponse")
    public static class Response {
        private Long id;
        private Long studentId;
        private String studentFirstName;
        private String studentLastName;
        private Long teacherId;
        private String teacherFirstName;
        private String teacherLastName;
        private LocalDate date;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AttendanceReportResponse")
    public static class ReportResponse {
        private Long studentId;
        private String studentFirstName;
        private String studentLastName;
        private long presentCount;
        private long absentCount;
        private long lateCount;
        private long totalDays;
    }
}
