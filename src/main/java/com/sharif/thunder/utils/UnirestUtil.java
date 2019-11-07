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
