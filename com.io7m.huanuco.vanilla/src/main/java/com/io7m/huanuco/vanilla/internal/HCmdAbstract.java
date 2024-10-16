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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

abstract class HCmdAbstract<C, R>
  implements HCmdExecutableType<R>
{
  private final C command;
  private final HClient client;
  private final HashMap<String, String> attributes;

  HCmdAbstract(
    final HClient inClient,
    final C inCommand)
  {
    this.client =
      Objects.requireNonNull(inClient, "client");
    this.command =
      Objects.requireNonNull(inCommand, "parameters");
    this.attributes =
      new HashMap<>();
  }

  protected final HClient client()
  {
    return this.client;
  }

  protected final C command()
  {
    return this.command;
  }

  protected final void setAttribute(
    final String name,
    final String value)
  {
    this.attributes.put(
      Objects.requireNonNull(name, "name"),
      Objects.requireNonNull(value, "value")
    );
  }

  protected final Map<String, String> attributes()
  {
    return Map.copyOf(this.attributes);
  }
}
