package com.sharif.thunder;

import com.sharif.thunder.playlist.PlaylistLoader;
import com.sharif.thunder.audio.NowplayingHandler;
import com.sharif.thunder.audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.JDA;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Objects;
import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
public class Thunder {
  
    private final OffsetDateTime readyAt = OffsetDateTime.now();
    private JDA jda;
    private final BotConfig config;
    private final ScheduledExecutorService threadpool;
    private final PlayerManager players;
    private final PlaylistLoader playlists;
    private final NowplayingHandler nowplaying;
    private final EventWaiter waiter;
  
    public Thunder(EventWaiter waiter, BotConfig config) throws Exception, IllegalArgumentException, LoginException, RateLimitedException {
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
  
    public void closeAudioConnection(long guildId) {
        Guild guild = jda.getGuildById(guildId);
        if(guild!=null)  threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
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