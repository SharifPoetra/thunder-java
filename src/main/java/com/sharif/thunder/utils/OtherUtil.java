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

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class OtherUtil {

  public static String scrub(String string, boolean encodeBlank) {
    string = string.replaceAll("[!@#$%^&*(),.?\":{}|<>]", "");

    return encodeBlank ? string.replace(" ", "%20") : string;
  }

  public static void deleteMessageAfter(Message message, long delay) {
    message.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
  }

  public static String loadResource(Object clazz, String name) {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(clazz.getClass().getResourceAsStream(name)))) {
      StringBuilder sb = new StringBuilder();
      reader.lines().forEach(line -> sb.append("\r\n").append(line));
      return sb.toString().trim();
    } catch (IOException ex) {
      return null;
    }
  }

  public static InputStream imageFromUrl(String url) {
    if (url == null) return null;
    try {
      URL u = new URL(url);
      URLConnection urlConnection = u.openConnection();
      urlConnection.setRequestProperty(
          "user-agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
      return urlConnection.getInputStream();
    } catch (IOException | IllegalArgumentException ignore) {
    }
    return null;
  }

  public static List<Guild> findGuild(String query, JDA jda) {
    String id;
    if (query.matches("[Ii][Dd]\\s*:\\s*\\d+")) {
      id = query.replaceAll("[Ii][Dd]\\s*:\\s*(\\d+)", "$1");
      Guild g = jda.getGuildById(id);
      if (g != null) return Collections.singletonList(g);
    }
    ArrayList<Guild> exact = new ArrayList<>();
    ArrayList<Guild> wrongcase = new ArrayList<>();
    ArrayList<Guild> startswith = new ArrayList<>();
    ArrayList<Guild> contains = new ArrayList<>();
    String lowerQuery = query.toLowerCase();
    jda.getGuilds()
        .stream()
        .forEach(
            (guild) -> {
              if (guild.getName().equals(query)) exact.add(guild);
              else if (guild.getName().equalsIgnoreCase(query) && exact.isEmpty())
                wrongcase.add(guild);
              else if (guild.getName().toLowerCase().startsWith(lowerQuery) && wrongcase.isEmpty())
                startswith.add(guild);
              else if (guild.getName().toLowerCase().contains(lowerQuery) && startswith.isEmpty())
                contains.add(guild);
            });
    if (!exact.isEmpty()) return exact;
    if (!wrongcase.isEmpty()) return wrongcase;
    if (!startswith.isEmpty()) return startswith;
    return contains;
  }
}
