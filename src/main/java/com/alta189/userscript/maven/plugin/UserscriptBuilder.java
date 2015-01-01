package com.alta189.userscript.maven.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class UserscriptBuilder {
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

        builder.append(source);

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
}
