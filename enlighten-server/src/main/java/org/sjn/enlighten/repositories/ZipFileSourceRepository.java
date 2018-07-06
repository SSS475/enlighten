package org.sjn.enlighten.repositories;

import org.sjn.enlighten.models.ZipFileSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ZipFileSourceRepository extends JpaRepository<ZipFileSource, UUID> {
}
