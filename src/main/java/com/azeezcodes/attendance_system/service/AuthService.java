package com.azeezcodes.attendance_system.service;

import com.azeezcodes.attendance_system.dto.AuthDto;
import com.azeezcodes.attendance_system.dto.UserDto;
import com.azeezcodes.attendance_system.model.UserModel;
import com.azeezcodes.attendance_system.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserDto.Response register(UserDto.Request request) {
        return userService.createUser(request);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserModel user = (UserModel) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return buildAuthResponse(user, token);
    }



    public AuthDto.ForgotPasswordResponse forgotPassword(AuthDto.ForgotPasswordRequest request) {
        String token = userService.generatePasswordResetToken(request.getEmail());

        AuthDto.ForgotPasswordResponse response = new AuthDto.ForgotPasswordResponse();
        response.setResetToken(token);
        return response;
    }

    public void resetPassword(AuthDto.ResetPasswordRequest request) {
        userService.resetPassword(request.getResetToken(), request.getNewPassword());
    }

    private AuthDto.AuthResponse buildAuthResponse(UserModel user, String token) {
        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName().name());
        return response;
    }
}
