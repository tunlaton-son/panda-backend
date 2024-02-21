package com.backend.aiblog.controller.v1;

import com.backend.aiblog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentsController {

    @Autowired
    CommentService commentService;

    @GetMapping("")
    public ResponseEntity<?> getCommentsByPostId(@RequestParam int page, @RequestParam(defaultValue = "10") int size, @RequestParam String postId){

        return commentService.getCommentsByPostId(postId, page, size);
    }
}
