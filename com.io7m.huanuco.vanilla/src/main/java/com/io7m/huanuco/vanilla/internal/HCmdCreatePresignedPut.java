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
import com.io7m.huanuco.api.commands.HCreatePresignedPut;
import com.io7m.huanuco.api.commands.HCreatePresignedResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * CreatePresignedPut.
 */

public final class HCmdCreatePresignedPut
  extends HCmdAbstract<HCreatePresignedPut, HCreatePresignedResponse>
{
  HCmdCreatePresignedPut(
    final HClient client,
    final HCreatePresignedPut parameters)
  {
    super(client, parameters);
  }

  @Override
  public HCreatePresignedResponse execute()
    throws HException
  {
    this.setAttribute("Command", "CreatePresignedPut");

    try {
      final var parameters =
        this.command();

      final var response =
        this.client()
          .s3Presigner()
          .presignPutObject(
            PutObjectPresignRequest.builder()
              .putObjectRequest(
                PutObjectRequest.builder()
                  .key(parameters.key())
                  .bucket(parameters.bucket())
                  .build())
              .signatureDuration(parameters.validityDuration())
              .build()
          );

      return HCreatePresignedResponse.builder()
        .setUri(response.url().toURI())
        .build();
    } catch (final Throwable e) {
      throw HExceptions.ofException(this.attributes(), e);
    }
  }
}
