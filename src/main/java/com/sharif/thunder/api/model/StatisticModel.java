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
package com.sharif.thunder.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
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
}
