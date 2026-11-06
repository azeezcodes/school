package com.azeezcodes.attendance_system.controller;

import com.azeezcodes.attendance_system.dto.StudentDto;
import com.azeezcodes.attendance_system.exception.ApiResponse;
import com.azeezcodes.attendance_system.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "APIs for managing students")
public class StudentsController {

    private final StudentService studentService;

    // POST   /api/students
    @Operation(summary = "Create a student", description = "Registers a new student and assigns them to a class")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<StudentDto.Response>> createStudent(@Valid @RequestBody StudentDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created successfully", HttpStatus.CREATED.value(), studentService.createStudent(request)));
    }

    // GET    /api/students
    @Operation(summary = "Get all students", description = "Returns a list of all students")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentDto.Response>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", HttpStatus.OK.value(), studentService.getAllStudents()));
    }

    // GET    /api/students/{id}
    @Operation(summary = "Get student by ID", description = "Returns a specific student by their ID")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDto.Response>> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", HttpStatus.OK.value(), studentService.getStudentById(id)));
    }

    // PUT    /api/students/{id}
    @Operation(summary = "Update student", description = "Updates an existing student's details")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDto.Response>> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", HttpStatus.OK.value(), studentService.updateStudent(id, request)));
    }

    // DELETE /api/students/{id}
    @Operation(summary = "Delete student", description = "Deletes a student from the system")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", HttpStatus.OK.value(), null));
    }

    // GET    /api/students/search?query=
    @Operation(summary = "Search students", description = "Search students by name or admission number")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StudentDto.Response>>> searchStudents(@RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", HttpStatus.OK.value(), studentService.searchStudents(query)));
    }
}
