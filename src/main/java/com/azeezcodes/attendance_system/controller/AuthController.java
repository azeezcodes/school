package com.azeezcodes.attendance_system.controller;

import com.azeezcodes.attendance_system.dto.AuthDto;
import com.azeezcodes.attendance_system.dto.UserDto;
import com.azeezcodes.attendance_system.exception.ApiResponse;
import com.azeezcodes.attendance_system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "APIs for registration and login")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a user account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto.Response>> register(@Valid @RequestBody UserDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", HttpStatus.CREATED.value(), authService.register(request)));
    }

    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", HttpStatus.OK.value(), authService.login(request)));
    }

    @Operation(summary = "Forgot password", description = "Generates a password reset token for the given email (dev-only: returned directly, not emailed)")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<AuthDto.ForgotPasswordResponse>> forgotPassword(@Valid @RequestBody AuthDto.ForgotPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Reset token generated successfully", HttpStatus.OK.value(), authService.forgotPassword(request)));
    }

    @Operation(summary = "Reset password", description = "Sets a new password using a valid reset token")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody AuthDto.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", HttpStatus.OK.value(), null));
    }
}
