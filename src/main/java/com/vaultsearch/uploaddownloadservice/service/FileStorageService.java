package com.vaultsearch.uploaddownloadservice.service;

import com.vaultsearch.uploaddownloadservice.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {

    public FileMetadata uploadFile(MultipartFile file) throws IOException;
}

