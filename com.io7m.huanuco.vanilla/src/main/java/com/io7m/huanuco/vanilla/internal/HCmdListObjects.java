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
import com.io7m.huanuco.api.commands.HListObjects;
import com.io7m.huanuco.api.commands.HListObjectsResponse;
import com.io7m.huanuco.api.commands.HObjectContents;
import com.io7m.huanuco.api.commands.HOwner;
import com.io7m.huanuco.api.commands.HRestoreStatus;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.services.s3.model.RestoreStatus;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

/**
 * ListObjects.
 */

public final class HCmdListObjects
  extends HCmdAbstract<HListObjects, HListObjectsResponse>
{
  HCmdListObjects(
    final HClient client,
    final HListObjects parameters)
  {
    super(client, parameters);
  }

  @Override
  public HListObjectsResponse execute()
    throws HException
  {
    this.setAttribute("Command", "ListObjects");

    try {
      final var parameters =
        this.command();

      final var response =
        this.client()
          .s3()
          .listObjectsV2(
            ListObjectsV2Request.builder()
              .bucket(parameters.bucket())
              .delimiter(parameters.delimiter())
              .maxKeys(parameters.maximumKeys())
              .prefix(parameters.prefix())
              .startAfter(parameters.startAfter())
              .build()
          );

      return HListObjectsResponse.builder()
        .setContents(contentsOf(response.contents()))
        .setDelimiter(response.delimiter())
        .setEncoding(Optional.ofNullable(response.encodingTypeAsString()).orElse("UTF-8"))
        .setMaximumKeys(response.maxKeys())
        .setName(response.name())
        .setPrefix(response.prefix())
        .build();
    } catch (final Throwable e) {
      throw HExceptions.ofException(this.attributes(), e);
    }
  }

  private static Iterable<HObjectContents> contentsOf(
    final List<S3Object> contents)
  {
    return contents.stream()
      .map(HCmdListObjects::contentOf)
      .toList();
  }

  private static HObjectContents contentOf(
    final S3Object o)
  {
    return HObjectContents.builder()
      .setETag(o.eTag())
      .setKey(o.key())
      .setLastModified(OffsetDateTime.ofInstant(o.lastModified(), UTC))
      .setOwner(ownerOf(o.owner()))
      .setRestoreStatus(restoreStatusOf(o.restoreStatus()))
      .setStorageClass(o.storageClassAsString())
      .build();
  }

  private static HRestoreStatus restoreStatusOf(
    final RestoreStatus status)
  {
    if (status == null) {
      return new HRestoreStatus(
        false,
        Optional.empty()
      );
    }

    return new HRestoreStatus(
      status.isRestoreInProgress(),
      Optional.of(OffsetDateTime.ofInstant(status.restoreExpiryDate(), UTC))
    );
  }

  private static Optional<HOwner> ownerOf(
    final Owner owner)
  {
    return Optional.ofNullable(owner)
      .map(o -> {
        return new HOwner(
          o.displayName(),
          o.id()
        );
      });
  }
}
