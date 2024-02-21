package com.backend.aiblog.service;

import com.backend.aiblog.dto.request.CommentRequest;
import com.backend.aiblog.dto.request.PostRequest;
import com.backend.aiblog.dto.request.UserRequest;
import com.backend.aiblog.dto.response.CommentResponse;
import com.backend.aiblog.dto.response.PaginationResponse;
import com.backend.aiblog.dto.response.PostResponse;
import com.backend.aiblog.entity.Comment;
import com.backend.aiblog.entity.Post;
import com.backend.aiblog.entity.User;
import com.backend.aiblog.mapper.CommentResponseMapper;
import com.backend.aiblog.mapper.PostResponseMapper;
import com.backend.aiblog.projection.CommentResult;
import com.backend.aiblog.projection.PostResult;
import com.backend.aiblog.repository.CommentRepository;
import com.backend.aiblog.repository.PostRepository;
import com.backend.aiblog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    private static Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostResponseMapper postResponseMapper;

    @Autowired
    CommentResponseMapper commentResponseMapper;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> savePost(PostRequest postRequest){

        try{

            Post post = new Post();
            post.setId(UUID.randomUUID());
            post.setBody(postRequest.getBody());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            User user = userRepository.findFirstByUsername(currentUserName).orElse(null);
            if(user == null){
                return new ResponseEntity<>("USER NOT FOUND", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            post.setUser(user);
            post.setCreatedBy(currentUserName);
            post.setUpdatedBy(currentUserName);

            postRepository.save(post);
            return new ResponseEntity<>(" saved successfully", HttpStatus.OK);
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(" saved unsuccessfully", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getPosts(int page, int size, String username){
        try{

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            Pageable paging = PageRequest.of(page, size);
            Page<Post> postList = null;

            if(!username.isBlank()){
                postList = postRepository.findPostByCreatedByOrderByCreateTsDesc(username, paging);
            }else{
                postList = postRepository.findAllByOrderByCreateTsDesc(paging);
            }

            List<PostResponse> postResponses = new ArrayList<>();
            for (Post post: postList) {
                PostResponse postResponse = new PostResponse();

                postResponse.setId(post.getId());
                postResponse.setBody(post.getBody());
                postResponse.setCommentCount(post.getComments().size());
                postResponse.setName(post.getUser().getName());
                postResponse.setUsername(post.getUser().getUsername());
                postResponse.setProfileImage(post.getUser().getProfileImage() != null ? post.getUser().getProfileImage() : null);

                boolean liked = post.getLikedUserList().stream().anyMatch(user -> user.getUsername().equals(currentUserName));
                postResponse.setLiked(liked);

                Integer likedCount = post.getLikedUserList().size();
                postResponse.setLikedCount(likedCount);

                postResponses.add(postResponse);
            }

            Integer previousCursor =  page;
            Integer nextCursor = page + 1;

            PaginationResponse response = new PaginationResponse();
            response.setData(postResponses);
            response.setPreviousCursor(previousCursor);

            if(postList.getContent().size() == 10){
                response.setNextCursor(nextCursor);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(" get posts unsuccessfully", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getPostById(String postId){
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            Post post = postRepository.findPostById(UUID.fromString(postId)).orElse(null);

            if(post == null){
                return new ResponseEntity<>("POST NOT FOUND", HttpStatus.NO_CONTENT);
            }

            PostResponse postResponse = new PostResponse();

            postResponse.setId(post.getId());
            postResponse.setBody(post.getBody());
            postResponse.setCommentCount(post.getComments().size());
            postResponse.setName(post.getUser().getName());
            postResponse.setUsername(post.getUser().getUsername());
            postResponse.setProfileImage(post.getUser().getProfileImage() != null ? post.getUser().getProfileImage() : null);

            boolean liked = post.getLikedUserList().stream().anyMatch(user -> user.getUsername().equals(currentUserName));
            postResponse.setLiked(liked);

            Integer likedCount = post.getLikedUserList().size();
            postResponse.setLikedCount(likedCount);

            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(" get post unsuccessfully", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> addComment(CommentRequest commentRequest){
        try{

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            Post post = postRepository.findPostById(UUID.fromString(commentRequest.getPostId())).orElse(null);

            User user = userRepository.findFirstByUsername(currentUserName).orElse(null);
            if(user == null){
                return new ResponseEntity<>(" USER NOT FOUND", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if(post != null){
                Comment comment = new Comment();
                comment.setId(UUID.randomUUID());
                comment.setBody(commentRequest.getBody());
                comment.setPost(post);
                comment.setUser(user);
                comment.setCreatedBy(currentUserName);
                comment.setUpdatedBy(currentUserName);

                commentRepository.save(comment);

                CommentResponse commentResponse = new CommentResponse();
                commentResponse.setId(comment.getId());
                commentResponse.setBody(comment.getBody());

                return new ResponseEntity<>(commentResponse, HttpStatus.OK);
            }

            return new ResponseEntity<>("POST NOT FOUND", HttpStatus.NO_CONTENT);
        }catch (Exception ex){
            logger.error(ex.getMessage());
            return new ResponseEntity<>(" save comment unsuccessfully", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> likePost(String postId, String username){
        try{

            User user = userRepository.findFirstByUsername(username).orElse(null);
            if(user == null){
                return new ResponseEntity<>("USER NOT FOUND", HttpStatus.NO_CONTENT);
            }

            Post post = postRepository.findById(UUID.fromString(postId)).orElse(null);
            if(post == null){
                return new ResponseEntity<>("POST NOT FOUND", HttpStatus.NO_CONTENT);
            }

            boolean liked = post.getLikedUserList().stream().anyMatch(m->m.getUsername().equals(user.getUsername()));
            if(liked) {
                post.getLikedUserList().remove(user);
            }else{
                post.getLikedUserList().add(user);
            }

            postRepository.save(post);

            return new ResponseEntity<>(post, HttpStatus.OK);
        }catch (Exception ex){
            logger.error(ex.getMessage());
            return new ResponseEntity<>(" Like post unsuccessfully", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
