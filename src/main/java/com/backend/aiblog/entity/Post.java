package com.backend.aiblog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "POST")
@Entity(name = "Post")
@Data
public class Post {

    @Id
    @Column(name = "ID", length = 50)
    private UUID id;

    @Column(name = "BODY", columnDefinition = "TEXT")
    private String body;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<User> favouritedBy;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "post")
    private List<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> likedUserList;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "CREATED_BY",length = 50)
    private String createdBy;

    @Column(name = "CREATE_TS")
    private Date createTs = new Date();

    @Column(name = "UPDATED_BY",length = 50)
    private String updatedBy;

    @Column(name = "UPDATE_TS")
    private Date updateTs = new Date();

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", user=" + user.toString() +
                '}';
    }
}
