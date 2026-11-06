package com.azeezcodes.attendance_system.controller;


import com.azeezcodes.attendance_system.dto.ClassDto;
import com.azeezcodes.attendance_system.dto.StudentDto;
import com.azeezcodes.attendance_system.dto.TeacherDto;
import com.azeezcodes.attendance_system.exception.ApiResponse;
import com.azeezcodes.attendance_system.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Classes", description = "Details of all the available classes")
public class ClassController {

    private final ClassService classService;
//    --- ClassController ---
//    POST   /api/classes                    [A]    create class
    @Operation(
            summary = "creating a class",
            description = "creating a class for students and teachers"
    )
@PreAuthorize("hasRole('ADMIN')")
@PostMapping
    public ResponseEntity<ApiResponse<ClassDto.Response>> createClass(@Valid @RequestBody ClassDto.Request request){
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Class created successfully", HttpStatus.CREATED.value(), classService.createClass(request) ));
}
//    GET    /api/classes                    [A/T]  list all classes
    @Operation(
            summary = "Get all classes",
            description = "to get all available class"
    )
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<ClassDto.Response>>> getAllClasses(){
        return ResponseEntity.ok(ApiResponse.success("All classes retrived successfully", HttpStatus.OK.value(), classService.getAllClasses()));
    }
//    GET    /api/classes/{id}               [A/T]  get one class
    @Operation(
            summary = "Get a class",
            description = "to get a  class"
    )
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassDto.Response>> getClass(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success("Class details retrived", HttpStatus.OK.value(), classService.getClassById(id) ));
    }
//    PUT    /api/classes/{id}               [A]    update class name
    @Operation(summary = "Edit a class", description = "Update a class name")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassDto.Response>> updateClass(@PathVariable Long id, @Valid @RequestBody ClassDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Class updated successfully", HttpStatus.OK.value(), classService.updateClass(id, request)));
    }
//DELETE /api/classes/{id}               [A]    delete class
    @Operation(summary = "Delete a class", description = "Deletes a class from the system")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(ApiResponse.success("Class deleted successfully", HttpStatus.OK.value(), null));
    }
//    GET    /api/classes/{id}/students      [A/T]  students in a class
    @Operation(summary = "Get students in a class", description = "Returns all students in a specific class")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/{id}/students")
    public ResponseEntity<ApiResponse<List<StudentDto.Response>>> getStudentsInClass(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", HttpStatus.OK.value(), classService.getStudentsInClass(id)));
    }
//    GET    /api/classes/{id}/teachers      [A]    teachers assigned to a class
    @Operation(summary = "Get teachers in a class", description = "Returns all teachers assigned to a specific class")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/teachers")
    public ResponseEntity<ApiResponse<List<TeacherDto.Response>>> getTeachersInClass(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Teachers retrieved successfully", HttpStatus.OK.value(), classService.getTeachersInClass(id)));
    }

//    PUT    /api/classes/{id}/students      [A]    move multiple students to this class
    @Operation(summary = "Assign students to a class", description = "Move multiple existing students to a class")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/students")
    public ResponseEntity<ApiResponse<List<StudentDto.Response>>> assignStudentsToClass(
            @PathVariable Long id,
            @Valid @RequestBody ClassDto.AssignStudentsRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Students assigned successfully", HttpStatus.OK.value(), classService.assignStudentsToClass(id, request)));
    }

}
