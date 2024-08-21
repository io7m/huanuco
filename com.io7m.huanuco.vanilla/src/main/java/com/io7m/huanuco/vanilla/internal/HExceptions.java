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


package com.io7m.huanuco.vanilla.internal;

import com.io7m.huanuco.api.HException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.HashMap;
import java.util.Map;

final class HExceptions
{
  private HExceptions()
  {

  }

  public static HException ofException(
    final Map<String, String> sourceAttributes,
    final Throwable e)
  {
    final var attributes = new HashMap<>(sourceAttributes);

    return switch (e) {
      case final S3Exception ee -> {
        final var details = ee.awsErrorDetails();
        attributes.put("Service", details.serviceName());
        yield new HException(
          e.getMessage(),
          e,
          details.errorCode(),
          Map.copyOf(attributes)
        );
      }
      default -> {
        yield new HException(
          e.getMessage(),
          e,
          "error-exception",
          Map.copyOf(attributes)
        );
      }
    };
  }
}
