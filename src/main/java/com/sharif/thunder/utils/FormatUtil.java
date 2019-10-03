package com.sharif.thunder.utils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.Command.Category;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import java.util.Objects;

public class FormatUtil {

  public static String formatHelp(Thunder thunder, CommandEvent event) {
    StringBuilder builder =
        new StringBuilder(
            thunder.getConfig().getSuccess()
                + " __**"
                + event.getSelfUser().getName()
                + "** commands:__\n");
    Category category = null;
    for (Command command : event.getClient().getCommands()) {
      if (command.isHidden()) continue;
      if (command.isOwnerCommand()
          && !event.getAuthor().getId().equals(event.getClient().getOwnerId())) continue;
      if (!Objects.equals(category, command.getCategory())) {
        category = command.getCategory();
        builder
            .append("\n\n  **__")
            .append(category == null ? "No Category" : category.getName())
            .append("__:**\n");
      }
      builder
          .append("\n`")
          .append(event.getClient().getPrefix())
          .append(command.getName())
          .append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`")
          .append(" - ")
          .append(command.getHelp());
    }
    builder.append(
        "\n\nDo not include <> nor [] - <> means required and [] means optional."
            + "\nFor additional help, contact **"
            + event.getJDA().getUserById(event.getClient().getOwnerId()).getName()
            + "**#"
            + event.getJDA().getUserById(event.getClient().getOwnerId()).getDiscriminator()
            + "");
    return builder.toString();
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

  public static String secondsToTimeCompact(long timeseconds) {
    StringBuilder builder = new StringBuilder();
    int years = (int) (timeseconds / (60 * 60 * 24 * 365));
    if (years > 0) {
      builder.append("**").append(years).append("**y ");
      timeseconds = timeseconds % (60 * 60 * 24 * 365);
    }

    int weeks = (int) (timeseconds / (60 * 60 * 24 * 7));
    if (weeks > 0) {
      builder.append("**").append(weeks).append("**w ");
      timeseconds = timeseconds % (60 * 60 * 24 * 7);
    }
    int days = (int) (timeseconds / (60 * 60 * 24));
    if (days > 0) {
      builder.append("**").append(days).append("**d ");
      timeseconds = timeseconds % (60 * 60 * 24);
    }
    int hours = (int) (timeseconds / (60 * 60));
    if (hours > 0) {
      builder.append("**").append(hours).append("**h ");
      timeseconds = timeseconds % (60 * 60);
    }
    int minutes = (int) (timeseconds / (60));
    if (minutes > 0) {
      builder.append("**").append(minutes).append("**m ");
      timeseconds = timeseconds % (60);
    }
    if (timeseconds > 0) builder.append("**").append(timeseconds).append("**s");
    String str = builder.toString();
    if (str.endsWith(", ")) str = str.substring(0, str.length() - 2);
    if (str.isEmpty()) str = "**No time**";
    return str;
  }
}
