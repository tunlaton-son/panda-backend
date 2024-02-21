package com.backend.aiblog.controller.v1;

import com.backend.aiblog.entity.FileDescriptor;
import com.backend.aiblog.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileStorageController {

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFileToS3(
            @RequestPart(name = "path") String s3Path,
            @RequestParam("files") List<MultipartFile> files) {

        return fileStorageService.uploadFileToS3(s3Path, files);
    }

    @GetMapping("/download")
    public ResponseEntity<List<byte[]>> uploadFileToS3(
            @RequestParam("path") String s3Path) throws IOException {
        return fileStorageService.getFilesFromS3(s3Path);
    }
}
