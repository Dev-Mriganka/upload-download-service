package com.vaultsearch.uploaddownloadservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
    private String region;
    private S3 s3;
    private DynamoDb dynamodb;
    private Sqs sqs;

    // Nested classes to match the YAML structure
    @Getter
    @Setter
    public static class S3 {
        private String bucketName;
    }

    @Getter
    @Setter
    public static class DynamoDb {
        private String tableName;
    }

    @Getter
    @Setter
    public static class Sqs {
        private String queueName;
        private String queueUrl;
    }
}
