package com.sharif.thunder;

import com.sharif.thunder.commands.fun.*;
import com.sharif.thunder.commands.owner.*;
import com.sharif.thunder.commands.music.*;
import com.sharif.thunder.commands.utilities.*;
import com.sharif.thunder.commands.CommandExceptionListener;
import com.sharif.thunder.utils.FormatUtil;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;
import static spark.Spark.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  public final static String PLAY_EMOJI  = "\u25B6"; // ▶
  public final static String PAUSE_EMOJI = "\u23F8"; // ⏸
  public final static String STOP_EMOJI  = "\u23F9"; // ⏹
  
  public static void main(String[] args) throws Exception, IllegalArgumentException, LoginException, RateLimitedException {
    
    Logger log = LoggerFactory.getLogger("Startup");
    
    EventWaiter waiter = new EventWaiter(Executors.newSingleThreadScheduledExecutor(), false);
    BotConfig config = new BotConfig();
    Thunder thunder = new Thunder(waiter, config);
    
    CommandClientBuilder client = new CommandClientBuilder()
      .setOwnerId(Long.toString(config.getOwnerId()))
      .setPrefix(config.getPrefix())
      .setAlternativePrefix(config.getAltPrefix())
      .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
      .setListener(new CommandExceptionListener())
      .setShutdownAutomatically(false)
      .setHelpConsumer(event -> event.reply(FormatUtil.formatHelp(thunder, event)))
      .addCommands(
      // fun
      new ChooseCommand(thunder),
      //utilities
      new UptimeCommand(thunder),
      new PingCommand(thunder), 
      // music
      new PlayCommand(thunder, config.getLoading()),
      new PlaylistsCommand(thunder),
      new NowplayingCommand(thunder),
      new VolumeCommand(thunder),
      new SkipCommand(thunder),
      new StopCommand(thunder),
      new ShuffleCommand(thunder),
      new QueueCommand(thunder),
      new SearchCommand(thunder, config.getSearching()),
      new SCSearchCommand(thunder, config.getSearching()),
      new RepeatCommand(thunder),
      new NightcoreCommand(thunder),
      new PitchCommand(thunder),
      new KaraokeCommand(thunder),
      new VaporwaveCommand(thunder),
      new BassboostCommand(thunder),
      new MoveTrackCommand(thunder),
      new PlaynextCommand(thunder, config.getLoading()),
      new LyricsCommand(thunder),
      new RemoveCommand(thunder),
      // owner
      new RestartCommand(thunder), 
      new DebugCommand(thunder),
      new PlaylistCommand(thunder),
      new EvalCommand(thunder)
    );
    
    log.info("Loaded config from "+config.getConfigLocation());
    
    try {
      JDA jda = new JDABuilder(AccountType.BOT)
        .setToken(config.getToken())
        .addEventListeners(waiter, client.build())
        .build();
    } catch (LoginException ex) {
      log.error("Something went wrong when tried to login to discord: " + ex);
      System.exit(1);
    } catch (IllegalArgumentException ex) {
      log.error("Some aspect of the configuration is invalid: " + ex + "\nConfig Location: " + config.getConfigLocation());
      System.exit(1);
    }
    
    get("/", (req, res) -> "Hello World");
  }
}