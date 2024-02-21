package com.backend.aiblog.dto.request;

import lombok.Data;

@Data
public class CommentRequest {

    private String postId;
    private String body;
}
