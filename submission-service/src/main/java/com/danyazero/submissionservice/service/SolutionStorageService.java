package com.danyazero.submissionservice.service;

import com.danyazero.submissionservice.exception.RequestException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionStorageService {
    private final MinioClient minioClient;

    @Value("minio.bucket")
    private String bucketName;

    @PostConstruct
    void initializeBucket() throws Exception {
        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!found) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
            );
        }
    }

    public void uploadSolution(String objectName, InputStream inputStream) {
        try {
            var response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType("text/plain")
                            .build()
            );
            log.info("Uploaded solution to bucket: {}, with response: {}", bucketName, response.toString());
        } catch (Exception e) {
            throw new RequestException("An error occurred while saving file");
        }
    }
}
