package com.azeezcodes.attendance_system.service;

import com.azeezcodes.attendance_system.dto.UserDto;
import com.azeezcodes.attendance_system.exception.BadRequestException;
import com.azeezcodes.attendance_system.exception.ConflictException;
import com.azeezcodes.attendance_system.exception.ResourceNotFoundException;
import com.azeezcodes.attendance_system.model.RoleModel;
import com.azeezcodes.attendance_system.model.TeacherModel;
import com.azeezcodes.attendance_system.model.UserModel;
import com.azeezcodes.attendance_system.repository.RoleRepository;
import com.azeezcodes.attendance_system.repository.TeacherRepository;
import com.azeezcodes.attendance_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto.Response createUser(UserDto.Request request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered: " + request.getEmail());
        }

        RoleModel role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRole()));

        UserModel user = UserModel.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        return toResponse(userRepository.save(user));
    }

    // GET /api/users
    public List<UserDto.Response> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    // GET /api/users/{id}
    public UserDto.Response getUserById(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toResponse(user);
    }

    // PUT /api/users/{id}/deactivate
    @Transactional
    public UserDto.Response deactivateUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(false);
        return toResponse(userRepository.save(user));
    }

    // PUT /api/users/{id}/activate
    @Transactional
    public UserDto.Response activateUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(true);
        return toResponse(userRepository.save(user));
    }

    // DELETE /api/users/{id}
    @Transactional
    public void deleteUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    // PUT /api/users/{id}/link-teacher/{teacherId}
    @Transactional
    public UserDto.Response linkTeacher(Long userId, Long teacherId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        TeacherModel teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        user.setTeacher(teacher);
        return toResponse(userRepository.save(user));
    }

    // PUT /api/users/me/change-password
    @Transactional
    public void changePassword(Long userId, UserDto.ChangePasswordRequest request) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // POST /api/auth/forgot-password
    @Transactional
    public String generatePasswordResetToken(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with email: " + email));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        return token;
    }

    // POST /api/auth/reset-password
    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        UserModel user = userRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid or expired reset token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    private UserDto.Response toResponse(UserModel user) {
        UserDto.Response response = new UserDto.Response();
        response.setId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName().name());
        response.setEnabled(user.isEnabled());
        response.setTeacherId(user.getTeacher() != null ? user.getTeacher().getId() : null);
        return response;
    }
}
