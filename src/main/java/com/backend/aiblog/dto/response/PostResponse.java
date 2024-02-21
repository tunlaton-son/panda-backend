package com.backend.aiblog.dto.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PostResponse {
    private UUID id;
    private String body;
    private Integer commentCount;
    private String name;
    private String username;
    private String profileImage;
    private boolean liked;
    private Integer likedCount;
}
