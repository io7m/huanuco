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

import com.io7m.huanuco.api.HClientAccessKeys;
import com.io7m.huanuco.api.HClientCommandType;
import com.io7m.huanuco.api.HClientConfiguration;
import com.io7m.huanuco.api.HClientType;
import com.io7m.huanuco.api.HException;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.auth.aws.internal.scheme.DefaultAwsV4AuthScheme;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.util.Map;
import java.util.Objects;

/**
 * The default client.
 */

public final class HClient implements HClientType
{
  private final HClientConfiguration configuration;
  private final S3Client s3;
  private final S3Presigner s3Signer;
  private final HClientCommandCollection commands;
  private final CloseableCollectionType<HException> resources;

  /**
   * The default client.
   *
   * @param inConfiguration The configuration
   * @param inS3            The S3 client
   * @param inS3Signer      The S3 signer
   * @param inCommands      The command set
   */

  private HClient(
    final HClientConfiguration inConfiguration,
    final S3Client inS3,
    final S3Presigner inS3Signer,
    final HClientCommandCollection inCommands)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.s3 =
      Objects.requireNonNull(inS3, "s3");
    this.s3Signer =
      Objects.requireNonNull(inS3Signer, "s3Signer");
    this.commands =
      Objects.requireNonNull(inCommands, "commands");
    this.resources =
      CloseableCollection.create(() -> {
        return new HException(
          "One or more resources could not be closed.",
          "error-resource",
          Map.of()
        );
      });

    this.resources.add(this.s3);
    this.resources.add(this.s3Signer);
  }

  /**
   * Create a new client.
   *
   * @param configuration The configuration
   * @param commands      The command collection
   *
   * @return A new client
   */

  public static HClientType of(
    final HClientConfiguration configuration,
    final HClientCommandCollection commands)
  {
    final var clientBuilder =
      S3Client.builder();
    clientBuilder.region(
      Region.of(configuration.region()));
    clientBuilder.httpClient(
      new HJDKHttpClient(configuration.httpClientProvider()));
    clientBuilder.putAuthScheme(
      new DefaultAwsV4AuthScheme());
    clientBuilder.endpointOverride(
      configuration.endpoint());

    final var presignerBuilder =
      S3Presigner.builder();

    switch (configuration.bucketAccessStyle()) {
      case VIRTUALHOST_STYLE -> {
        clientBuilder.forcePathStyle(false);
      }
      case PATH_STYLE -> {
        clientBuilder.forcePathStyle(true);
      }
    }

    switch (configuration.credentials()) {
      case final HClientAccessKeys accessKeys -> {
        final var creds =
          StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
              accessKeys.accessKey(),
              accessKeys.secret()
            )
          );
        clientBuilder
          .credentialsProvider(creds);
        presignerBuilder
          .credentialsProvider(creds);
      }
    }

    final var s3Client =
      clientBuilder.build();

    presignerBuilder.s3Client(
      s3Client);
    presignerBuilder.region(
      Region.of(configuration.region()));
    presignerBuilder.endpointOverride(
      configuration.endpoint());

    final var s3Signer =
      presignerBuilder.build();

    return new HClient(configuration, s3Client, s3Signer, commands);
  }

  S3Client s3()
  {
    return this.s3;
  }

  S3Presigner s3Presigner()
  {
    return this.s3Signer;
  }

  @Override
  public HClientConfiguration configuration()
  {
    return this.configuration;
  }

  @Override
  public <R, C extends HClientCommandType<R>> R execute(
    final C command)
    throws HException
  {
    Objects.requireNonNull(command, "command");

    return this.commands.get(this, command).execute();
  }

  @Override
  public void close()
    throws HException
  {
    this.resources.close();
  }
}
