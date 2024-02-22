package com.backend.aiblog.controller.v1;

import com.backend.aiblog.dto.request.AuthenticationRequest;
import com.backend.aiblog.dto.request.UserRequest;
import com.backend.aiblog.dto.response.AuthenticationResponse;
import com.backend.aiblog.entity.User;
import com.backend.aiblog.service.AuthenticationService;
import com.backend.aiblog.service.JwtService;
import com.backend.aiblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

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

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody AuthenticationRequest request) throws IOException {

        final String userEmail = request.getUsername();
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

        final boolean isRefreshTokenValid = jwtService.isRefreshTokenValid(request.getRefreshToken(), userDetails);
        if(!isRefreshTokenValid){
            return new ResponseEntity<>("REFRESH TOKEN UNSUCCESSFUL" , HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(authenticationService.authenticateRefreshToken(request));
    }
}
