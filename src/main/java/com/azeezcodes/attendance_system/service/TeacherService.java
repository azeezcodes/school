package com.azeezcodes.attendance_system.service;

import com.azeezcodes.attendance_system.dto.TeacherDto;
import com.azeezcodes.attendance_system.exception.ResourceNotFoundException;
import com.azeezcodes.attendance_system.model.ClassModel;
import com.azeezcodes.attendance_system.model.TeacherModel;
import com.azeezcodes.attendance_system.repository.ClassRepository;
import com.azeezcodes.attendance_system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;


    // POST   /api/teachers                   [A]    create teacher
    @Transactional
    public TeacherDto.Response createTeacher(TeacherDto.Request request) {
        ClassModel schoolClass = null;
        if (request.getClassId() != null) {
            schoolClass = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));
        }

        TeacherModel saved = teacherRepository.save(TeacherModel.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .schoolClass(schoolClass)
                .build());

        TeacherDto.Response response = new TeacherDto.Response();
        response.setId(saved.getId());
        response.setFirstName(saved.getFirstName());
        response.setLastName(saved.getLastName());
        response.setEmail(saved.getEmail());
        response.setActive(saved.isActive());
        if (saved.getSchoolClass() != null) {
            response.setClassId(saved.getSchoolClass().getId());
            response.setClassName(saved.getSchoolClass().getName());
        }
        return response;
    }


    // GET    /api/teachers                   [A]    list all teachers
    public List<TeacherDto.Response> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(teacher -> {
                    TeacherDto.Response response = new TeacherDto.Response();
                    response.setId(teacher.getId());
                    response.setFirstName(teacher.getFirstName());
                    response.setLastName(teacher.getLastName());
                    response.setEmail(teacher.getEmail());
                    response.setActive(teacher.isActive());
                    if (teacher.getSchoolClass() != null) {
                        response.setClassId(teacher.getSchoolClass().getId());
                        response.setClassName(teacher.getSchoolClass().getName());
                    }
                    return response;
                })
                .toList();
    }

    // GET    /api/teachers/{id}              [A]    get one teacher
    public TeacherDto.Response getTeacherById(Long id) {
        TeacherModel teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        TeacherDto.Response response = new TeacherDto.Response();
        response.setId(teacher.getId());
        response.setFirstName(teacher.getFirstName());
        response.setLastName(teacher.getLastName());
        response.setEmail(teacher.getEmail());
        response.setActive(teacher.isActive());
        if (teacher.getSchoolClass() != null) {
            response.setClassId(teacher.getSchoolClass().getId());
            response.setClassName(teacher.getSchoolClass().getName());
        }
        return response;
    }

    // DELETE /api/teachers/{id}              [A]    delete teacher
    @Transactional
    public void deleteTeacher(Long id) {
        TeacherModel teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        teacherRepository.delete(teacher);
    }

    // PUT    /api/teachers/{id}              [A]    update teacher info
    @Transactional
    public TeacherDto.Response updateTeacher(Long id, TeacherDto.Request request) {
        TeacherModel teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        teacher.setFirstName(request.getFirstName());
        teacher.setLastName(request.getLastName());
        teacher.setEmail(request.getEmail());

        if (request.getClassId() != null) {
            ClassModel schoolClass = classRepository.findById(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));
            teacher.setSchoolClass(schoolClass);
        }

        TeacherModel saved = teacherRepository.save(teacher);

        TeacherDto.Response response = new TeacherDto.Response();
        response.setId(saved.getId());
        response.setFirstName(saved.getFirstName());
        response.setLastName(saved.getLastName());
        response.setEmail(saved.getEmail());
        response.setActive(saved.isActive());
        if (saved.getSchoolClass() != null) {
            response.setClassId(saved.getSchoolClass().getId());
            response.setClassName(saved.getSchoolClass().getName());
        }
        return response;
    }

    // PUT    /api/teachers/{id}/assign-class/{classId}
    @Transactional
    public TeacherDto.Response assignClass(Long teacherId, Long classId) {
        TeacherModel teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        ClassModel schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        teacher.setSchoolClass(schoolClass);
        TeacherModel saved = teacherRepository.save(teacher);

        TeacherDto.Response response = new TeacherDto.Response();
        response.setId(saved.getId());
        response.setFirstName(saved.getFirstName());
        response.setLastName(saved.getLastName());
        response.setEmail(saved.getEmail());
        response.setActive(saved.isActive());
        response.setClassId(saved.getSchoolClass().getId());
        response.setClassName(saved.getSchoolClass().getName());
        return response;
    }

    // DEACTIVATE /api/teachers/{id}           [A]    deactivate teacher
    @Transactional
    public TeacherDto.Response deactivateTeacher(Long id) {
        TeacherModel teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        teacher.setActive(false);
        TeacherModel saved = teacherRepository.save(teacher);

        TeacherDto.Response response = new TeacherDto.Response();
        response.setId(saved.getId());
        response.setFirstName(saved.getFirstName());
        response.setLastName(saved.getLastName());
        response.setEmail(saved.getEmail());
        response.setActive(saved.isActive());
        if (saved.getSchoolClass() != null) {
            response.setClassId(saved.getSchoolClass().getId());
            response.setClassName(saved.getSchoolClass().getName());
        }
        return response;
    }

    // ACTIVATE /api/teachers/{id}           [A]    deactivate teacher
    @Transactional
    public TeacherDto.Response activateTeacher(Long id) {
        TeacherModel teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        teacher.setActive(true);
        TeacherModel savedData = teacherRepository.save(teacher);

        TeacherDto.Response response = new TeacherDto.Response();
        response.setId(savedData.getId());
        response.setFirstName(savedData.getFirstName());
        response.setLastName(savedData.getLastName());
        response.setEmail(savedData.getEmail());
        response.setActive(savedData.isActive());
        if (savedData.getSchoolClass() != null) {
            response.setClassId(savedData.getSchoolClass().getId());
            response.setClassName(savedData.getSchoolClass().getName());
        }
        return response;
    }


}
