/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.huanuco.demo;

import com.io7m.huanuco.api.HClientAccessKeys;
import com.io7m.huanuco.api.HClientBucketAccessStyle;
import com.io7m.huanuco.api.HClientConfiguration;
import com.io7m.huanuco.api.HException;
import com.io7m.huanuco.api.commands.HPutObject;
import com.io7m.huanuco.vanilla.HClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * S3 client (Demo).
 */

public final class DemoMain
{
  private static final Logger LOG =
    LoggerFactory.getLogger(DemoMain.class);

  private DemoMain()
  {

  }

  /**
   * S3 client (Demo).
   *
   * @param args The arguments
   *
   * @throws Exception On errors
   */

  public static void main(
    final String[] args)
    throws Exception
  {
    final var endpoint =
      System.getProperty("ophis.endpoint");
    final var access =
      System.getProperty("ophis.accessKey");
    final var secret =
      System.getProperty("ophis.secret");

    Objects.requireNonNull(endpoint, "endpoint");
    Objects.requireNonNull(access, "access");
    Objects.requireNonNull(secret, "secret");

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
  }
}
