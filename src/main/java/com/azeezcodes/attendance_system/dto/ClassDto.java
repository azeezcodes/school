package com.azeezcodes.attendance_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ClassDto {

    private ClassDto() {}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ClassRequest")
    public static class Request {
        @NotBlank(message = "Class name is required")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ClassResponse")
    public static class Response {
        private Long id;
        private String name;
        private int totalStudents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AssignStudentsRequest")
    public static class AssignStudentsRequest {
        @NotEmpty(message = "Student IDs list must not be empty")
        private List<Long> studentIds;
    }
}
