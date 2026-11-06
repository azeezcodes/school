package com.azeezcodes.attendance_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TeacherDto {

    private TeacherDto() {}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "TeacherRequest")
    public static class Request {

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        private Long classId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "TeacherResponse")
    public static class Response {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private Long classId;
        private String className;
        private boolean isActive;
    }
}
