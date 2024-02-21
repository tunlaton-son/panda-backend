package com.backend.aiblog.controller.v1;

import com.backend.aiblog.dto.request.AuthenticationRequest;
import com.backend.aiblog.dto.request.UserRequest;
import com.backend.aiblog.dto.response.AuthenticationResponse;
import com.backend.aiblog.entity.User;
import com.backend.aiblog.service.AuthenticationService;
import com.backend.aiblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        return userService.saveUser(userRequest);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<String> getUserByEmail(@PathVariable String email){
        List<User> users;
        users = userService.getUserByEmail(email);
        if(users.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok("User founded");
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<String> getUserByUsername(@PathVariable String username){
        List<User> users;
        users = userService.getUserByUsername(username);
        if(users.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok("User founded");
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws IOException {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}