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

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GsonUtil {
  public static Gson gson;

  static {
    gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
  }

  public static String toJSON(Object object) {
    return gson.toJson(object);
  }

  public static String toJSON(String string) {
    return gson.toJson(string);
  }

  public static <T> T fromJSON(byte[] data, Class<T> object) {
    return gson.fromJson(new String(data, Charsets.UTF_8), object);
  }

  public static <T> T fromJSON(String text, Class<T> object) {
    return gson.fromJson(text, object);
  }

  public static <T> T fromJSON(InputStream is, Class<T> object) {
    return gson.fromJson(new InputStreamReader(is), object);
  }
}
