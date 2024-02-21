package com.backend.aiblog.repository;

import com.backend.aiblog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>  {

    List<User> findByEmail(String email);

    List<User> findByUsername(String username);

    Optional<User> findFirstByUsername(String username);
}
