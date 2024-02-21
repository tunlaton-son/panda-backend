package com.backend.aiblog.controller.v1;

import com.backend.aiblog.dto.request.UserRequest;
import com.backend.aiblog.repository.UserRepository;
import com.backend.aiblog.service.FollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/following")
public class FollowingController {

    @Autowired
    private FollowingService followingService;

    @PostMapping("")
    public ResponseEntity<?> addFollowing(@RequestBody UserRequest userRequest) {

        return followingService.addFollowing(userRequest);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllFollowings(){
        return followingService.getAllFollowings();
    }
}
