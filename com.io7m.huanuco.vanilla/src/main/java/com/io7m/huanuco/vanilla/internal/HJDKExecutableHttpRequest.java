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

import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * An executable HTTP request.
 *
 * @see ExecutableHttpRequest
 */

public final class HJDKExecutableHttpRequest
  implements ExecutableHttpRequest
{
  private final HttpRequest request;
  private final HttpClient httpClient;

  private HJDKExecutableHttpRequest(
    final HttpRequest inRequest,
    final HttpClient inHttpClient)
  {
    this.request =
      Objects.requireNonNull(inRequest, "request");
    this.httpClient =
      Objects.requireNonNull(inHttpClient, "httpClient");
  }

  /**
   * Create a request.
   *
   * @param sourceRequest The source request
   * @param httpClient    The HTTP client
   *
   * @return The request
   */

  public static HJDKExecutableHttpRequest of(
    final HttpExecuteRequest sourceRequest,
    final HttpClient httpClient)
  {
    final var baseRequest =
      sourceRequest.httpRequest();

    final var requestBuilder =
      HttpRequest.newBuilder(baseRequest.getUri());

    requestBuilder.version(HttpClient.Version.HTTP_1_1);

    for (final var entry : baseRequest.headers().entrySet()) {
      final var name = entry.getKey();
      final var values = entry.getValue();
      for (final var value : values) {
        try {
          requestBuilder.header(name, value);
        } catch (final IllegalArgumentException e) {
          /*
           * Some headers are restricted and will result in this exception
           * when trying to set them. Nothing we can do about it.
           */
        }
      }
    }

    switch (baseRequest.method()) {
      case GET -> {
        requestBuilder.GET();
      }
      case POST -> {
        requestBuilder.POST(BodyPublishers.ofByteArray(
          inputStreamData(sourceRequest.contentStreamProvider())
        ));
      }
      case PUT -> {
        requestBuilder.PUT(BodyPublishers.ofByteArray(
          inputStreamData(sourceRequest.contentStreamProvider())
        ));
      }
      case DELETE -> {
        requestBuilder.DELETE();
      }
      case HEAD -> {
        requestBuilder.HEAD();
      }
      case PATCH -> {
        throw new UnsupportedOperationException("PATCH");
      }
      case OPTIONS -> {
        throw new UnsupportedOperationException("OPTIONS");
      }
    }

    return new HJDKExecutableHttpRequest(
      requestBuilder.build(),
      httpClient
    );
  }

  private static byte[] inputStreamData(
    final Optional<ContentStreamProvider> contentStreamProvider)
  {
    try {
      if (contentStreamProvider.isPresent()) {
        final var provider =
          contentStreamProvider.get();
        try (var stream = provider.newStream()) {
          return stream.readAllBytes();
        }
      }
      return new byte[0];
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public HttpExecuteResponse call()
    throws IOException
  {
    try {
      final var sourceResponse =
        this.httpClient.send(
          this.request,
          HttpResponse.BodyHandlers.ofInputStream());

      final var responseBuilder = SdkHttpResponse.builder();
      responseBuilder.headers(sourceResponse.headers().map());
      responseBuilder.statusCode(sourceResponse.statusCode());

      return HttpExecuteResponse.builder()
        .response(responseBuilder.build())
        .responseBody(AbortableInputStream.create(sourceResponse.body()))
        .build();
    } catch (final InterruptedException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void abort()
  {

  }
}
