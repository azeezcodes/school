package com.azeezcodes.attendance_system.controller;

import com.azeezcodes.attendance_system.dto.UserDto;
import com.azeezcodes.attendance_system.exception.ApiResponse;
import com.azeezcodes.attendance_system.model.UserModel;
import com.azeezcodes.attendance_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Users", description = "Admin APIs for managing user accounts")
public class UserController {

    private final UserService userService;

    // GET /api/users
    @Operation(summary = "Get all users", description = "Returns a list of all user accounts")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto.Response>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", HttpStatus.OK.value(), userService.getAllUsers()));
    }

    // GET /api/users/{id}
    @Operation(summary = "Get user by ID", description = "Returns a specific user account")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto.Response>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", HttpStatus.OK.value(), userService.getUserById(id)));
    }

    // PUT /api/users/{id}/deactivate
    @Operation(summary = "Deactivate a user", description = "Disables a user account without deleting it")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserDto.Response>> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", HttpStatus.OK.value(), userService.deactivateUser(id)));
    }

    // PUT /api/users/{id}/activate
    @Operation(summary = "Activate a user", description = "Re-enables a previously deactivated user account")
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<UserDto.Response>> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", HttpStatus.OK.value(), userService.activateUser(id)));
    }

    // DELETE /api/users/{id}
    @Operation(summary = "Delete a user", description = "Permanently deletes a user account")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", HttpStatus.OK.value(), null));
    }

    // PUT /api/users/{id}/link-teacher/{teacherId}
    @Operation(summary = "Link user to teacher profile", description = "Connects a TEACHER-role login account to its TeacherModel record")
    @PutMapping("/{id}/link-teacher/{teacherId}")
    public ResponseEntity<ApiResponse<UserDto.Response>> linkTeacher(@PathVariable Long id, @PathVariable Long teacherId) {
        return ResponseEntity.ok(ApiResponse.success("User linked to teacher successfully", HttpStatus.OK.value(), userService.linkTeacher(id, teacherId)));
    }

    // PUT /api/users/me/change-password
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change my password", description = "Allows the logged-in user to change their own password")
    @PutMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody UserDto.ChangePasswordRequest request,
            @AuthenticationPrincipal UserModel currentUser) {
        userService.changePassword(currentUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", HttpStatus.OK.value(), null));
    }
}
