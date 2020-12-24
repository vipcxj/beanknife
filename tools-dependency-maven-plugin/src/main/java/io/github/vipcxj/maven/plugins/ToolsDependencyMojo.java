package io.github.vipcxj.maven.plugins;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.apache.maven.toolchain.java.DefaultJavaToolChain;

import java.io.File;
import java.io.IOException;
import java.util.Set;

@Mojo(name = "resolve", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class ToolsDependencyMojo extends AbstractMojo {

    @Component
    private ToolchainManager toolchainManager;

    @Component
    private ArtifactFactory artifactFactory;

    @Parameter(defaultValue = "${session}", readonly = false, required = true)
    private MavenSession session;

    @Parameter(defaultValue = "${project}", readonly = false, required = true)
    private MavenProject project;

    private DefaultJavaToolChain getToolchain() {
        DefaultJavaToolChain javaToolchain = null;
        if (toolchainManager != null) {
            final Toolchain tc = toolchainManager.getToolchainFromBuildContext("jdk", session);
            if (tc.getClass().equals(DefaultJavaToolChain.class)) {
                // can this ever NOT happen?
                javaToolchain = (DefaultJavaToolChain) tc;
            }
        }
        return javaToolchain;
    }

    public static String normalize(String path) {
        String normalized = path;

        while(true) {
            int index = normalized.indexOf("//");
            if (index < 0) {
                while(true) {
                    index = normalized.indexOf("/./");
                    if (index < 0) {
                        while(true) {
                            index = normalized.indexOf("/../");
                            if (index < 0) {
                                return normalized;
                            }

                            if (index == 0) {
                                return null;
                            }

                            int index2 = normalized.lastIndexOf(47, index - 1);
                            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
                        }
                    }

                    normalized = normalized.substring(0, index) + normalized.substring(index + 2);
                }
            }

            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
    }

    private File findTools(String javaHome) {
        String normalize = normalize(javaHome);
        if (normalize == null) {
            return null;
        }
        File installFolder = new File(normalize);
        if (!installFolder.exists()) {
            return null;
        }
        File toolsFile = new File(installFolder, "lib/tools.jar");
        if (!toolsFile.exists()) {
            toolsFile = new File(installFolder, "Classes/classes.jar");
            if (!toolsFile.exists()) {
                return null;
            }
        }
        return toolsFile;
    }

    public void execute() {
        String javaHome;
        DefaultJavaToolChain tc = getToolchain();
        if (tc != null) {
            getLog().info("Toolchain in javahome-resolver-maven-plugin: " + tc);
            // we are interested in JAVA_HOME for given jdk
            javaHome = tc.getJavaHome();
        } else {
            javaHome = System.getenv("JAVA_HOME");

            if (javaHome == null) {
                getLog().error("No toolchain configured. No JAVA_HOME configured");
                return;
            }
            getLog().error("No toolchain in javahome-resolver-maven-plugin. Using default JDK[" + javaHome + "]");
        }
        File toolsFile = findTools(javaHome);
        if (toolsFile != null) {
            addOptionJarDependency("jdk.tools", "jdk.tools", "1.0.0", toolsFile);
            getLog().info("Success add tools dependency. The tools.jar is at \"" + toolsFile.getAbsolutePath() + "\"");
        }
    }

    private void addOptionJarDependency(String groupId, String artifactId, String version, File file) {
        //noinspection unchecked
        Set<Artifact> artifacts = project.getDependencyArtifacts();
        for (Artifact artifact : artifacts) {
            if (artifact.getGroupId().equals(groupId) && artifact.getArtifactId().equals(artifactId)) {
                return;
            }
        }
        Artifact artifact = artifactFactory.createArtifact(groupId, artifactId, version, "system", "jar");
        artifact.setFile(file);
        artifact.setOptional(true);
        artifacts.add(artifact);
    }
}
