XJC Plugin for Instantiation
============================

This plugin enables XJC to instantiate Fields in classes generated from XML-Schemas. It also provides the capability of generating collections as Set<?> instead of List<?>.

Usage from command line:

    xjc [ -Xinst-fields | -Xinst-lists | -Xinst-sets ] ...


Usage with maven:

First install with `mvn install`

Then add to your pom.xml file.

  <build>
    <plugins>
        <plugin>
            <groupId>org.jvnet.jaxb2.maven2</groupId>
            <artifactId>maven-jaxb2-plugin</artifactId>
            <version>0.8.1</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <schemaDirectory>${basedir}/src/main/resources/schema</schemaDirectory>
                <generatePackage>de.example.sample</generatePackage>
                <generateDirectory>${basedir}/target/generated-sources/xjc</generateDirectory>
                <extension>true</extension>
                <args>
                    <arg>-Xinst-fields</arg>
                </args>
                <plugins>
                    <plugin>
                        <groupId>de.jaxbnstuff</groupId>
                        <artifactId>xjc-plugin</artifactId>
                        <version>0.1.1-SNAPSHOT</version>
                    </plugin>
                </plugins>
            </configuration>
        </plugin>
    </plugins>
  </build>
