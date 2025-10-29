package com.furniture.controller;

import com.furniture.domain.USER_ROLE;
import com.furniture.modal.User;
import com.furniture.repository.UserRepository;
import com.furniture.response.AuthResponse;
import com.furniture.response.SignupRequest;
import com.furniture.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignupRequest req) {

//        User user = new User();
//        user.setEmail(req.getEmail());
//        user.setFullName(req.getFullName());
//
//        User savedUser = userRepository.save(user);

        String jwt = authService.createUser(req);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("register success");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);

        return ResponseEntity.ok(res);
    }


}
