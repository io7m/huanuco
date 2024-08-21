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
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@Tag("client")
@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.huanuco", disabledIfUnsupported = true)
public final class HClientListObjectsTest
  extends HClientContract
{
  /**
   * The list of objects is returned.
   *
   * @throws Exception On errors
   */

  @Test
  public void testExecute()
    throws Exception
  {
    try (final var client = this.client()) {
      final var result =
        client.execute(
          HListObjects.builder()
            .setBucket("example-bucket-0")
            .build()
          );
    }
  }

  /**
   * Listing objects requires permissions.
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
            HListObjects.builder()
              .setBucket("example-bucket-0")
              .build()
          );
        });
      assertTrue(ex.message().contains("Access Denied."));
    }
  }
}
