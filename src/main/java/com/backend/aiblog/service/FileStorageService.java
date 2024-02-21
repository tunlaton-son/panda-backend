package com.backend.aiblog.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.backend.aiblog.entity.FileDescriptor;
import com.backend.aiblog.repository.FileDescriptorRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${amazonS3.bucket}")
    private String bucketName;

    @Value("${amazonS3.accessKey}")
    private String accessKey;

    @Value("${amazonS3.secretAccessKey}")
    private String secretAccessKey;

    @Value("${amazonS3.region}")
    private String region;

    @Autowired
    FileDescriptorRepository fileDescriptorRepository;

    public ResponseEntity<?> uploadFileToS3(String pathName, List<MultipartFile> multipartFiles) {

        List<FileDescriptor> fileDescriptorList = new ArrayList<>();
        try{

            for (MultipartFile file:multipartFiles) {
                FileDescriptor fileDescriptor = buildFileDescriptor(pathName, file);
                pathName = pathName + (fileDescriptor.getId() + "." + fileDescriptor.getExtension()) ;

                File covertedFile = convertMultipartFileToFile(file, fileDescriptor.getId());
                PutObjectRequest putObjectRequest =
                        new PutObjectRequest(bucketName, pathName, covertedFile);

                amazonS3.putObject(putObjectRequest);
                fileDescriptorList.add(fileDescriptor);
                boolean delete = covertedFile.delete();
            }

            fileDescriptorRepository.saveAll(fileDescriptorList);
            return new ResponseEntity<>(fileDescriptorList, HttpStatus.OK);
        }catch (Exception e){
           log.error("Error upload File : ", e);
            return new ResponseEntity<>("UPLOAD FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<byte[]>> getFilesFromS3(String s3Path) throws IOException {
        ListObjectsV2Request listObjectsRequest =  new ListObjectsV2Request().withBucketName(bucketName).withPrefix(s3Path);
        var listObjectsResponse = amazonS3.listObjectsV2(listObjectsRequest);
        List<S3ObjectSummary> objectSummaries = listObjectsResponse.getObjectSummaries();

        List<byte[]> bytesList = new ArrayList<>();

        for (S3ObjectSummary objectSummary : objectSummaries) {
            String key = objectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucketName,  key);
            byte[] objectData = IOUtils.toByteArray(s3Object.getObjectContent());
            bytesList.add(objectData);

        }
        return new ResponseEntity<>(bytesList, HttpStatus.OK);
    }

    public ResponseEntity<byte[]> getSingleFileFromS3(String s3Path) throws IOException {
        ListObjectsV2Request listObjectsRequest =  new ListObjectsV2Request().withBucketName(bucketName).withPrefix(s3Path);
        var listObjectsResponse = amazonS3.listObjectsV2(listObjectsRequest);
        List<S3ObjectSummary> objectSummaries = listObjectsResponse.getObjectSummaries();

        byte[] bytes = null;

        for (S3ObjectSummary objectSummary : objectSummaries) {
            String key = objectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucketName,  key);
            byte[] objectData = IOUtils.toByteArray(s3Object.getObjectContent());
            bytes = objectData;
        }
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile, UUID id) {
        File file;

        file = new File(String.valueOf(id));
        try  {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            log.error("Error converting MultipartFile to File : ", e);
        }
        return file;
    }

    private FileDescriptor buildFileDescriptor(String pathName, MultipartFile multipartFile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        String originalFilename = multipartFile.getOriginalFilename();
        FileDescriptor fileDescriptor = new FileDescriptor();

        String extension = FilenameUtils.getExtension(originalFilename);

        fileDescriptor.setId(UUID.randomUUID());
        fileDescriptor.setName(originalFilename);
        fileDescriptor.setSize(String.valueOf(multipartFile.getSize()));
        fileDescriptor.setFolderName(pathName);
        fileDescriptor.setExtension(extension);
        fileDescriptor.setDescription("");
        fileDescriptor.setCreateTs(new Date());
        fileDescriptor.setCreatedBy(currentUserName);

        if (originalFilename != null) {

            int lastDotIndex = originalFilename.lastIndexOf(".");

            if (lastDotIndex > 0) {
                String fileExtension = originalFilename.substring(lastDotIndex + 1);
                fileDescriptor.setExtension(fileExtension);

            }
        }
        return fileDescriptor;
    }
}
