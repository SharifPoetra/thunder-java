package com.sharif.thunder.api.model;

import lombok.Getter;
import lombok.Setter;

public class StatisticModel {

  @Getter @Setter private long guildCount;
  @Getter @Setter private long channelCount;
  @Getter @Setter private long textChannelCount;
  @Getter @Setter private long voiceChannelCount;
  @Getter @Setter private long userCount;
  @Getter @Setter private long botCount;
  @Getter @Setter private long totalMemory;
  @Getter @Setter private long freeMemory;
  @Getter @Setter private long usedMemory;

  public StatisticModel() {}

  public StatisticModel(
      long guildCount,
      long channelCount,
      long textChannelCount,
      long voiceChannelCount,
      long userCount,
      long botCount,
      long totalMemory,
      long freeMemory,
      long usedMemory) {
    this.guildCount = guildCount;
    this.channelCount = channelCount;
    this.textChannelCount = textChannelCount;
    this.voiceChannelCount = voiceChannelCount;
    this.userCount = userCount;
    this.botCount = botCount;
    this.totalMemory = totalMemory;
    this.freeMemory = freeMemory;
    this.usedMemory = usedMemory;
  }

  @Override
  public String toString() {
    return "StatisticModel [guildCount="
        + guildCount
        + ", channelCount="
        + channelCount
        + ", textChannelCount="
        + textChannelCount
        + ", voiceChannelCount="
        + voiceChannelCount
        + ", userCount="
        + userCount
        + ", botCount="
        + botCount
        + ", totalMemory="
        + totalMemory
        + ", freeMemory="
        + freeMemory
        + ", usedMemory="
        + usedMemory
        + "]";
  }
}
