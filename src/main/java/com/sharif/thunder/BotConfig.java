/*
 *   Copyright 2019-2020 SharifPoetra
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
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class BotConfig {

  @Getter(AccessLevel.NONE)
  public Path path = null;
  public String token;
  public String prefix;
  public String serverInvite;
  public String success;
  public String warning;
  public String error;
  public String loading;
  public String searching;
  public String music;
  public String shuffleEmoji;
  public String repeat;
  public String playlistsFolder;
  public String databaseFolder;
  public String dbHost;
  public String dbUser;
  public String dbPass;
  public String defaultLyrics;
  public String emiliaKey;
  @Getter(AccessLevel.NONE)
  public boolean stayInChannel;
  @Getter(AccessLevel.NONE)
  public boolean npImages;
  @Getter
  public int port;
  public long ownerId;
  public long maxSeconds;

  public BotConfig() {

    try {
      path = Paths.get(System.getProperty("config.file", System.getProperty("config", "config.txt")));
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
      dbHost = config.getString("dbhost");
      dbUser = config.getString("dbuser");
      dbPass = config.getString("dbpass");
      defaultLyrics = config.getString("lyrics.default");
      emiliaKey = config.getString("emiliakey");
      port = config.getInt("port");
    } catch (Exception ex) {
      System.out.println(ex + ": " + ex.getMessage() + "\n\nConfig Location: " + path.toAbsolutePath().toString());
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
