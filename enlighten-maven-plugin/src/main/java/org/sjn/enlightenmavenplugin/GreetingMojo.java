package org.sjn.enlightenmavenplugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

@Mojo(name = "sayhi")
public class GreetingMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    private String buildDir;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession session;

    @Component
    private BuildPluginManager buildPluginManager;

    public void execute() throws MojoExecutionException {
        getLog().info("Hello");
        getLog().info(project.getName());
        getLog().info(project.getCompileSourceRoots().toString());

        getLog().info(buildDir);
        try {
            getLog().info(GitUtils.getCurrentBranchName(project.getBasedir().getAbsolutePath()));
        } catch (Exception e) {
            getLog().error("Unable to process branch name", e);
        }
        getLog().info("Done");

        try {
            URL url = new URL("http://repo.maven.apache.org/maven2/com/github/spotbugs/spotbugs/3.1.1/spotbugs-3.1.1.tgz");
            Path spotBugsJar = downloadJarFile(url);
            getLog().info("SpotBugs Jar File: " + spotBugsJar.toString());
        } catch (Exception e) {
            getLog().error("What", e);
        }

    }

    public Path downloadJarFile(URL url) throws IOException {
        Path returnValue = Files.createTempFile("~", ".jar");

        try (ReadableByteChannel input = Channels.newChannel(url.openStream());
             FileChannel output = FileChannel.open(returnValue)) {
            output.transferFrom(input, 0, Long.MAX_VALUE);
        }

        return returnValue;
    }
}
