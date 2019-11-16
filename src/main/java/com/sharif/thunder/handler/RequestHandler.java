/*
 *   Copyright 2019 SharifPoetra
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
package com.sharif.thunder.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sharif.thunder.handler.entity.RequestProperty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {
  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
  private static final OkHttpClient client = new OkHttpClient();
  private String content;

  /**
   * RequestHandler method which takes a url and optional arguments as request properties.
   *
   * @param url String: the url used for the connection
   * @param requestProperties RequestProperty: the optional request properties with default
   *     application/json when none are given.
   */
  public RequestHandler(String url, RequestProperty... requestProperties) {
    Request.Builder builder = new Request.Builder().url(url);

    for (RequestProperty property : requestProperties) {
      builder.addHeader(property.getHeader(), property.getDirective());
    }

    try (Response response = client.newCall(builder.build()).execute()) {
      if (response.code() == 200) {
        this.content = response.body().string();
      }
    } catch (Exception ex) {
      log.error(
          "An error occurred while running the {} class, message: {}", this, ex.getMessage(), ex);
    }
  }

  /**
   * Retrieves the content as a string, does nothing else to it.
   *
   * @return String
   */
  public String getString() {
    return content;
  }

  /**
   * Retrieves the content as a JsonObject which can be handled and manipulated with the Google Gson
   * package.
   *
   * @return {@link JsonObject}
   * @throws IllegalStateException IllegalStateException
   */
  public JsonObject getJsonObject() throws IllegalStateException {
    try {
      return (content == null) ? null : JsonParser.parseString(content).getAsJsonObject();
    } catch (Exception ex) {
      log.error(
          "An error occurred while running the {} class, message: {}", this, ex.getMessage(), ex);
      return null;
    }
  }

  /**
   * Retrieves the content as a JsonArray which can be handled and manipulated with the Google Gson
   * package.
   *
   * @return {@link JsonArray}
   * @throws IllegalStateException IllegalStateException
   */
  public JsonArray getJsonArray() throws IllegalStateException {
    try {
      return (content == null) ? null : JsonParser.parseString(content).getAsJsonArray();
    } catch (Exception ex) {
      log.error(
          "An error occurred while running the {} class, message: {}", this, ex.getMessage(), ex);
      return null;
    }
  }

  /**
   * Retrieves the content as a Jsoup Document.
   *
   * @return {@link Document}
   */
  public Document getDocument() {
    try {
      return (content == null) ? null : Jsoup.parse(content);
    } catch (Exception ex) {
      log.error(
          "An error occurred while running the {} class, message: {}", this, ex.getMessage(), ex);
      return null;
    }
  }
}
