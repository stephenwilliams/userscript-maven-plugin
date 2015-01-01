package com.alta189.userscript.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;

public class UserscriptBuilderException extends MojoExecutionException {
    public UserscriptBuilderException(Object source, String shortMessage, String longMessage) {
        super(source, shortMessage, longMessage);
    }

    public UserscriptBuilderException(String message, Exception cause) {
        super(message, cause);
    }

    public UserscriptBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserscriptBuilderException(String message) {
        super(message);
    }
}
