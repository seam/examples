package org.jboss.seam.security.examples.openid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves a maven artifact present on the test classpath.
 *
 * @author Stuart Douglas
 */
public class MavenArtifactResolver {

    public static File resolve(String groupId, String artifactId) {
        if (groupId == null) {
            throw new IllegalArgumentException("groupId cannot be null");
        }
        if (artifactId == null) {
            throw new IllegalArgumentException("artifactId cannot be null");
        }
        String path = new MavenArtifactResolver(groupId.trim(), artifactId.trim(), System.getProperty("java.class.path"), File.pathSeparatorChar, File.separatorChar).resolve();
        if (path == null) {
            throw new IllegalArgumentException("Cannot locate artifact for " + groupId + ":" + artifactId);
        }
        return new File(path);
    }

    public static File resolve(String qualifiedArtifactId) {
        String[] segments = qualifiedArtifactId.split(":");
        if (segments.length == 2) {
            return resolve(segments[0], segments[1]);
        } else {
            throw new IllegalArgumentException("Unable to parse " + qualifiedArtifactId + " as a groupId:artifactId");
        }
    }

    private final String classPathSeparatorRegex;
    private final char fileSeparator;
    private final String groupId;
    private final String artifactId;
    private final String classPath;

    MavenArtifactResolver(String groupId, String artifactId, String classPath, char pathSeparator, char fileSeparator) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.classPath = classPath;
        this.classPathSeparatorRegex = "[^" + pathSeparator + "]*";
        this.fileSeparator = fileSeparator;
    }

    String resolve() {
        Matcher matches = createFullyQualifiedMatcher();
        if (!matches.find()) {
            matches = createUnqualifiedMatcher();
            if (!matches.find()) {
                matches = createTargetClassesMatcher();
                if (!matches.find()) {
                    return null;
                } else {
                    String fileName = scanForArtifact(matches);
                    if (fileName == null) {
                        return null;
                    } else {
                        return fileName;
                    }
                }
            }
        }
        return matches.group(0);
    }

    private String scanForArtifact(Matcher targetClassesMatcher) {
        // Locate all target/classes in classpath and store the path to all files
        // target/
        List<String> paths = new ArrayList<String>();
        do {
            String path = targetClassesMatcher.group();
            File target = new File(path.substring(0, path.length() - 8));
            if (target.exists()) {
                if (!target.isDirectory()) {
                    throw new IllegalStateException("Found ${project.dir}/target/ but it is not a directory!");
                }
                for (File file : target.listFiles()) {
                    paths.add(file.getPath());
                }
            }
        }
        while (targetClassesMatcher.find());
        return scanForArtifact(paths);
    }

    String scanForArtifact(List<String> paths) {
        Pattern pattern = Pattern.compile(artifactId + "-[\\d+\\.]+(?:[\\-\\.]\\p{Alpha}*)?.jar$");
        for (String path : paths) {
            if (pattern.matcher(path).find()) {
                return path;
            }
        }
        return null;
    }

    /**
     * Creates a matcher that returns any fully qualified matches of the form
     * <code>com/acme/acme-core/1.0/acme-core-1.0.jar</code>. This will match
     * artifacts on the classpath from the Maven repo.
     */
    private Matcher createFullyQualifiedMatcher() {
        String pathString = groupId.replace('.', fileSeparator) + fileSeparator + artifactId + fileSeparator;
        Pattern p = Pattern.compile(classPathSeparatorRegex + Pattern.quote(pathString) + classPathSeparatorRegex, Pattern.CASE_INSENSITIVE);
        return p.matcher(classPath);
    }

    /**
     * Creates a matcher that returns any unqualified matches of the form
     * <code>target/acme-foo-1.0.jar</code>. This will match artifacts on the
     * classpath from the reactor.
     */
    private Matcher createUnqualifiedMatcher() {
        Pattern p = Pattern.compile(classPathSeparatorRegex + Pattern.quote("target" + fileSeparator + artifactId) + classPathSeparatorRegex, Pattern.CASE_INSENSITIVE);
        return p.matcher(classPath);
    }

    /**
     * Creates a matcher that returns any unqualified matches of the form
     * <code>target/acme-foo-1.0.jar</code>. This locates all
     */
    private Matcher createTargetClassesMatcher() {
        Pattern p = Pattern.compile(classPathSeparatorRegex + Pattern.quote("target" + fileSeparator + "classes") + classPathSeparatorRegex, Pattern.CASE_INSENSITIVE);
        return p.matcher(classPath);
    }
}
