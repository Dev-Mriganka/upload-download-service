package com.vaultsearch.uploaddownloadservice.repository;

import com.vaultsearch.uploaddownloadservice.config.AwsProperties;
import com.vaultsearch.uploaddownloadservice.model.FileMetadata;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Repository
public class FileMetadataRepository {

    private final DynamoDbTable<FileMetadata> fileMetadataTable;

    public FileMetadataRepository(DynamoDbEnhancedClient enhancedClient, AwsProperties awsProperties) {
        this.fileMetadataTable = enhancedClient.table(
                awsProperties.getDynamodb().getTableName(), // Use table name from properties
                TableSchema.fromBean(FileMetadata.class)
        );
    }

    public void save(FileMetadata metadata) {
        fileMetadataTable.putItem(metadata);
    }

    public Optional<FileMetadata> findById(String contentId) {
        Key key = Key.builder().partitionValue(contentId).build();
        FileMetadata item = fileMetadataTable.getItem(r -> r.key(key));
        return Optional.ofNullable(item);
    }
}
