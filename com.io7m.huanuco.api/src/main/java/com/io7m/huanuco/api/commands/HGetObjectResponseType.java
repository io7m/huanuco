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

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.io.InputStream;
import java.util.Optional;

/**
 * A response to GetObject.
 */

@ImmutablesStyleType
@Value.Immutable
public interface HGetObjectResponseType
{
  /**
   * @return The object size
   */

  long size();

  /**
   * @return The content type
   */

  String contentType();

  /**
   * @return The Base64 encoded hash
   */

  Optional<String> sha256();

  /**
   * @return The stream
   */

  InputStream data();
}
