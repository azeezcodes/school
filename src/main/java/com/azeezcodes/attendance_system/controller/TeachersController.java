package com.azeezcodes.attendance_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.azeezcodes.attendance_system.dto.TeacherDto;
import com.azeezcodes.attendance_system.exception.ApiResponse;
import com.azeezcodes.attendance_system.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "APIs for managing teachers")
public class TeachersController {

    private final TeacherService teacherService;

    // POST   /api/teachers
    @Operation(
            summary = "Create a teacher",
            description = "Creates a new teacher and assigns the teacher to a class."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TeacherDto.Response>> addTeacher(@Valid @RequestBody TeacherDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Teacher created successfully", HttpStatus.CREATED.value(), teacherService.createTeacher(request)));
    }

    // GET    /api/teachers
    @Operation(
            summary = "Get all teachers",
            description = "Retrieves a list of all teachers registered in the school."
    )
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherDto.Response>>> getAllTeachers() {
        return ResponseEntity.ok(ApiResponse.success("Teachers retrieved successfully", HttpStatus.OK.value(), teacherService.getAllTeachers()));
    }

    // GET    /api/teachers/{id}
    @Operation(
            summary = "Get teacher by ID",
            description = "Retrieves a specific teacher using their unique ID."
    )
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherDto.Response>> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Teacher retrieved successfully", HttpStatus.OK.value(), teacherService.getTeacherById(id)));
    }

    // PUT    /api/teachers/{id}
    @Operation(
            summary = "Edit a teacher",
            description = "Updates the details of an existing teacher."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherDto.Response>> updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Teacher updated successfully", HttpStatus.OK.value(), teacherService.updateTeacher(id, request)));
    }

    // DELETE /api/teachers/{id}
    @Operation(
            summary = "Delete a teacher",
            description = "Deletes a teacher from the school system."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok(ApiResponse.success("Teacher deleted successfully", HttpStatus.OK.value(), null));
    }

    // PUT    /api/teachers/{id}/deactivate
    @Operation(
            summary = "Deactivate a teacher",
            description = "Deactivates an active teacher without deleting their record."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<TeacherDto.Response>> deactivateTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Teacher deactivated successfully", HttpStatus.OK.value(), teacherService.deactivateTeacher(id)));
    }

    // PUT    /api/teachers/{id}/activate
    @Operation(
            summary = "Activate a teacher",
            description = "Activates an active teacher without deleting their record."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<TeacherDto.Response>> activateTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Teacher activated successfully", HttpStatus.OK.value(), teacherService.activateTeacher(id)));
    }

    // PUT    /api/teachers/{id}/assign-class/{classId}
    @Operation(summary = "Assign teacher to a class", description = "Assigns an existing teacher to a class.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/assign-class/{classId}")
    public ResponseEntity<ApiResponse<TeacherDto.Response>> assignClass(@PathVariable Long id, @PathVariable Long classId) {
        return ResponseEntity.ok(ApiResponse.success("Teacher assigned to class successfully", HttpStatus.OK.value(), teacherService.assignClass(id, classId)));
    }

}
