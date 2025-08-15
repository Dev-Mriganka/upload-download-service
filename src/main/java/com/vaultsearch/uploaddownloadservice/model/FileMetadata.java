package com.vaultsearch.uploaddownloadservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;


import java.time.Instant;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File metadata information")
public class FileMetadata {

    @Schema(description = "Unique identifier for the file", example = "550e8400-e29b-41d4-a716-446655440000")
    private String contentId;

    @Schema(description = "S3 object key where the file is stored", example = "550e8400-e29b-41d4-a716-446655440000/example.pdf")
    private String s3Key;
    
    @Schema(description = "Original name of the uploaded file", example = "example.pdf")
    private String fileName;
    
    @Schema(description = "MIME type of the file", example = "application/pdf")
    private String contentType;

    @Schema(description = "Size of the file in bytes", example = "1024")
    private long size;
    
    @Schema(description = "Timestamp when the file was uploaded", example = "2025-08-08T12:00:00Z")
    private Instant uploadedAt;

    @DynamoDbPartitionKey
    public String getContentId() {
        return contentId;
    }

}
