package com.furniture.service;

import com.furniture.request.LoginRequest;
import com.furniture.response.AuthResponse;
import com.furniture.response.SignupRequest;

public interface AuthService {

    void sentLoginOtp(String email) throws Exception;
    String createUser(SignupRequest req) throws Exception;

    AuthResponse signing(LoginRequest req);
}
