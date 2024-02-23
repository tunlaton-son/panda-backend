package com.backend.aiblog.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private UUID id;
    private String email;
    private String name;
    private String username;
    private String profileImage;
    private String coverImage;
    private boolean following = false;
    private Integer followings;
    private Integer followers;
}
