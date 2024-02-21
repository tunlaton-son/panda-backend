package com.backend.aiblog.service;

import com.backend.aiblog.dto.response.CommentResponse;
import com.backend.aiblog.dto.response.PaginationResponse;
import com.backend.aiblog.dto.response.PostResponse;
import com.backend.aiblog.entity.Comment;
import com.backend.aiblog.entity.Post;
import com.backend.aiblog.mapper.CommentResponseMapper;
import com.backend.aiblog.projection.CommentResult;
import com.backend.aiblog.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private static Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentResponseMapper commentResponseMapper;

    public ResponseEntity<?> getCommentsByPostId(String postId, int page, int size){
        try{

            Pageable paging = PageRequest.of(page, size);

            Page<Comment> commentList = commentRepository.getCommentsByPostIdAsc(UUID.fromString(postId) , paging);

            List<CommentResponse> commentResponses  = new ArrayList<>();
            for (Comment comment: commentList) {

                CommentResponse commentResponse = new CommentResponse();
                commentResponse.setId(comment.getId());
                commentResponse.setBody(comment.getBody());
                commentResponse.setName(comment.getUser().getName());
                commentResponse.setUsername(comment.getUser().getUsername());
                commentResponse.setProfileImage(comment.getUser().getProfileImage());

                commentResponses.add(commentResponse);
            }

            Integer previousCursor =  page;
            Integer nextCursor = page + 1;

            PaginationResponse response = new PaginationResponse();
            response.setData(commentResponses);
            response.setPreviousCursor(previousCursor);

            if(commentList.getContent().size() == 10){
                response.setNextCursor(nextCursor);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.error(ex.getMessage());
            return new ResponseEntity<>(" get comments unsuccessfully", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
