package com.backend.aiblog.repository;

import com.backend.aiblog.entity.Comment;
import com.backend.aiblog.entity.Post;
import com.backend.aiblog.projection.CommentResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query(value = "select e.id, e.body from Comment e where e.post.id =: postId")
    List<CommentResult> findCommentByPostId(UUID postId);

    @Query(value = "select e from Comment e where e.post.id =:postId order by e.createTs asc ")
    Page<Comment> getCommentsByPostIdAsc(UUID postId, Pageable pageable);

}
