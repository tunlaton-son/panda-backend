package com.backend.aiblog.projection;

import com.backend.aiblog.dto.response.CommentResponse;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;

public interface PostResult {

    @Value("#{target.id}")
    UUID getId();

    @Value("#{target.body}")
    String getBody();

    @Value("#{target.name}")
    String getName();

    @Value("#{target.username}")
    String getUsername();

    @Value("#{target.profile_image}")
    String getProfileImage();

    @Value("#{target.commentCount}")
    Integer getCommentCount();

    @Value("#{target.liked}")
    boolean getLiked();

    @Value("#{target.likedCount}")
    Integer getLikedCount();
}
