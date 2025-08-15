package com.vaultsearch.uploaddownloadservice.controller;

import com.vaultsearch.uploaddownloadservice.model.FileMetadata;
import com.vaultsearch.uploaddownloadservice.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@Tag(name = "File Upload", description = "APIs for uploading files to the system")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "Upload a file",
            description = "Uploads a file to the server and returns its metadata.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = FileMetadata.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file provided"),
            @ApiResponse(responseCode = "413", description = "File size exceeds the limit"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileMetadata> uploadFile(
            @RequestPart("file")
            @Schema(description = "The file to upload",
                    format = "binary",
                    type = "string")
            MultipartFile file) throws IOException {

        log.info("Received file upload request: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            log.warn("Empty file provided");
            return ResponseEntity.badRequest().build();
        }

        FileMetadata savedMetadata = fileStorageService.uploadFile(file);
        log.info("File upload completed successfully: {}", savedMetadata.getContentId());

        return ResponseEntity.ok(savedMetadata);
    }
}