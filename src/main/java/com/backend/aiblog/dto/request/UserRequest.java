package com.backend.aiblog.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRequest implements Serializable {

    private String email;
    private String name;
    private String username;
    private String password;
}
