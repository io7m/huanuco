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
import com.io7m.huanuco.api.commands.HHeadObject;
import com.io7m.huanuco.api.commands.HHeadObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

import java.util.Optional;

/**
 * HeadObject.
 */

public final class HCmdHeadObject
  extends HCmdAbstract<HHeadObject, HHeadObjectResponse>
{
  HCmdHeadObject(
    final HClient client,
    final HHeadObject parameters)
  {
    super(client, parameters);
  }

  @Override
  public HHeadObjectResponse execute()
    throws HException
  {
    this.setAttribute("Command", "HeadObject");

    try {
      final var parameters =
        this.command();

      final var response =
        this.client()
          .s3()
          .headObject(
            HeadObjectRequest.builder()
              .bucket(parameters.bucket())
              .key(parameters.key())
              .build()
          );

      return HHeadObjectResponse.builder()
        .setSha256(Optional.ofNullable(response.checksumSHA256()))
        .setSize(response.contentLength())
        .setContentType(response.contentType())
        .setMetadata(response.metadata())
        .build();
    } catch (final Throwable e) {
      throw HExceptions.ofException(this.attributes(), e);
    }
  }
}
