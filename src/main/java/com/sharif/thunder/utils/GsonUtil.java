package com.sharif.thunder.utils;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GsonUtil {
  public static Gson gson;

  static {
    gson = new GsonBuilder().disableHtmlEscaping().create();
  }

  public static String toJSON(Object object) {
    return gson.toJson(object);
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
