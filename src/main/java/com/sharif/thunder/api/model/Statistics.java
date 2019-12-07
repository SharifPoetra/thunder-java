package com.sharif.thunder.api.model;

public class Statistics {

  private long guilds;
  private long channels;
  private long textChannels;
  private long voiceChannels;
  private long users;
  private long bots;
  private long totalMemory;
  private long freeMemory;
  private long usedMemory;

  public Statistics(
      long guilds,
      long channels,
      long textChannels,
      long voiceChannels,
      long users,
      long bots,
      long totalMemory,
      long freeMemory,
      long usedMemory) {
    this.guilds = guilds;
    this.channels = channels;
    this.textChannels = textChannels;
    this.voiceChannels = voiceChannels;
    this.users = users;
    this.bots = bots;
    this.totalMemory = totalMemory;
    this.freeMemory = freeMemory;
    this.usedMemory = usedMemory;
  }

  public long getGuildCount() {
    return guilds;
  }

  public long getChannelCount() {
    return channels;
  }

  public long getTextChannelCount() {
    return textChannels;
  }

  public long getVoiceChannelCount() {
    return voiceChannels;
  }

  public long getUserCount() {
    return users;
  }

  public long getBotCount() {
    return bots;
  }

  public long getTotalMemory() {
    return totalMemory;
  }

  public long getFreeMemory() {
    return freeMemory;
  }

  public long getUsedMemory() {
    return usedMemory;
  }
}
