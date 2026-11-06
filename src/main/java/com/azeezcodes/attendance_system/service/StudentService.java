package com.azeezcodes.attendance_system.service;

import com.azeezcodes.attendance_system.dto.StudentDto;
import com.azeezcodes.attendance_system.exception.ResourceNotFoundException;
import com.azeezcodes.attendance_system.model.ClassModel;
import com.azeezcodes.attendance_system.model.StudentModel;
import com.azeezcodes.attendance_system.repository.ClassRepository;
import com.azeezcodes.attendance_system.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    // POST   /api/students
    @Transactional
    public StudentDto.Response createStudent(StudentDto.Request request) {
        ClassModel schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));

        StudentModel saved = studentRepository.save(StudentModel.builder()
                .admissionNo(request.getAdmissionNo())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .schoolClass(schoolClass)
                .build());

        StudentDto.Response response = new StudentDto.Response();
        response.setId(saved.getId());
        response.setAdmissionNo(saved.getAdmissionNo());
        response.setFirstName(saved.getFirstName());
        response.setLastName(saved.getLastName());
        response.setClassId(saved.getSchoolClass().getId());
        response.setClassName(saved.getSchoolClass().getName());
        return response;
    }

    // GET    /api/students
    public List<StudentDto.Response> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> {
                    StudentDto.Response response = new StudentDto.Response();
                    response.setId(student.getId());
                    response.setAdmissionNo(student.getAdmissionNo());
                    response.setFirstName(student.getFirstName());
                    response.setLastName(student.getLastName());
                    response.setClassId(student.getSchoolClass().getId());
                    response.setClassName(student.getSchoolClass().getName());
                    return response;
                })
                .toList();
    }

    // GET    /api/students/{id}
    public StudentDto.Response getStudentById(Long id) {
        StudentModel student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        StudentDto.Response response = new StudentDto.Response();
        response.setId(student.getId());
        response.setAdmissionNo(student.getAdmissionNo());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setClassId(student.getSchoolClass().getId());
        response.setClassName(student.getSchoolClass().getName());
        return response;
    }

    // PUT    /api/students/{id}
    @Transactional
    public StudentDto.Response updateStudent(Long id, StudentDto.Request request) {
        StudentModel student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        ClassModel schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));

        student.setAdmissionNo(request.getAdmissionNo());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setSchoolClass(schoolClass);

        StudentModel saved = studentRepository.save(student);

        StudentDto.Response response = new StudentDto.Response();
        response.setId(saved.getId());
        response.setAdmissionNo(saved.getAdmissionNo());
        response.setFirstName(saved.getFirstName());
        response.setLastName(saved.getLastName());
        response.setClassId(saved.getSchoolClass().getId());
        response.setClassName(saved.getSchoolClass().getName());
        return response;
    }

    // DELETE /api/students/{id}
    @Transactional
    public void deleteStudent(Long id) {
        StudentModel student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
    }
    // GET    /api/students/search?query=
    public List<StudentDto.Response> searchStudents(String query) {
        return studentRepository.search(query).stream()
                .map(student -> {
                    StudentDto.Response response = new StudentDto.Response();
                    response.setId(student.getId());
                    response.setAdmissionNo(student.getAdmissionNo());
                    response.setFirstName(student.getFirstName());
                    response.setLastName(student.getLastName());
                    response.setClassId(student.getSchoolClass().getId());
                    response.setClassName(student.getSchoolClass().getName());
                    return response;
                })
                .toList();
    }
}
