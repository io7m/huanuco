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

import java.util.List;

/**
 * The response to the ListObjects command.
 */

@ImmutablesStyleType
@Value.Immutable
public interface HListObjectsResponseType
{
  /**
   * @return {@code true} if the list is truncated
   */

  @Value.Default
  default boolean isTruncated()
  {
    return false;
  }

  /**
   * @return The list of keys and contents
   */

  List<HObjectContents> contents();

  /**
   * @return The name
   */

  String name();

  /**
   * @return The prefix to which to limit keys
   */

  String prefix();

  /**
   * @return The delimiter for keys
   */

  @Value.Default
  default String delimiter()
  {
    return "/";
  }

  /**
   * @return The maximum number of keys
   */

  @Value.Default
  default int maximumKeys()
  {
    return 1000;
  }

  /**
   * @return The encoding
   */

  @Value.Default
  default String encoding()
  {
    return "UTF-8";
  }

  /**
   * @return The key count
   */

  @Value.Default
  default int keyCount()
  {
    return 0;
  }

  /**
   * @return The "start after" value
   */

  @Value.Default
  default String startAfter()
  {
    return "";
  }
}
