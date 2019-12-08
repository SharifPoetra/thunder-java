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
package com.sharif.thunder;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sharif.thunder.utils.FormatUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Getter
public class BotConfig {
  @Getter(AccessLevel.NONE)
  private Path path = null;
  private String token;
  private String prefix;
  private String altPrefix;
  private String serverInvite;
  private String success;
  private String warning;
  private String error;
  private String loading;
  private String searching;
  private String music;
  private String shuffleEmoji;
  private String repeat;
  private String playlistsFolder;
  private String databaseFolder;
  private String defaultLyrics;
  private String emiliaKey;
  @Getter(AccessLevel.NONE)
  private boolean stayInChannel;
  @Getter(AccessLevel.NONE)
  private boolean npImages;
  private long ownerId;
  private long maxSeconds;

  public BotConfig() {

    try {

      path =
          Paths.get(System.getProperty("config.file", System.getProperty("config", "config.txt")));
      if (path.toFile().exists()) {
        if (System.getProperty("config.file") == null)
          System.setProperty("config.file", System.getProperty("config", "config.txt"));
        ConfigFactory.invalidateCaches();
      }

      Config config = ConfigFactory.load();

      token = config.getString("token");
      ownerId = config.getLong("owner");
      serverInvite = config.getString("serverinvite");
      prefix = config.getString("prefix");
      altPrefix = config.getString("altprefix");
      success = config.getString("success");
      warning = config.getString("warning");
      error = config.getString("error");
      loading = config.getString("loading");
      searching = config.getString("searching");
      music = config.getString("music");
      shuffleEmoji = config.getString("shuffle");
      repeat = config.getString("repeat");
      stayInChannel = config.getBoolean("stayinchannel");
      npImages = config.getBoolean("npimages");
      maxSeconds = config.getLong("maxtime");
      playlistsFolder = config.getString("playlistsfolder");
      databaseFolder = config.getString("databasefolder");
      defaultLyrics = config.getString("lyrics.default");
      emiliaKey = config.getString("emiliakey");

    } catch (Exception ex) {
      System.out.println(
          ex + ": " + ex.getMessage() + "\n\nConfig Location: " + path.toAbsolutePath().toString());
    }
  }

  public String getConfigLocation() {
    return path.toFile().getAbsolutePath();
  }
  
  public boolean useNPImages() {
    return npImages;
  }

  public boolean getStay() {
    return stayInChannel;
  }

  public String getMaxTime() {
    return FormatUtil.formatTime(maxSeconds * 1000);
  }
  
  public boolean isTooLong(AudioTrack track) {
    if (maxSeconds <= 0) return false;
    return Math.round(track.getDuration() / 1000.0) > maxSeconds;
  }
}
