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

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.dv8tion.jda.api.entities.*;

public class FormatUtil {

  public static String[] cleanSplit(String input) {
    return cleanSplit(input, "\\s+", 2);
  }

  public static String[] cleanSplit(String input, int size) {
    return cleanSplit(input, "\\s+", size);
  }

  public static String[] cleanSplit(String input, String regex) {
    return cleanSplit(input, regex, 2);
  }

  public static String[] cleanSplit(String input, String regex, int size) {
    return Arrays.copyOf(input.trim().split(regex, size), size);
  }

  public static String appendAttachmentUrls(Message message) {
    return appendAttachmentUrls(message, message.getContentRaw());
  }

  public static String appendAttachmentUrls(Message message, String content) {
    if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
      StringBuilder builder = content == null ? new StringBuilder() : new StringBuilder(content);
      message
          .getAttachments()
          .stream()
          .map((att) -> " " + att.getUrl())
          .forEach(url -> builder.append(" ").append(url));
      return builder.toString();
    }
    return content;
  }
  
  public static String listOfMembers(List<Member> list, String query) {
    String out = String.format("**Multiple %s found matching \"%s\":**", "members", query);
    for (int i = 0; i < 6 && i < list.size(); i++)
      out += "\n - " + list.get(i).getUser().getName() + " #" + list.get(i).getUser().getDiscriminator();
    if (list.size() > 6) out += "\n**And " + (list.size() - 6) + " more...**";
    return out;
  }

  public static String listOfUsers(List<User> list, String query) {
    String out = String.format("**Multiple %s found matching \"%s\":**", "users", query);
    for (int i = 0; i < 6 && i < list.size(); i++)
      out += "\n - " + list.get(i).getName() + " #" + list.get(i).getDiscriminator();
    if (list.size() > 6) out += "\n**And " + (list.size() - 6) + " more...**";
    return out;
  }

  public static String listOfRoles(List<Role> list, String query) {
    String out = " Multiple roles found matching \"" + query + "\":";
    for (int i = 0; i < 6 && i < list.size(); i++)
      out += "\n - " + list.get(i).getName() + " (ID:" + list.get(i).getId() + ")";
    if (list.size() > 6) out += "\n**And " + (list.size() - 6) + " more...**";
    return out;
  }

  public static String listOfChannels(List<TextChannel> list, String query) {
    String out = String.format("**Multiple %s found matching \"%s\":**", "text channels", query);
    for (int i = 0; i < 6 && i < list.size(); i++)
      out += "\n - " + list.get(i).getName() + " (<#" + list.get(i).getId() + ">)";
    if (list.size() > 6) out += "\n**And " + (list.size() - 6) + " more...**";
    return out;
  }

  public static String listOfGuilds(List<Guild> list, String query) {
    String out = String.format("**Multiple %s found matching \"%s\":**", "servers", query);
    for (int i = 0; i < 6 && i < list.size(); i++)
      out += "\n - " + list.get(i).getName() + " (ID:" + list.get(i).getId() + ")";
    if (list.size() > 6) out += "\n**And " + (list.size() - 6) + " more...**";
    return out;
  }

  public static String zeroHexFill(String s) {
    if (s.length() < 4) {

      StringBuilder sb = new StringBuilder(s);
      while (sb.length() < 4) sb.insert(0, "0");

      return sb.toString();
    }
    return s;
  }

  public static String capitalize(String input) {
    if (input == null || input.isEmpty()) return "";
    if (input.length() == 1) return input.toUpperCase();
    return Character.toUpperCase(input.charAt(0)) + input.substring(1).toLowerCase();
  }

  public static String join(String delimiter, char... items) {
    if (items == null || items.length == 0) return "";
    StringBuilder sb = new StringBuilder().append(items[0]);
    for (int i = 1; i < items.length; i++) sb.append(delimiter).append(items[i]);
    return sb.toString();
  }

  @SafeVarargs
  public static <T> String join(String delimiter, Function<T, String> function, T... items) {
    if (items == null || items.length == 0) return "";
    StringBuilder sb = new StringBuilder(function.apply(items[0]));
    for (int i = 1; i < items.length; i++) sb.append(delimiter).append(function.apply(items[i]));
    return sb.toString();
  }

  public static String filterEveryone(String input) {
    return input
        .replace("@everyone", "@\u0435veryone")
        .replace("@here", "@h\u0435re")
        .replace("discord.gg/", "dis\u0441ord.gg/");
  }

  public static String formatTime(long duration) {
    if (duration == Long.MAX_VALUE) return "LIVE";
    long seconds = Math.round(duration / 1000.0);
    long hours = seconds / (60 * 60);
    seconds %= 60 * 60;
    long minutes = seconds / 60;
    seconds %= 60;
    return (hours > 0 ? hours + ":" : "")
        + (minutes < 10 ? "0" + minutes : minutes)
        + ":"
        + (seconds < 10 ? "0" + seconds : seconds);
  }

  public static String progressBar(double percent) {
    String str = "";
    for (int i = 0; i < 12; i++)
      if (i == (int) (percent * 12)) str += "\uD83D\uDD18"; // 🔘
      else str += "▬";
    return str;
  }

  public static String volumeIcon(int volume) {
    if (volume == 0) return "\uD83D\uDD07"; // 🔇
    if (volume < 30) return "\uD83D\uDD08"; // 🔈
    if (volume < 70) return "\uD83D\uDD09"; // 🔉
    return "\uD83D\uDD0A"; // 🔊
  }

  public static String secondsToTime(long timeseconds) {
    StringBuilder builder = new StringBuilder();
    int years = (int) (timeseconds / (60 * 60 * 24 * 365));
    if (years > 0) {
      builder.append("**").append(years).append("** years, ");
      timeseconds = timeseconds % (60 * 60 * 24 * 365);
    }
    int weeks = (int) (timeseconds / (60 * 60 * 24 * 7));
    if (weeks > 0) {
      builder.append("**").append(weeks).append("** weeks, ");
      timeseconds = timeseconds % (60 * 60 * 24 * 7);
    }
    int days = (int) (timeseconds / (60 * 60 * 24));
    if (days > 0) {
      builder.append("**").append(days).append("** days, ");
      timeseconds = timeseconds % (60 * 60 * 24);
    }
    int hours = (int) (timeseconds / (60 * 60));
    if (hours > 0) {
      builder.append("**").append(hours).append("** hours, ");
      timeseconds = timeseconds % (60 * 60);
    }
    int minutes = (int) (timeseconds / (60));
    if (minutes > 0) {
      builder.append("**").append(minutes).append("** minutes, ");
      timeseconds = timeseconds % (60);
    }
    if (timeseconds > 0) builder.append("**").append(timeseconds).append("** seconds");
    String str = builder.toString();
    if (str.endsWith(", ")) str = str.substring(0, str.length() - 2);
    if (str.isEmpty()) str = "**No time**";
    return str;
  }
}
