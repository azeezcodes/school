package com.azeezcodes.attendance_system.dto;

import com.azeezcodes.attendance_system.model.RoleModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

    private UserDto() {}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserRequest")
    public static class Request {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotNull(message = "Role is required")
        private RoleModel.RoleName role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserResponse")
    public static class Response {
        private Long id;
        private String email;
        private String role;
        private boolean enabled;
        private Long teacherId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ChangePasswordRequest")
    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        private String newPassword;
    }
}