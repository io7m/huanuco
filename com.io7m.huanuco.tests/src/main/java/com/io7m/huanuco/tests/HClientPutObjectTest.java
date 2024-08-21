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
import com.io7m.huanuco.api.HException;
import com.io7m.huanuco.api.commands.HListObjects;
import com.io7m.huanuco.api.commands.HPutObject;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@Tag("client")
@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.huanuco", disabledIfUnsupported = true)
public final class HClientPutObjectTest
  extends HClientContract
{
  /**
   * Putting an object works.
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
            .setBucket("example-bucket-0")
            .setContentType("text/plain")
            .setData("Hello!".getBytes(StandardCharsets.UTF_8))
            .setKey("file.txt")
            .build()
          );

        final var r =
          client.execute(
            HListObjects.builder()
              .setBucket("example-bucket-0")
              .build()
          );

        assertTrue(
          r.contents()
            .stream()
            .anyMatch(o -> {
              return Objects.equals(o.key(), Optional.of("file.txt"));
            }),
          "List must contain a key 'file.txt'"
        );
    }
  }

  /**
   * Putting objects requires permissions.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNoPermissions()
    throws Exception
  {
    this.minio().detachUserPolicy(minioUser(), "readwrite");

    try (final var client = this.client()) {
      final var ex =
        assertThrows(HException.class, () -> {
          client.execute(
            HPutObject.builder()
              .setBucket("example-bucket-0")
              .setContentType("text/plain")
              .setData("Hello!".getBytes(StandardCharsets.UTF_8))
              .setKey("file.txt")
              .build()
          );
        });
      assertTrue(ex.message().contains("Access Denied."));
    }
  }
}
