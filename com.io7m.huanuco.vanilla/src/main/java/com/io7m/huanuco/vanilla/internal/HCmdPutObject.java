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
import com.io7m.huanuco.api.commands.HPutObject;
import com.io7m.huanuco.api.commands.HPutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * PutObject.
 */

public final class HCmdPutObject
  extends HCmdAbstract<HPutObject, HPutObjectResponse>
{
  HCmdPutObject(
    final HClient client,
    final HPutObject parameters)
  {
    super(client, parameters);
  }

  @Override
  public HPutObjectResponse execute()
    throws HException
  {
    this.setAttribute("Command", "PutObject");

    try {
      final var parameters =
        this.command();
      final var data =
        parameters.data();

      final var response =
        this.client()
          .s3()
          .putObject(
            PutObjectRequest.builder()
              .contentType(parameters.contentType())
              .checksumSHA256(parameters.sha256())
              .bucket(parameters.bucket())
              .key(parameters.key())
              .build(),
            RequestBody.fromBytes(data)
          );

      return HPutObjectResponse.builder()
        .setETag(response.eTag())
        .build();
    } catch (final Throwable e) {
      throw HExceptions.ofException(this.attributes(), e);
    }
  }
}
