package com.alta189.userscript.maven.plugin;

import lombok.Getter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

@Mojo(name = "userscript", defaultPhase = LifecyclePhase.COMPILE)
public class UserscriptMojo extends AbstractMojo {
    @Getter
    @Parameter(required = true)
    private List<Userscript> userscripts;

    @Getter
    @Parameter(defaultValue = "${basedir}/src/main/userscripts")
    private File sourceDirectory;

    @Getter
    @Parameter(defaultValue = "${basedir}/target")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (userscripts == null || userscripts.size() == 0) {
            return;
        }

        if (!sourceDirectory.exists()) {
            throw new MojoFailureException("Userscripts source directory does not exist: " + sourceDirectory.getPath());
        }

        if (!sourceDirectory.isDirectory()) {
            throw new MojoFailureException("Userscripts source directory is not a directory: " + sourceDirectory.getPath());
        }

        for (Userscript userscript : userscripts) {
            getLog().info(userscript.getMetadata().toString());
            new UserscriptBuilder(this, userscript).build();
        }
    }
}
