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


package com.io7m.huanuco.tests;

import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.huanuco.api.commands.HCreatePresignedGet;
import com.io7m.huanuco.api.commands.HCreatePresignedPut;
import com.io7m.huanuco.api.commands.HPutObject;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@Tag("client")
@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.huanuco", disabledIfUnsupported = true)
public final class HClientCreatePresignedGetTest
  extends HClientContract
{
  /**
   * Creating a presigned GET URL works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testExecute()
    throws Exception
  {
    try (final var client = this.client()) {
      client.execute(
        HPutObject.builder()
          .setKey("file.txt")
          .setBucket("example-bucket-0")
          .setData("Hello!".getBytes(StandardCharsets.UTF_8))
          .build()
      );

      final var r =
        client.execute(
          HCreatePresignedGet.builder()
            .setKey("file.txt")
            .setBucket("example-bucket-0")
            .setValidityDuration(Duration.ofDays(1L))
            .build()
        );

      {
        try (var c = HttpClient.newHttpClient()) {
          final var q =
            HttpRequest.newBuilder(r.uri())
              .GET()
              .build();

          final var rs =
            c.send(q, HttpResponse.BodyHandlers.ofString());

          assertTrue(
            rs.statusCode() < 300,
            "Status code %s must be < 300".formatted(rs.statusCode())
          );
          assertEquals("Hello!", rs.body());
        }
      }
    }
  }
}
