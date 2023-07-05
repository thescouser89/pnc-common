# pnc-common

## Description

pnc-common contains common util code that can be shared between different
Project Newcastle services.

For example, the log package contains shared code for how MDC values are stored
within a service, and how to transmit those MDC values to other services via
HTTP headers.

## Building
```bash
$ mvn clean install -DskipTests=true
```

## Consuming pnc-common in your project
You can simply use the pnc-common library by specifying the dependency in your
Maven project's pom.xml:
```xml
<dependency>
  <groupId>org.jboss.pnc</groupId>
  <artifactId>pnc-common</artifactId>
  <version>LATEST_VERSION</version>
</dependency>
```

Using the Eclipse Transformer Maven plugin, we also produce a jar with Jakarta
annotations. You can consume it in your project as:
```
<dependency>
  <groupId>org.jboss.pnc</groupId>
  <artifactId>pnc-common</artifactId>
  <version>LATEST_VERSION</version>
  <classifier>jakarta</classifier>
</dependency>
```

Once we migrate all of our annotations / classes from `javax` to `jakarta`, the
classifier won't be needed anymore.

The latest version is specified [here](https://repo1.maven.org/maven2/org/jboss/pnc/pnc-common/maven-metadata.xml)
Snapshot versions are published [here](https://repository.jboss.org/org/jboss/pnc/pnc-common/)
