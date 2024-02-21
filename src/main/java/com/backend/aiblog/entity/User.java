package com.backend.aiblog.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "_USER")
@Entity(name = "User")
@Data
public class User implements UserDetails {

    @Id
    @Column(name = "ID", length = 50)
    private UUID id;

    @Column(name = "EMAIL", length = 250)
    private String email;

    @Column(name = "NAME", length = 250)
    private String name;

    @Column(name = "USERNAME", length = 100)
    private String username;

    @Column(name = "PROFILE_IMAGE")
    private String profileImage;

    @Column(name = "COVER_IMAGE")
    private String coverImage;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Post> postList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Comment> commentList;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> likedPostList;

    @Column(name = "PASSWORD", columnDefinition = "TEXT")
    private String password;

    @Column(name = "CREATED_BY",length = 50)
    private String createdBy;

    @Column(name = "CREATE_TS")
    private Date createTs = new Date();

    @Column(name = "UPDATED_BY",length = 50)
    private String updatedBy;

    @Column(name = "UPDATE_TS")
    private Date updateTs = new Date();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
