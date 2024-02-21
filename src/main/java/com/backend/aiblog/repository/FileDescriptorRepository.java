package com.backend.aiblog.repository;

import com.backend.aiblog.entity.FileDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileDescriptorRepository extends JpaRepository<FileDescriptor, UUID>  {
}
