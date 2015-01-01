package com.alta189.userscript.maven.plugin;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
public class UserscriptMetadata {
    @Getter
    private String description;
    @Getter
    private String downloadURL;
    @Getter
    private List<String> excludes;
    @Getter
    private List<String> grants;
    @Getter
    private String icon;
    @Getter
    private List<String> includes;
    @Getter
    private List<String> matches;
    @Getter
    private String name;
    @Getter
    private String namespace;
    @Getter
    private boolean noFrames = false;
    @Getter
    private List<String> requires;
    @Getter
    private List<String> resources;
    @Getter
    private String runAt;
    @Getter
    private String updateURL;
    @Getter
    private String version;
    @Getter
    private String author;
}
