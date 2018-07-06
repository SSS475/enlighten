package org.sjn.enlighten.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.sjn.enlighten.models.SourceEntry;
import org.sjn.enlighten.models.SourceLine;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
public class EvaluateSourceService {

    private final ZipFileService zipFileService;

    public EvaluateSourceService(@NonNull ZipFileService zipFileService) {
        this.zipFileService = zipFileService;
    }

    // TODO: ZipFileSystem Caching

    public Stream<SourceEntry> getListing(UUID source, String internalPath) {
        Path zipFilePath = zipFileService.getPathForZipSource(source);

        if (zipFilePath == null) {
            return Stream.empty();
        }

        try (FileSystem zipFs = FileSystems.newFileSystem(zipFilePath, ClassLoader.getSystemClassLoader())) {
            Path zipDir = zipFs.getPath(internalPath);
            if (Files.exists(zipDir) && Files.isDirectory(zipDir)) {
                return Files.list(zipDir)
                        .map(SourceEntry::new);
            } else {
                log.warn("File doesn't exist or isn't a directory: {}", zipDir);
            }
        } catch (IOException ex) {
            log.error("Unable to read directory listing", ex);
        }

        return Stream.empty();
    }

    public Stream<SourceLine> streamSourceLines(UUID source, String internalPath) {
        return null;
    }
}
