package com.backend.aiblog.service;

import com.backend.aiblog.dto.request.UserRequest;
import com.backend.aiblog.dto.response.FollowingResponse;
import com.backend.aiblog.entity.User;
import com.backend.aiblog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FollowingService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> addFollowing(UserRequest userRequest){
        try{

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            if(userRequest.getUsername() == null || userRequest.getUsername().isBlank()){
                return  new ResponseEntity<>("USERNAME IS MISSING" , HttpStatus.INTERNAL_SERVER_ERROR);
            }

            User follwingUser  = userRepository.findFirstByUsername(userRequest.getUsername()).orElse(null);
            if(follwingUser == null) {
                return  new ResponseEntity<>("FOLLOWING USER NOT FOUND" , HttpStatus.NO_CONTENT);
            }

            User currentUser = userRepository.findFirstByUsername(currentUserName).orElse(null);
            if(currentUser == null) {
                return  new ResponseEntity<>("CURRENT USER NOT FOUND" , HttpStatus.NO_CONTENT);
            }

            boolean isFollowing = currentUser.getFollowing().parallelStream().anyMatch(e->e.getUsername().equals(follwingUser.getUsername()));
            if(isFollowing){
                currentUser.getFollowing().remove(follwingUser);
            }else{
                currentUser.getFollowing().add(follwingUser);
            }

            userRepository.save(currentUser);

            return  new ResponseEntity<>("ADD FOLLOWING SUCCESSFUL" , HttpStatus.OK);
        }catch (Exception ex){
            log.error(ex.getMessage());
            return  new ResponseEntity<>("ADD FOLLOWING UNSUCCESSFUL" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> getAllFollowings(){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            User currentUser = userRepository.findFirstByUsername(currentUserName).orElse(null);
            if(currentUser == null) {
                return  new ResponseEntity<>("CURRENT USER NOT FOUND" , HttpStatus.NO_CONTENT);
            }

            List<FollowingResponse> followingResponseList = new ArrayList<>();
            for (User user: currentUser.getFollowing()) {
                FollowingResponse followingResponse = new FollowingResponse();
                followingResponse.setUsername(user.getUsername());
                followingResponse.setProfileImage(user.getProfileImage());
                followingResponseList.add(followingResponse);
            }

            return  new ResponseEntity<>(followingResponseList , HttpStatus.OK);
        }catch (Exception ex){
            log.error(ex.getMessage());
            return  new ResponseEntity<>("GET FOLLOWING UNSUCCESSFUL" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
