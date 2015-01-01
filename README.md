# userscript-maven-plugin
-------------------------
This plugin for maven allows you to "compile" a userscript. It generates the Userscript metadata block and also alows you to include other files to combine them into one.

## Usage

By default the userscript source files should be located in `src/main/userscript` and are outputed to `target`

Minimal pom.xml

```xml
<build>
	...
    <plugins>
    	...
        <plugin>
            <groupId>com.alta189</groupId>
            <artifactId>userscript-maven-plugin</artifactId>
            <version>1.0</version>
            <configuration>
                <userscripts>
                    <userscript>
                        <source>hello-world.user.js</source>
                        <metadata>
                            <name>${project.name}</name>
                            <version>${project.version}</version>
                        </metadata>
                    </userscript>
                </userscripts>
            </configuration>
            <executions>
                <execution>
                    <phase>compile</phase>
                    <goals>
                        <goal>userscript</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Full pom.xml

```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.alta189</groupId>
      <artifactId>userscript-maven-plugin</artifactId>
      <version>1.0</version>
      <configuration>
        <sourceDirectory>${basedir}/src/main/userscripts</sourceDirectory>
        <outputDirectory>${basedir}/target</outputDirectory>
        <userscripts>
          <userscript>
            <source>test-userscript.user.js</source>
            <output>test-userscript-compiled.user.js</output>
            <metadata>
              <name>${project.name}</name>
              <version>${project.version}</version>
              <description>${project.description}</description>
              <namespace>${project.groupId}.${project.artifactId}</namespace>
              <author>Stephen Williams</author>
              <icon>http://example.com/icon.png</icon>
              <includes>
                <include>http://example.com/example</include>
              </includes>
              <exludes>
                <exclude>http://example.com/qwerty</exclude>
              </exludes>
              <matches>
                <match>http://google.com/*</match>
              </matches>
              <grants>
                <grant>GM_log</grant>
                <grant>GM_info</grant>
              </grants>
              <requires>
                <require>http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js</require>
              </requires>
              <resources>
                <resources>http://example.com/something.css</resources>
              </resources>
              <noFrames>false</noFrames>
              <runAt>document-end</runAt>
              <updateURL>https://www.example.com/myscript.meta.js</updateURL>
              <downloadURL>https://www.example.com/myscript.user.js</downloadURL>
            </metadata>
          </userscript>
        </userscripts>
      </configuration>
      <executions>
        <execution>
          <phase>compile</phase>
          <goals>
            <goal>userscript</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request

### Guidelines

* Avoid mass whitespace changes
* Avoid code style changes
* Pull Request must be detailed in its description
* Test your changes!


## License

This project is licensed under the [New BSD License][license].

[license]: https://raw.githubusercontent.com/alta189/userscript-maven-plugin/master/LICENSE.txt