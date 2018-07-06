package org.sjn.enlighten.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
public class ZipFileSource {
    @Id
    private UUID id;
}
