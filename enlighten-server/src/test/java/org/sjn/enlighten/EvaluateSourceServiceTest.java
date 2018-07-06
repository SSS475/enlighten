package org.sjn.enlighten;

import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.sjn.enlighten.models.SourceEntry;
import org.sjn.enlighten.services.EvaluateSourceService;
import org.sjn.enlighten.services.ZipFileService;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@Slf4j
public class EvaluateSourceServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private EvaluateSourceService evaluateSourceService;

    @Mock
    private ZipFileService zipFileService;

    @Test
    public void getListingTest() throws URISyntaxException {
        UUID source = UUID.randomUUID();

        final Path zipFilePath;
        try {
            zipFilePath =  Paths.get(ClassLoader.getSystemResource("temp.zip").toURI());
        } catch (URISyntaxException ex) {
            log.error("Unable to build path to zip file", ex);
            throw ex;
        }

        when(zipFileService.getPathForZipSource(source)).thenReturn(zipFilePath);

        Stream<SourceEntry> sources = evaluateSourceService.getListing(source, "/");

        assertTrue("No matching file", sources.anyMatch(sourceEntry -> sourceEntry.getInternalPath().contains("HelloWorld")));
    }

    @Test
    public void streamSourceLinesTest() {
        evaluateSourceService.streamSourceLines(null, null);
    }
}
