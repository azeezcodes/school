package com.azeezcodes.attendance_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    private AuthDto() {}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "LoginRequest")
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AuthResponse")
    public static class AuthResponse {
        private String token;
        private String email;
        private String role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ForgotPasswordRequest")
    public static class ForgotPasswordRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ForgotPasswordResponse")
    public static class ForgotPasswordResponse {
        private String resetToken;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ResetPasswordRequest")
    public static class ResetPasswordRequest {
        @NotBlank(message = "Reset token is required")
        private String resetToken;

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        private String newPassword;
    }
}
