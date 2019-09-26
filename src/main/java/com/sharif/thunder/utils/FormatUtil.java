package com.sharif.thunder.utils;

import net.dv8tion.jda.api.entities.VoiceChannel;
import java.util.List;

public class FormatUtil {
  
  private final static String MULTIPLE_FOUND = "**Multiple %s found matching \"%s\":**";
  
  public static String filterEveryone(String input) {
    return input.replace("@everyone", "@\u0435veryone")
      .replace("@here", "@h\u0435re")
      .replace("discord.gg/", "dis\u0441ord.gg/");
  }
  
  public static String secondsToTime(long timeseconds) {
    StringBuilder builder = new StringBuilder();
    int years = (int)(timeseconds / (60*60*24*365));
    if(years>0) {
      builder.append("**").append(years).append("** years, ");
      timeseconds = timeseconds % (60*60*24*365);
    }
    int weeks = (int)(timeseconds / (60*60*24*7));
    if(weeks>0) {
      builder.append("**").append(weeks).append("** weeks, ");
      timeseconds = timeseconds % (60*60*24*7);
    }
    int days = (int)(timeseconds / (60*60*24));
    if(days>0) {
      builder.append("**").append(days).append("** days, ");
      timeseconds = timeseconds % (60*60*24);
    }
    int hours = (int)(timeseconds / (60*60));
    if(hours>0) {
      builder.append("**").append(hours).append("** hours, ");
      timeseconds = timeseconds % (60*60);
    }
    int minutes = (int)(timeseconds / (60));
    if(minutes>0) {
      builder.append("**").append(minutes).append("** minutes, ");
      timeseconds = timeseconds % (60);
    }
    if(timeseconds>0)
      builder.append("**").append(timeseconds).append("** seconds");
    String str = builder.toString();
    if(str.endsWith(", "))
      str = str.substring(0,str.length()-2);
    if(str.isEmpty())
      str="**No time**";
    return str;
  }
  
  public static String secondsToTimeCompact(long timeseconds) {
    StringBuilder builder = new StringBuilder();
    int years = (int)(timeseconds / (60*60*24*365));
    if(years>0) {
      builder.append("**").append(years).append("**y ");
      timeseconds = timeseconds % (60*60*24*365);
    }
    
    int weeks = (int)(timeseconds / (60*60*24*7));
    if(weeks>0) {
      builder.append("**").append(weeks).append("**w ");
      timeseconds = timeseconds % (60*60*24*7);
    }
    int days = (int)(timeseconds / (60*60*24));
    if(days>0) {
      builder.append("**").append(days).append("**d ");
      timeseconds = timeseconds % (60*60*24);
    }
    int hours = (int)(timeseconds / (60*60));
    if(hours>0) {
      builder.append("**").append(hours).append("**h ");
      timeseconds = timeseconds % (60*60);
    }
    int minutes = (int)(timeseconds / (60));
    if(minutes>0) {
      builder.append("**").append(minutes).append("**m ");
      timeseconds = timeseconds % (60);
    }
    if(timeseconds>0)
      builder.append("**").append(timeseconds).append("**s");
    String str = builder.toString();
    if(str.endsWith(", "))
      str = str.substring(0,str.length()-2);
    if(str.isEmpty())
      str="**No time**";
    return str;
  }
} 