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

import com.io7m.huanuco.api.HClientCommandType;

import java.util.HashMap;
import java.util.Objects;

/**
 * A command factory collection.
 */

public final class HClientCommandCollection
{
  private final HashMap<Class<?>, HClientCommandFactoryType<?, ?>> commands;

  /**
   * Create an empty collection.
   */

  public HClientCommandCollection()
  {
    this.commands = new HashMap<>();
  }

  /**
   * Register a command factory.
   *
   * @param factory The command factory
   */

  public void register(
    final HClientCommandFactoryType<?, ?> factory)
  {
    Objects.requireNonNull(factory, "factory");
    final Class<?> clazz = factory.commandClass();
    if (this.commands.containsKey(clazz)) {
      throw new IllegalStateException(
        "A command factory already exists for %s".formatted(clazz)
      );
    }
    this.commands.put(clazz, factory);
  }

  /**
   * Create a new collection.
   *
   * @return The collection
   */

  public static HClientCommandCollection create()
  {
    final var collection = new HClientCommandCollection();
    collection.register(new HCmdCreatePresignedGetF());
    collection.register(new HCmdCreatePresignedPutF());
    collection.register(new HCmdGetObjectF());
    collection.register(new HCmdListBucketsF());
    collection.register(new HCmdListObjectsF());
    collection.register(new HCmdPutObjectF());
    return collection;
  }

  /**
   * Get a command.
   *
   * @param client  The client
   * @param <R>     The type of results
   * @param <C>     The command type
   * @param command The command
   *
   * @return The executable command
   */

  @SuppressWarnings("unchecked")
  public <R, C extends HClientCommandType<R>> HCmdExecutableType<R> get(
    final HClient client,
    final C command)
  {
    Objects.requireNonNull(client, "client");
    Objects.requireNonNull(command, "command");

    final var factory =
      this.commands.get(command.getClass());

    if (factory == null) {
      throw new IllegalStateException(
        "No command implementation available for %s"
          .formatted(command.getClass())
      );
    }

    return (HCmdExecutableType<R>) factory.createCommand(client, cast(command));
  }

  @SuppressWarnings("unchecked")
  private static <A, B> B cast(
    final A x)
  {
    return (B) (Object) x;
  }
}
