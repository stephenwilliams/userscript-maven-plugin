/*
 * This file is part of userscript-maven-plugin, licensed under the New BSD License.
 *
 * Copyright (c) 2014, Stephen Williams (alta189) <https://github.com/alta189/userscript-maven-plugin/>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of userscript-maven-plugin nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
