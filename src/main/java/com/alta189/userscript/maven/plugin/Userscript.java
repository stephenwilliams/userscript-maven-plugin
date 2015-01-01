package com.alta189.userscript.maven.plugin;

import lombok.Getter;

import java.io.File;

public class Userscript {
    @Getter
    private UserscriptMetadata metadata;
    @Getter
    private String source;
    @Getter
    private String output;
}
