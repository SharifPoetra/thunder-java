package com.sharif.thunder.utils;

import java.util.Map;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class UnirestUtil {

  public static byte[] getBytes(String url, Map headers) {
    HttpResponse<byte[]> response = Unirest.get(url).headers(headers).asBytes();
    return response.getBody();
  }
}
