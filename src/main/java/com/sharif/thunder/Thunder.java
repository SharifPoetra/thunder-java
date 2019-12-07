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

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import com.sharif.thunder.audio.NowplayingHandler;
import com.sharif.thunder.audio.PlayerManager;
import com.sharif.thunder.playlist.PlaylistLoader;
import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.JDA;

public class Thunder {

  private final OffsetDateTime readyAt = OffsetDateTime.now();
  private static JDA jda;
  private final BotConfig config;
  private final ScheduledExecutorService threadpool;
  private final PlayerManager players;
  private final PlaylistLoader playlists;
  private final NowplayingHandler nowplaying;
  private final EventWaiter waiter;

  public Thunder(EventWaiter waiter, BotConfig config) throws Exception {
    this.waiter = waiter;
    this.config = config;
    this.playlists = new PlaylistLoader(config);
    this.threadpool = Executors.newSingleThreadScheduledExecutor();
    this.players = new PlayerManager(this);
    this.players.init();
    this.nowplaying = new NowplayingHandler(this);
    this.nowplaying.init();
    players.setFrameBufferDuration(300);
    players.getConfiguration().setFilterHotSwapEnabled(true);
    players.getConfiguration().setOpusEncodingQuality(10);
    players.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
  }

  public NowplayingHandler getNowplayingHandler() {
    return nowplaying;
  }

  public EventWaiter getWaiter() {
    return waiter;
  }

  public PlayerManager getPlayerManager() {
    return players;
  }

  public BotConfig getConfig() {
    return config;
  }

  public ScheduledExecutorService getThreadpool() {
    return threadpool;
  }

  public PlaylistLoader getPlaylistLoader() {
    return playlists;
  }

  public JDA getJDA() {
    return jda;
  }

  public EventWaiter getEventWaiter() {
    return waiter;
  }

  public OffsetDateTime getReadyAt() {
    return readyAt;
  }

  public void setJDA(JDA jda) {
    this.jda = jda;
  }
}
