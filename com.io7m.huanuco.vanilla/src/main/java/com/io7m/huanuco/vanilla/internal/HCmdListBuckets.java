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
import com.io7m.huanuco.api.commands.HBucketDescription;
import com.io7m.huanuco.api.commands.HListBucketsResponse;
import com.io7m.huanuco.api.commands.HListBucketsType;
import com.io7m.huanuco.api.commands.HOwner;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.time.OffsetDateTime;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

/**
 * ListBuckets.
 */

public final class HCmdListBuckets
  extends HCmdAbstract<HListBucketsType, HListBucketsResponse>
{
  HCmdListBuckets(
    final HClient client,
    final HListBucketsType parameters)
  {
    super(client, parameters);
  }

  @Override
  public HListBucketsResponse execute()
    throws HException
  {
    this.setAttribute("Command", "ListBuckets");

    try {
      final var response =
        this.client()
          .s3()
          .listBuckets();

      final var owner =
        response.owner();

      return new HListBucketsResponse(
        response.buckets()
          .stream()
          .map(this::bucket)
          .toList(),
        new HOwner(
          owner.displayName(),
          owner.id()
        ),
        Optional.ofNullable(response.continuationToken())
      );
    } catch (final Throwable e) {
      throw HExceptions.ofException(this.attributes(), e);
    }
  }

  private HBucketDescription bucket(
    final Bucket x)
  {
    return new HBucketDescription(
      x.name(),
      OffsetDateTime.ofInstant(x.creationDate(), UTC)
    );
  }
}
