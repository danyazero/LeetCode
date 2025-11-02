package com.danyazero.submissionservice.service;

import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

    public void uploadSolution(String objectName, InputStream inputStream) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        var response = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType("text/plain")
                        .build()
        );
        log.info("Uploaded solution to bucket: {}, with response: {}", bucketName, response.checksumSHA256());
    }

    public GetObjectResponse getSolution(String objectName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        var request = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();

        var response = minioClient.getObject(request);
        log.info("Downloaded solution from bucket: {}, with name: {}", bucketName, objectName);

        return response;
    }
}
