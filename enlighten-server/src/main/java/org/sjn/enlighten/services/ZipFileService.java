package org.sjn.enlighten.services;

import lombok.NonNull;
import org.sjn.enlighten.repositories.ZipFileSourceRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.UUID;

@Service
public class ZipFileService {
    private final ZipFileSourceRepository zipFileSourceRepository;

    public ZipFileService(@NonNull ZipFileSourceRepository zipFileSourceRepository) {
        this.zipFileSourceRepository = zipFileSourceRepository;
    }

    public Path getPathForZipSource(UUID source) {
        return null;
    }
}
