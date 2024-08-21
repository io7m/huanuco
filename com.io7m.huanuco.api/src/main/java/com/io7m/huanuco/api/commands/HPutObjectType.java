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


package com.io7m.huanuco.api.commands;

import com.io7m.huanuco.api.HClientCommandType;
import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * A request to put an object.
 */

@ImmutablesStyleType
@Value.Immutable
public non-sealed interface HPutObjectType
  extends HClientCommandType<HPutObjectResponse>
{
  /**
   * @return The bucket name
   */

  String bucket();

  /**
   * @return The key
   */

  String key();

  /**
   * @return The time the object expires, for cache purposes
   */

  Optional<OffsetDateTime> expires();

  /**
   * @return The content type
   */

  @Value.Default
  default String contentType()
  {
    return "application/octet-stream";
  }

  /**
   * @return The object data
   */

  byte[] data();

  /**
   * @return The Base64 encoded hash of the data
   */

  @Value.Derived
  default String sha256()
  {
    try {
      final var digest =
        MessageDigest.getInstance("SHA256");

      digest.update(this.data());
      return Base64.getEncoder().encodeToString(digest.digest());
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}
