package com.furniture.service;

import com.furniture.response.SignupRequest;

public interface AuthService {

    String createUser(SignupRequest req);
}
