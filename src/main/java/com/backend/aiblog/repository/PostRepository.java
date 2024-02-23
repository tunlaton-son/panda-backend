package com.backend.aiblog.repository;

import com.backend.aiblog.entity.Post;
import com.backend.aiblog.projection.PostResult;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@EnableRedisRepositories
@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Page<Post> findAllByOrderByCreateTsDesc(Pageable pageable);

    Page<Post> findPostByCreatedByOrderByCreateTsDesc(String createdBy, Pageable pageable);

    @Query(value = " select p.id, p.body, count(c.id) as commentCount, u.profile_image, u.name, u.username, count(ul.id) as likedCount from post p " +
            " left join comment c ON c.post_id = p.id " +
            " left join _user u ON u.id = p.user_id " +
            " left join _user_liked_post_list ul ON ul.liked_post_list_id = p.id"+
            " where p.id =:id group by p.id, p.body, u.profile_image, u.name, u.username", nativeQuery = true)
    Optional<PostResult> getPostById(UUID id);

    Optional<Post> findPostById(UUID id);
}
