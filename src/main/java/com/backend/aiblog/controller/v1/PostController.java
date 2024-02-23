package com.backend.aiblog.controller.v1;

import com.backend.aiblog.dto.request.CommentRequest;
import com.backend.aiblog.dto.request.PostRequest;
import com.backend.aiblog.dto.request.UserRequest;
import com.backend.aiblog.dto.response.PaginationResponse;
import com.backend.aiblog.entity.Post;
import com.backend.aiblog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("")
    public ResponseEntity<?> createPost(@RequestBody PostRequest postRequest) {

        return postService.savePost(postRequest);
    }

    @GetMapping("")
    public ResponseEntity<?> getPosts(@RequestParam int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "") String username){

        try{
            PaginationResponse response = postService.getPosts(page, size, username);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>("GET POSTS UNSUCCESSFUL", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reply")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest){

        return postService.addComment(commentRequest);
    }

    @GetMapping("/id")
    public ResponseEntity<?> getPostById(@RequestParam String id){
        return postService.getPostById(id);
    }

    @PatchMapping("/like")
    public ResponseEntity<?> likePostById(@RequestParam String id, @RequestParam String username){
        try{
            Post response = postService.likePost(id, username);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>("LIKE POST UNSUCCESSFUL: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
