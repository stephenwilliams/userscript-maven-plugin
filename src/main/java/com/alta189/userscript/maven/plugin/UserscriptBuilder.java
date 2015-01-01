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
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserscriptBuilder {
    private final Pattern infludePattern = Pattern.compile("/\\* include:(?<file>.+)\\*/", Pattern.CASE_INSENSITIVE);
    @Getter
    private final UserscriptMojo mojo;
    @Getter
    private final Userscript userscript;

    private void validate() throws UserscriptBuilderException {
        File sourceFile = new File(mojo.getSourceDirectory(), userscript.getSource());
        if (!sourceFile.exists()) {
            throw new UserscriptBuilderException("Source file does not exist: " + sourceFile.getPath());
        }

        if (!valid(userscript.getMetadata().getName())) {
            throw new UserscriptBuilderException("Userscript's name cannot be null or empty: " + sourceFile.getPath());
        }

        if (!valid(userscript.getMetadata().getVersion())) {
            throw new UserscriptBuilderException("Userscript's version cannot be null or empty: " + sourceFile.getPath());
        }
    }

    private String assemble() throws UserscriptBuilderException {
        StrBuilder builder = new StrBuilder();

        builder.appendln("// ==UserScript==");
        appendValid(builder, "// @name          ", userscript.getMetadata().getName());
        appendValid(builder, "// @namespace     ", userscript.getMetadata().getNamespace());
        appendValid(builder, "// @description   ", userscript.getMetadata().getDescription());
        appendValid(builder, "// @version       ", userscript.getMetadata().getVersion());
        appendValid(builder, "// @author        ", userscript.getMetadata().getAuthor());
        appendValid(builder, "// @include       ", userscript.getMetadata().getIncludes());
        appendValid(builder, "// @exclude       ", userscript.getMetadata().getExcludes());
        appendValid(builder, "// @match         ", userscript.getMetadata().getMatches());
        appendValid(builder, "// @require       ", userscript.getMetadata().getRequires());
        appendValid(builder, "// @resource      ", userscript.getMetadata().getResources());
        appendValid(builder, "// @grant         ", userscript.getMetadata().getGrants());
        appendValid(builder, "// @noframes      ", userscript.getMetadata().isNoFrames());
        appendValid(builder, "// @run-at        ", userscript.getMetadata().getRunAt());
        appendValid(builder, "// @icon          ", userscript.getMetadata().getIcon());
        appendValid(builder, "// @downloadURL   ", userscript.getMetadata().getDownloadURL());
        appendValid(builder, "// @updateURL     ", userscript.getMetadata().getUpdateURL());
        builder.appendln("// ==/UserScript==");
        builder.appendNewLine();

        File sourceFile = new File(mojo.getSourceDirectory(), userscript.getSource());
        String source;
        try {
            source = FileUtils.readFileToString(sourceFile);
        } catch (IOException e) {
            throw new UserscriptBuilderException("Could not read sourceFile: " + sourceFile.getPath(), e);
        }

        builder.append(filterSource(source));

        return builder.build();
    }

    public void build() throws UserscriptBuilderException {
        validate();
        String contents = assemble();

        if (!mojo.getOutputDirectory().exists()) {
            mojo.getOutputDirectory().mkdirs();
        }

        if (!mojo.getOutputDirectory().isDirectory()) {
            throw new UserscriptBuilderException("Output directory is not a directory");
        }

        File outputFile;
        if (valid(userscript.getOutput())) {
            outputFile = new File(mojo.getOutputDirectory(), userscript.getOutput());
        } else {
            outputFile = new File(mojo.getOutputDirectory(), userscript.getSource());
        }

        try {
            FileUtils.writeStringToFile(outputFile, contents);
        } catch (IOException e) {
            throw new UserscriptBuilderException("Error writing output to " + outputFile.getPath(), e);
        }
    }

    private boolean valid(String input) {
        return StringUtils.isNotEmpty(input) && StringUtils.isNotBlank(input);
    }

    private void appendValid(StrBuilder builder, String prefix, String input) {
        if (valid(input)) {
            builder.append(prefix).appendln(input);
        }
    }

    private void appendValid(StrBuilder builder, String prefix, List<String> input) {
        if (input == null || input.size() < 1) {
            return;
        }
        for (String in : input) {
            appendValid(builder, prefix, in);
        }
    }

    private void appendValid(StrBuilder builder, String input, boolean condition) {
        if (condition) {
            builder.appendln(input);
        }
    }

    private String filterSource(String source) throws UserscriptBuilderException {
        String result = source;
        Matcher matcher = infludePattern.matcher(source);

        while (matcher.find()) {
            String replace = matcher.group();
            String file = matcher.group("file");
            if (!valid(file)) {
                throw new UserscriptBuilderException("include tag is invalid");
            }

            File srcFile = new File(getMojo().getSourceDirectory(), file.trim());
            if (!srcFile.exists() || !srcFile.isFile()) {
                throw new UserscriptBuilderException("File does not exist or is not a file: " + srcFile.getPath());
            }

            String contents;
            try {
                contents = FileUtils.readFileToString(srcFile);
            } catch (IOException e) {
                throw new UserscriptBuilderException("Exception reading file for input tag: " + srcFile.getPath(), e);
            }

            result = result.replaceFirst(Pattern.quote(replace), contents);
        }

        return result;
    }
}
