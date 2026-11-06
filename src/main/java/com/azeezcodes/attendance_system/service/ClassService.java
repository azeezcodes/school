package com.azeezcodes.attendance_system.service;

import com.azeezcodes.attendance_system.dto.ClassDto;
import com.azeezcodes.attendance_system.dto.StudentDto;
import com.azeezcodes.attendance_system.dto.TeacherDto;
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
public class ClassService {

    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;

    // POST   /api/classes
    @Transactional
    public ClassDto.Response createClass(ClassDto.Request request) {
        ClassModel saved = classRepository.save(ClassModel.builder()
                .name(request.getName())
                .build());

        ClassDto.Response response = new ClassDto.Response();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setTotalStudents(saved.getStudents() != null ? saved.getStudents().size() : 0);
        return response;
    }

    // GET    /api/classes
    public List<ClassDto.Response> getAllClasses() {
        return classRepository.findAll().stream()
                .map(c -> {
                    ClassDto.Response response = new ClassDto.Response();
                    response.setId(c.getId());
                    response.setName(c.getName());
                    response.setTotalStudents(c.getStudents() != null ? c.getStudents().size() : 0);
                    return response;
                })
                .toList();
    }

    // GET    /api/classes/{id}
    public ClassDto.Response getClassById(Long id) {
        ClassModel classModel = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));

        ClassDto.Response response = new ClassDto.Response();
        response.setId(classModel.getId());
        response.setName(classModel.getName());
        response.setTotalStudents(classModel.getStudents() != null ? classModel.getStudents().size() : 0);
        return response;
    }

    // GET    /api/classes/{id}/students
    public List<StudentDto.Response> getStudentsInClass(Long id) {
        ClassModel classModel = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));

        return classModel.getStudents().stream()
                .map(student -> {
                    StudentDto.Response response = new StudentDto.Response();
                    response.setId(student.getId());
                    response.setAdmissionNo(student.getAdmissionNo());
                    response.setFirstName(student.getFirstName());
                    response.setLastName(student.getLastName());
                    response.setClassId(classModel.getId());
                    response.setClassName(classModel.getName());
                    return response;
                })
                .toList();
    }

    // GET    /api/classes/{id}/teachers
    public List<TeacherDto.Response> getTeachersInClass(Long id) {
        ClassModel classModel = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));

        return classModel.getTeachers().stream()
                .map(teacher -> {
                    TeacherDto.Response response = new TeacherDto.Response();
                    response.setId(teacher.getId());
                    response.setFirstName(teacher.getFirstName());
                    response.setLastName(teacher.getLastName());
                    response.setEmail(teacher.getEmail());
                    response.setActive(teacher.isActive());
                    response.setClassId(classModel.getId());
                    response.setClassName(classModel.getName());
                    return response;
                })
                .toList();
    }

    // PUT    /api/classes/{id}
    @Transactional
    public ClassDto.Response updateClass(Long id, ClassDto.Request request) {
        ClassModel classFromDatabase = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));

        classFromDatabase.setName(request.getName());
        ClassModel saved = classRepository.save(classFromDatabase);

        ClassDto.Response response = new ClassDto.Response();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setTotalStudents(saved.getStudents() != null ? saved.getStudents().size() : 0);
        return response;
    }

    // PUT    /api/classes/{classId}/students
    @Transactional
    public List<StudentDto.Response> assignStudentsToClass(Long classId, ClassDto.AssignStudentsRequest request) {
        ClassModel classModel = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        List<StudentModel> students = studentRepository.findAllById(request.getStudentIds());

        if (students.size() != request.getStudentIds().size()) {
            throw new ResourceNotFoundException("One or more student IDs not found");
        }

        students.forEach(student -> student.setSchoolClass(classModel));
        studentRepository.saveAll(students);

        return students.stream()
                .map(student -> {
                    StudentDto.Response response = new StudentDto.Response();
                    response.setId(student.getId());
                    response.setAdmissionNo(student.getAdmissionNo());
                    response.setFirstName(student.getFirstName());
                    response.setLastName(student.getLastName());
                    response.setClassId(classModel.getId());
                    response.setClassName(classModel.getName());
                    return response;
                })
                .toList();
    }

    // DELETE /api/classes/{id}
    @Transactional
    public void deleteClass(Long id) {
        ClassModel classModel = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));
        classRepository.delete(classModel);
    }

}
