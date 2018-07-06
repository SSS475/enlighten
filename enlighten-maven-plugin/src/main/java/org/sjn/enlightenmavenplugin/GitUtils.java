package org.sjn.enlightenmavenplugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GitUtils {
    private static final Pattern BRANCH_PATTERN = Pattern.compile(" *(?:ref:)? */?(?:refs/heads/|refs/remotes/)?(\\S+)");

    private GitUtils() {
    }

    public static String getCurrentBranchName(String root) throws IOException {
        Path rootFile = Paths.get(root);

        Path gitFile = rootFile.resolve(".git");

        Path headFile = gitFile.resolve("HEAD");

        String headContents = new String(Files.readAllBytes(headFile), "UTF-8");

        Matcher matcher = BRANCH_PATTERN.matcher(headContents.trim());

        if (!matcher.matches()) {
            return "x" + headContents;
        }

        headContents = matcher.group(1);

        return headContents;
    }
}
