package com.backend.aiblog.service;

import com.backend.aiblog.dto.request.UserRequest;
import com.backend.aiblog.dto.response.AuthenticationResponse;
import com.backend.aiblog.dto.response.UserResponse;
import com.backend.aiblog.entity.FileDescriptor;
import com.backend.aiblog.entity.User;
import com.backend.aiblog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private JwtService jwtService;

    @Value("${amazonS3.url}")
    private String s3Url;

    public ResponseEntity<?> saveUser(UserRequest userRequest){
        try{

            User user = new User();
            user.setId(UUID.randomUUID());
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setUsername(userRequest.getUsername());
            user.setCreatedBy("admin");
            user.setUpdatedBy("admin");

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
            String encodePassword = encoder.encode(userRequest.getPassword());
            user.setPassword(encodePassword);

            userRepository.save(user);

            return new ResponseEntity<>(user.getId() + " saved successfully", HttpStatus.OK);
        }catch (Exception ex){
            logger.error(ex.toString());
            return new ResponseEntity<>(" saved unsuccessfully", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?>  patchUser(String data, String profilePath, MultipartFile profileImage, String coverPath, MultipartFile coverImage){
        try{

            ObjectMapper objectMapper = new ObjectMapper();
            UserRequest userRequest = objectMapper.readValue(data, UserRequest.class);

            User user = userRepository.findFirstByUsername(userRequest.getUsername()).orElse(null);
            if(user == null){
                return new ResponseEntity<>("USER NOT FOUND", HttpStatus.NO_CONTENT);
            }

            List<FileDescriptor> profileImages = new ArrayList<>();
            if(profileImage != null){
                List<MultipartFile> profileImageList = new ArrayList<>();
                profileImageList.add(profileImage);
                ResponseEntity<?> response = fileStorageService.uploadFileToS3(profilePath, profileImageList);
                profileImages = (List<FileDescriptor>) response.getBody();
            }

            List<FileDescriptor> coverImages = new ArrayList<>();
            if(coverImage != null){
                List<MultipartFile> coverImageList = new ArrayList<>();
                coverImageList.add(coverImage);
                ResponseEntity<?> response = fileStorageService.uploadFileToS3(coverPath, coverImageList);
                coverImages = (List<FileDescriptor>) response.getBody();
            }

            user.setName(userRequest.getName());

            assert profileImages != null;
            if(!profileImages.isEmpty()){
                String profileImageUrl = s3Url + "/profile/" + profileImages.get(0).getId().toString() + "." + profileImages.get(0).getExtension();
                user.setProfileImage(profileImageUrl);
            }

            assert coverImages != null;
            if(!coverImages.isEmpty()){
                String coverImageUrl = s3Url + "/cover/" + coverImages.get(0).getId().toString() + "." + coverImages.get(0).getExtension();
                user.setCoverImage(coverImageUrl);
            }
            userRepository.save(user);

            var jwtToken = jwtService.generateToken(user);
            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(jwtToken);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.error(ex.toString());
            return new ResponseEntity<>(" update unsuccessfully", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public List<User> getUserByEmail(String email){

        return  userRepository.findByEmail(email);
    }

    public List<User> getUserByUsername(String username){

        return  userRepository.findByUsername(username);
    }

    public ResponseEntity<?> getFirstUserByUsername(String username){
        try{

            User user = userRepository.findFirstByUsername(username)
                        .orElse(null);

            if(user == null){
                return new ResponseEntity<>(" USER NOT FOUND", HttpStatus.NO_CONTENT);
            }

            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setUsername(user.getUsername());
            userResponse.setName(user.getName());
            userResponse.setProfileImage(user.getProfileImage() != null ?  user.getProfileImage():null);
            userResponse.setCoverImage(user.getCoverImage() != null ? user.getCoverImage():null);

            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        }catch (Exception ex){
            logger.error(ex.toString());
            return new ResponseEntity<>(" get User unsuccessfully", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
