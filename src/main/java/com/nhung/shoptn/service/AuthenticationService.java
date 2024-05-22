package com.nhung.shoptn.service;

import com.nhung.shoptn.dto.JwtAuthenticationResponse;
import com.nhung.shoptn.dto.RefreshTokenRequest;
import com.nhung.shoptn.dto.SignUpRequest;
import com.nhung.shoptn.dto.SigninRequest;
import com.nhung.shoptn.model.User;

public interface AuthenticationService {
    User signup(SignUpRequest signUpRequest);
    JwtAuthenticationResponse signIn(SigninRequest signinRequest);
    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
