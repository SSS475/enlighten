package org.sjn.enlightenmavenplugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.map.SingletonMap;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Mojo(name = "scan")
public class ScannerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException {
        final Log log = getLog();

        log.info("Enlighten Scanner Running");

        project.getCompileSourceRoots().stream()
                .map(this::bundleSource)
                .forEach(this::uploadBundle);
    }

    private void uploadBundle(Path bundle) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("http://localhost:8080/api/analysis/" + UUID.randomUUID() + "/bundles");

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", bundle.toFile(), ContentType.APPLICATION_OCTET_STREAM, bundle.getFileName().toString())
                    .build();

            httpPost.setEntity(entity);
            CloseableHttpResponse response = client.execute(httpPost);
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.toString());
            response.getEntity().writeTo(System.out);
        } catch (IOException e) {
            throw new BundleException(e);
        }
    }

    private Path bundleSource (String sourceRoot) {
        Path bundle = newTempZipFile();
        try (FileSystem zipfs = newTempZipFileSystem(bundle)) {
            Path basePath = Paths.get(sourceRoot);

            SourceFileBuilder builder = new SourceFileBuilder(basePath);
            FileCopier fileCopier = new FileCopier(zipfs);

            Files.walk(basePath)
                    .filter(Files::isRegularFile)
                    .map(builder::build)
                    .forEach(fileCopier::copyFile);
        } catch (IOException e) {
            throw new BundleException(e);
        }
        return bundle;
    }

    private FileSystem newTempZipFileSystem(Path zipFile) {
        try {
            URI baseUri = zipFile.toUri();
            URI zipUri = new URI("jar:" + baseUri.getScheme(), baseUri.getPath(), null);
            System.out.println(zipUri);
            return FileSystems.newFileSystem(zipUri, new SingletonMap<>("create", "true"));
        } catch (IOException | URISyntaxException e) {
            throw new BundleException(e);
        }
    }

    private Path newTempZipFile() {
        return Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
    }

    private class BundleException extends RuntimeException {
        public BundleException(Throwable t) {
            super(t);
        }
    }

    @Data
    @AllArgsConstructor
    private class SourceFileHolder {
        private Path source;
        private Path target;
    }

    @Data
    @AllArgsConstructor
    private class SourceFileBuilder {
        private Path base;

        private SourceFileHolder build(Path file) {
            return new SourceFileHolder(file, base.relativize(file));
        }
    }

    @Data
    @AllArgsConstructor
    private class FileCopier {
        private FileSystem targetFileSystem;

        private void copyFile(SourceFileHolder file) {
            try {
                Files.createDirectories(targetFileSystem.getPath(file.getTarget().getParent().toString()));
                Files.copy(file.getSource(), targetFileSystem.getPath(file.getTarget().toString()));
            } catch (IOException e) {
                throw new BundleException(e);
            }
        }
    }
}
