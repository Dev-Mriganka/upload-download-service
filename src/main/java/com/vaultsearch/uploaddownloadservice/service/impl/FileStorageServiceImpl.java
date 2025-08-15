package com.vaultsearch.uploaddownloadservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultsearch.uploaddownloadservice.config.AwsProperties;
import com.vaultsearch.uploaddownloadservice.model.FileMetadata;
import com.vaultsearch.uploaddownloadservice.repository.FileMetadataRepository;
import com.vaultsearch.uploaddownloadservice.service.FileStorageService;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileMetadataRepository metadataRepository;
    private final S3Client s3Client;
    private final AwsProperties awsProperties;
    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public FileMetadata uploadFile(MultipartFile file) throws IOException {
        String contentId = UUID.randomUUID().toString();
        String s3Key = contentId + "-" + file.getOriginalFilename();

        // Upload to S3 (tracing handled by Aspect and AWS SDK auto-instrumentation)
        log.info("Uploading file to S3: {}", s3Key);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucketName())
                .key(s3Key)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // Save metadata (tracing handled by Aspect and DynamoDB auto-instrumentation if enabled)
        log.info("Saving metadata to DynamoDB: {}", contentId);
        FileMetadata metadata = new FileMetadata(
                contentId,
                s3Key,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                Instant.now()
        );
        metadataRepository.save(metadata);

        // Send message to SQS (tracing handled by Aspect and AWS SDK auto-instrumentation)
        log.info("Sending message to SQS queue: {}", awsProperties.getSqs().getQueueName());
        FileMetadata savedMetadata = metadataRepository.findById(contentId).orElseThrow();
        String jsonMetadata = objectMapper.writeValueAsString(savedMetadata);
        sqsTemplate.send(to -> to
                .queue(awsProperties.getSqs().getQueueName())
                .payload(jsonMetadata)
        );

        return metadataRepository.findById(contentId).orElseThrow();
    }
}
