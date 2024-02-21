package com.backend.aiblog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Table(name = "FILE_DESCRIPTOR")
@Entity(name = "fileDescriptor")
@Data
public class FileDescriptor implements Serializable {
    @Id
    private UUID id;

    @Column(name = "FILE_NAME", length = 50)
    private String name;

    @Column(name = "FILE_EXT", length = 10)
    private String extension;

    @Column(name = "FILE_SIZE")
    private String size;

    @Column(name = "FILE_DESCRIPTION")
    private String description;

    @Column(name = "FOLDER_NAME")
    private String folderName;

    @Column(name = "CREATE_TS")
    private Date createTs;

    @Column(name = "CREATED_BY",length = 50)
    private String createdBy;

    @Column(name = "UPDATE_TS")
    private Date updateTs;

    @Column(name = "UPDATED_BY",length = 50)
    private String updatedBy;
}
