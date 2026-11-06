package com.azeezcodes.attendance_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StudentDto {

    private StudentDto() {}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "StudentRequest")
    public static class Request {

        @NotBlank(message = "Admission number is required")
        private String admissionNo;

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotNull(message = "Class ID is required")
        private Long classId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "StudentResponse")
    public static class Response {
        private Long id;
        private String admissionNo;
        private String firstName;
        private String lastName;
        private Long classId;
        private String className;
    }
}
