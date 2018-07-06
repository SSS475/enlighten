package org.sjn.enlighten.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisEventController {
    private static final String UPLOAD_DIR = ".";

    @PostMapping
    public void createAnalysisEvent() {

    }

    @PutMapping
    public void updateAnalysisEvent(@RequestParam("ready") boolean ready) {

    }

    @PostMapping("/{analysisId}/bundles")
    public void acceptBundle (@PathVariable UUID analysisId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return;
        }

        Path destination = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
        try {
            Files.write(destination, file.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("What the file!");
        }
    }
}
