/*
 *   Copyright 2019-2020 SharifPoetra
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sharif.thunder.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkUtil {

  private static final Logger log = LoggerFactory.getLogger(NetworkUtil.class);
  private String content;
  private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:63.0; ThunderBot) Gecko/20100101 Firefox/63.0";
  private static OkHttpClient client = new OkHttpClient();
  private static final int MAX_LENGTH = 64 * 1024;

  private static final String BAD_REQUEST_JSON = "{\"code\": 400, \"message\": \"Bad Request\"}";
  private static final String UNAUTHORIZED_JSON = "{\"code\": 401, \"message\": \"Unauthorized\"}";
  private static final String NOT_FOUND_JSON = "{\"code\": 404, \"message\": \"Not Found\"}";
  private static final String METHOD_NOT_ALLOWED_JSON = "{\"code\": 405, \"message\": \"Method Not Allowed\"}";
  private static final String PAYLOAD_TOO_LARGE_JSON = "{\"code\": 413, \"message\": \"PayloadTooLarge\"}";
  private static final String SERVER_ERROR_JSON = "{\"code\": 500, \"message\": \"Internal Server Error\"}";

  public static byte[] download(String url) throws IOException {
    Request request = new Request.Builder().addHeader("User-Agent", USER_AGENT).url(url).build();
    Response response = client.newCall(request).execute();
    return response.body() == null ? new byte[0] : response.body().bytes();
  }

  public static byte[] download(String url, String authorization) throws IOException {
    Request request = new Request.Builder().addHeader("User-Agent", USER_AGENT).addHeader("Authorization", authorization).url(url).build();
    Response response = client.newCall(request).execute();
    return response.body() == null ? new byte[0] : response.body().bytes();
  }

  @NotNull
  public static CompletableFuture<JsonElement> getJsonOrFail(
      String method, final HttpServerExchange exchange) {
    if (!exchange.getRequestMethod().equalToString(method)) {
      exchange.setStatusCode(405).getResponseSender().send(METHOD_NOT_ALLOWED_JSON);
      return CompletableFuture.completedFuture(null);
    }

    if (exchange.getRequestContentLength() > MAX_LENGTH) {
      exchange.setStatusCode(413).getResponseSender().send(PAYLOAD_TOO_LARGE_JSON);
      return CompletableFuture.completedFuture(null);
    }

    CompletableFuture<JsonElement> future = new CompletableFuture<>();
    exchange.getRequestReceiver().receiveFullString((sex, string) -> future.complete(JsonParser.parseString(string)), (sex, err) -> future.completeExceptionally(err));
    return future;
  }

  public static void serverError(final HttpServerExchange exchange, Throwable err) {
    exchange.setStatusCode(500).getResponseSender().send(SERVER_ERROR_JSON);
    if (err != null) {
      log.error("A server error occurred", err);
    }
  }

  public static void invalidRequest(final HttpServerExchange exchange) {
    exchange.setStatusCode(400).getResponseSender().send(BAD_REQUEST_JSON);
  }

  public String getString() {
    return content;
  }
}
