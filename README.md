huanuco
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.huanuco/com.io7m.huanuco.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.huanuco%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.huanuco/com.io7m.huanuco?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/huanuco/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/huanuco.svg?style=flat-square)](https://codecov.io/gh/io7m-com/huanuco)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=e6c35c)

![com.io7m.huanuco](./src/site/resources/huanuco.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/huanuco/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/huanuco/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/huanuco/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/huanuco/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/huanuco/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/huanuco/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/huanuco/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/huanuco/actions?query=workflow%3Amain.windows.temurin.lts)|

## huanuco

An opinionated, shaded, modularized distribution of the S3 client from
the [AWS SDK](https://github.com/aws/aws-sdk-java-v2).

If you don't know why you need this specifically, you probably don't. Use
the AWS SDK directly.

## Features

* An opinionated, shaded, modularized distribution of the S3 client from the [AWS SDK](https://github.com/aws/aws-sdk-java-v2).
* [OSGi-ready](https://www.osgi.org/).
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System).
* ISC license.

## Usage

```
final var clients =
  new HClients();

final var configuration =
  HClientConfiguration.builder()
    .setEndpoint(URI.create(endpoint))
    .setCredentials(new HClientAccessKeys(access, secret))
    .setBucketAccessStyle(HClientBucketAccessStyle.PATH_STYLE)
    .build();

final var tempFile =
  Paths.get("/tmp/file.txt");

try (var client = clients.createClient(configuration)) {
  Files.writeString(tempFile, "Hello!\n");

  {
    final var r =
      client.execute(
        HPutObject.builder()
          .setBucket("general")
          .setContentType("text/plain")
          .setKey("example.txt")
          .setData(Files.readAllBytes(tempFile))
          .build()
      );
  }

} catch (final HException e) {
  LOG.error("{}: {}", e.errorCode(), e.getMessage());
  for (final var entry : e.attributes().entrySet()) {
    LOG.error("  {}: {}", entry.getKey(), entry.getValue());
  }
  LOG.error("Stacktrace: ", e);
}
```

## Demo

A [demo application](com.io7m.huanuco.demo) is included.

