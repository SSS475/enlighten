package org.sjn.enlighten.models;

import lombok.Data;

import java.nio.file.Path;

@Data
public class SourceEntry {
    String internalPath;

    public SourceEntry(Path path) {
        internalPath = path.toString();
    }
}
