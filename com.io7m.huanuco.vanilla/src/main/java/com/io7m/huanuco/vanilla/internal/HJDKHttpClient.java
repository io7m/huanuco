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

import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.SdkHttpClient;

import java.net.http.HttpClient;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * An HTTP client based on {@code java.net.http}.
 *
 * @see SdkHttpClient
 */

public final class HJDKHttpClient implements SdkHttpClient
{
  private final HttpClient httpClient;

  /**
   * An HTTP client based on {@code java.net.http}.
   *
   * @param inHttpClientSupplier The client supplier
   */

  public HJDKHttpClient(
    final Supplier<HttpClient> inHttpClientSupplier)
  {
    this.httpClient =
      Objects.requireNonNull(inHttpClientSupplier, "httpClientSupplier")
        .get();
  }

  @Override
  public ExecutableHttpRequest prepareRequest(
    final HttpExecuteRequest request)
  {
    return HJDKExecutableHttpRequest.of(request, this.httpClient);
  }

  @Override
  public void close()
  {
    this.httpClient.close();
  }
}
