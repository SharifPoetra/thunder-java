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
import com.sharif.thunder.database.DatabaseConnector;
import com.sharif.thunder.playlist.PlaylistLoader;
import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.AccessLevel;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;

public class Thunder {

  @Getter(AccessLevel.NONE)
  private static JDA jda;

  @Getter private final OffsetDateTime readyAt = OffsetDateTime.now();
  @Getter private final BotConfig config;
  @Getter private final ScheduledExecutorService threadpool;
  @Getter private final PlayerManager playerManager;
  @Getter private final PlaylistLoader playlistLoader;
  @Getter private final NowplayingHandler nowplayingHandler;
  @Getter private final EventWaiter waiter;
  @Getter private final DatabaseConnector database;

  public Thunder(EventWaiter waiter, BotConfig config, DatabaseConnector database) throws Exception {
    this.waiter = waiter;
    this.config = config;
    this.database = database;
    this.playlistLoader = new PlaylistLoader(config);
    this.threadpool = Executors.newSingleThreadScheduledExecutor();
    this.playerManager = new PlayerManager(this);
    this.playerManager.init();
    this.nowplayingHandler = new NowplayingHandler(this);
    this.nowplayingHandler.init();
    playerManager.setFrameBufferDuration(300);
    playerManager.getConfiguration().setFilterHotSwapEnabled(true);
    playerManager.getConfiguration().setOpusEncodingQuality(10);
    playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
  }

  public JDA getJDA() {
    return jda;
  }

  public void setJDA(JDA jda) {
    this.jda = jda;
  }
}
