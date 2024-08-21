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


package com.io7m.huanuco.tests;

import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterSuite;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.huanuco.api.HClientAccessKeys;
import com.io7m.huanuco.api.HClientConfiguration;
import com.io7m.huanuco.api.HClientCredentialsType;
import com.io7m.huanuco.api.HClientType;
import com.io7m.huanuco.api.HException;
import com.io7m.huanuco.vanilla.HClients;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;

@Tag("integration")
@Tag("client")
@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.huanuco", disabledIfUnsupported = true)
public abstract class HClientContract
{
  private static final HClientCredentialsType CREDENTIALS_GOOD =
    new HClientAccessKeys(
      "buvHtDywtJbpc5ZoptQl",
      "NdvOkckxvPeawuikDrlO1fbt1V2an6f3RaxAYLO0"
    );

  private static HClients CLIENTS;
  private static HMinIOFixture MINIO;
  private static Path DIRECTORY;

  static String minioPassword()
  {
    return "12345678";
  }

  static String minioUser()
  {
    return "someone";
  }

  static HClientCredentialsType credentials()
  {
    return CREDENTIALS_GOOD;
  }

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterSuite EContainerSupervisorType supervisor,
    final @TempDir Path directory)
    throws Exception
  {
    CLIENTS =
      new HClients();
    DIRECTORY =
      directory;
    MINIO =
      HFixtures.minio(supervisor);
  }

  protected final HMinIOFixture minio()
  {
    return MINIO;
  }

  protected final HClientType client()
    throws HException
  {
    return CLIENTS.createClient(
      HClientConfiguration.builder()
        .setHttpClientProvider(HttpClient::newHttpClient)
        .setEndpoint(URI.create("http://localhost:" + MINIO.port()))
        .setCredentials(CREDENTIALS_GOOD)
        .build()
    );
  }

  @BeforeEach
  public void setupEach(
    final @ErvillaCloseAfterSuite EContainerSupervisorType supervisor,
    final CloseableResourcesType closeables)
    throws Exception
  {
    MINIO.reset();
    MINIO.createUser(
      minioUser(),
      minioPassword(),
      CREDENTIALS_GOOD
    );
    MINIO.createBucket(
      "example-bucket-0"
    );
  }

}
