package com.sharif.thunder;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sharif.thunder.utils.*;
import com.typesafe.config.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BotConfig {
  private Path path = null;
  private String token,
      prefix,
      altPrefix,
      successEmoji,
      warningEmoji,
      errorEmoji,
      loadingEmoji,
      searchingEmoji,
      musicEmoji,
      shuffleEmoji,
      repeatEmoji,
      playlistsFolder,
      emiliaKey,
      sentryKey;
  private boolean stayInChannel, npImages, updateAlerts;
  private long owner, maxSeconds;

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
      owner = config.getLong("owner");
      prefix = config.getString("prefix");
      altPrefix = config.getString("altprefix");
      successEmoji = config.getString("success");
      warningEmoji = config.getString("warning");
      errorEmoji = config.getString("error");
      loadingEmoji = config.getString("loading");
      searchingEmoji = config.getString("searching");
      musicEmoji = config.getString("music");
      shuffleEmoji = config.getString("shuffle");
      repeatEmoji = config.getString("repeat");
      stayInChannel = config.getBoolean("stayinchannel");
      npImages = config.getBoolean("npimages");
      maxSeconds = config.getLong("maxtime");
      updateAlerts = config.getBoolean("updatealerts");
      playlistsFolder = config.getString("playlistsfolder");
      emiliaKey = config.getString("emiliakey");
      sentryKey = config.getString("sentrykey");

    } catch (Exception ex) {
      System.out.println(
          ex + ": " + ex.getMessage() + "\n\nConfig Location: " + path.toAbsolutePath().toString());
    }
  }

  public String getConfigLocation() {
    return path.toFile().getAbsolutePath();
  }

  public String getToken() {
    return token;
  }

  public long getOwnerId() {
    return owner;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getAltPrefix() {
    return altPrefix;
  }

  public String getSuccess() {
    return successEmoji;
  }

  public String getWarning() {
    return warningEmoji;
  }

  public String getError() {
    return errorEmoji;
  }

  public String getLoading() {
    return loadingEmoji;
  }

  public String getSearching() {
    return searchingEmoji;
  }

  public String getMusic() {
    return musicEmoji;
  }

  public String getShuffle() {
    return shuffleEmoji;
  }

  public String getRepeat() {
    return repeatEmoji;
  }

  public boolean getStay() {
    return stayInChannel;
  }

  public boolean useUpdateAlerts() {
    return updateAlerts;
  }

  public boolean useNPImages() {
    return npImages;
  }

  public long getMaxSeconds() {
    return maxSeconds;
  }

  public String getMaxTime() {
    return FormatUtil.formatTime(maxSeconds * 1000);
  }

  public String getPlaylistsFolder() {
    return playlistsFolder;
  }

  public String getEmiliaKey() {
    return emiliaKey;
  }

  public String getSentryKey() {
    return sentryKey;
  }

  public boolean isTooLong(AudioTrack track) {
    if (maxSeconds <= 0) return false;
    return Math.round(track.getDuration() / 1000.0) > maxSeconds;
  }
}
