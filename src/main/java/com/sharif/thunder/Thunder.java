package com.sharif.thunder; 

import com.sharif.thunder.commands.fun.*;
import com.sharif.thunder.commands.owner.*;
import com.sharif.thunder.commands.music.*;
import com.sharif.thunder.commands.utilities.*;
import com.sharif.thunder.commands.CommandExceptionListener;
import com.sharif.thunder.utils.FormatUtil;
import com.sharif.thunder.playlist.PlaylistLoader;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.audio.NowplayingHandler;
import com.sharif.thunder.audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Objects;
import java.time.OffsetDateTime;

import static spark.Spark.*;

public class Thunder {
  
  public final static String PLAY_EMOJI  = "\u25B6"; // ▶
  public final static String PAUSE_EMOJI = "\u23F8"; // ⏸
  public final static String STOP_EMOJI  = "\u23F9"; // ⏹
  public static final int RESTART_EXITCODE = 11;
  public static final Logger LOGGER = LoggerFactory.getLogger("Thunder");
  private final OffsetDateTime readyAt = OffsetDateTime.now();
  private final EventWaiter waiter;
  private JDA jda;
  private final ScheduledExecutorService threadpool;
  private final PlayerManager players;
  private final PlaylistLoader playlists;
  private final NowplayingHandler nowplaying;
  private final BotConfig config;
  private boolean shuttingDown = false;
  
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
          
    CommandClientBuilder client = new CommandClientBuilder()
      .setOwnerId(Long.toString(config.getOwnerId()))
      .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
      .setPrefix(config.getPrefix())
      .setAlternativePrefix("@mention")
      .setListener(new CommandExceptionListener())
      .setShutdownAutomatically(false)
      .setHelpConsumer(event -> event.reply(FormatUtil.formatHelp(this, event)))
      .addCommands(
      // fun
      new ChooseCommand(this),
      //utilities
      new UptimeCommand(this),
      new PingCommand(this), 
      // music
      new PlayCommand(this, config.getLoading()),
      new PlaylistsCommand(this),
      new NowplayingCommand(this),
      new VolumeCommand(this),
      new SkipCommand(this),
      new StopCommand(this),
      new ShuffleCommand(this),
      new QueueCommand(this),
      new SearchCommand(this, config.getSearching()),
      new SCSearchCommand(this, config.getSearching()),
      new RepeatCommand(this),
      new NightcoreCommand(this),
      new PitchCommand(this),
      new KaraokeCommand(this),
      new VaporwaveCommand(this),
      new BassboostCommand(this),
      // new MoveTrackCommand(this),
      // new PlaynextCommand(this, config.getLoading()),
      // owner
      new RestartCommand(this), 
      new DebugCommand(this),
      new PlaylistCommand(this),
      new EvalCommand(this));
      
      new JDABuilder(AccountType.BOT)
        .setToken(config.getToken())
        .addEventListeners(waiter, client.build())
        .build();
      
      get("/", (req, res) -> "Hello World");
    }
  
  public void closeAudioConnection(long guildId) {
    Guild guild = jda.getGuildById(guildId);
    if(guild!=null)
      threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
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
  
  public static void main(String[] args) throws Exception, IllegalArgumentException, LoginException, RateLimitedException {
    
    EventWaiter waiter = new EventWaiter(Executors.newSingleThreadScheduledExecutor(), false);
    BotConfig config = new BotConfig();

    Thunder thunder = new Thunder(waiter, config);
  }
  
}