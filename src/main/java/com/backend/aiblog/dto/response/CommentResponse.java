package com.backend.aiblog.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class CommentResponse implements Serializable {

    private UUID id;
    private String body;
    private String name;
    private String username;
    private String profileImage;
}
