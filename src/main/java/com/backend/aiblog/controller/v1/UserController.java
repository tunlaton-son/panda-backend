package com.backend.aiblog.controller.v1;

import com.backend.aiblog.dto.request.UserRequest;
import com.backend.aiblog.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username){
        return userService.getFirstUserByUsername(username);
    }

    @PostMapping("")
    public ResponseEntity<?> postUser(@RequestBody UserRequest userRequest) {

        return userService.saveUser(userRequest);
    }

    @PatchMapping("")
    public ResponseEntity<?> patchUser(
                                       @RequestPart(name = "data") String data,
                                       @RequestPart(name = "profilePath") String profilePath,
                                       @RequestParam(name="profileImage", required = false) MultipartFile profileImage,
                                       @RequestPart(name = "coverPath") String coverPath,
                                       @RequestParam(name = "coverImage", required = false) MultipartFile coverImage
    ) throws JsonProcessingException {


        return userService.patchUser(data, profilePath, profileImage, coverPath, coverImage);
    }
}
