package com.sharif.thunder;

import static spark.Spark.*;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sharif.thunder.commands.CommandExceptionListener;
import com.sharif.thunder.commands.administration.*;
import com.sharif.thunder.commands.fun.*;
import com.sharif.thunder.commands.interaction.*;
import com.sharif.thunder.commands.music.*;
import com.sharif.thunder.commands.owner.*;
import com.sharif.thunder.commands.utilities.*;
import com.sharif.thunder.datasources.*;
import com.sharif.thunder.utils.*;
import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.Executors;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends ListenerAdapter {
  public static final String PLAY_EMOJI = "\u25B6"; // ▶
  public static final String PAUSE_EMOJI = "\u23F8"; // ⏸
  public static final String STOP_EMOJI = "\u23F9"; // ⏹
  public static final Permission[] RECOMMENDED_PERMS =
      new Permission[] {
        Permission.MESSAGE_READ,
        Permission.MESSAGE_WRITE,
        Permission.MESSAGE_HISTORY,
        Permission.MESSAGE_ADD_REACTION,
        Permission.MESSAGE_EMBED_LINKS,
        Permission.MESSAGE_ATTACH_FILES,
        Permission.MESSAGE_MANAGE,
        Permission.MESSAGE_EXT_EMOJI,
        Permission.MANAGE_CHANNEL,
        Permission.VOICE_CONNECT,
        Permission.VOICE_SPEAK,
        Permission.NICKNAME_CHANGE
      };

  // datasources
  private static AFKs afks;
  private static InVCRoles inVcRoles;

  public static void main(String[] args)
      throws Exception, IOException, IllegalArgumentException, LoginException,
          RateLimitedException {
    Logger log = LoggerFactory.getLogger(Main.class);

    EventWaiter waiter = new EventWaiter(Executors.newSingleThreadScheduledExecutor(), false);
    BotConfig config = new BotConfig();
    Thunder thunder = new Thunder(waiter, config);

    // datasources
    afks = new AFKs();
    inVcRoles = new InVCRoles();

    // reading datasources
    afks.read();
    inVcRoles.read();

    CommandClientBuilder client =
        new CommandClientBuilder()
            .setOwnerId(Long.toString(config.getOwnerId()))
            .setPrefix(config.getPrefix())
            .setAlternativePrefix(config.getAltPrefix())
            .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
            .setListener(new CommandExceptionListener())
            .setShutdownAutomatically(false)
            .useHelpBuilder(false)
            // .setHelpConsumer(
            //         event -> event.reply(FormatUtil.formatHelp(thunder, event)))
            .addCommands(
                // interaction
                new PatCommand(thunder),
                // administration
                new SetInVCRoleCommand(inVcRoles),
                // fun
                new ChooseCommand(thunder),
                new SayCommand(thunder),
                // utilities
                new AboutCommand(
                    Color.BLUE,
                    "a simple but powerfull multipurpose bot",
                    new String[] {"Music", "Utilities", "Lots of fun!"},
                    RECOMMENDED_PERMS),
                new UptimeCommand(thunder),
                new PingCommand(thunder),
                new EmotesCommand(thunder),
                new HelpCommand(thunder),
                new AFKCommand(afks),
                new KitsuCommand(thunder),
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
                new PauseCommand(thunder),
                new SkiptoCommand(thunder),
                // owner
                new RestartCommand(thunder),
                new DebugCommand(thunder),
                new PlaylistCommand(thunder),
                new EvalCommand(thunder));

    log.info("Loaded config from " + config.getConfigLocation());

    try {
      JDA jda =
          new JDABuilder(AccountType.BOT)
              .addEventListeners(new Main())
              .setToken(config.getToken())
              .addEventListeners(waiter, client.build())
              .build()
              .awaitReady();
    } catch (LoginException ex) {
      log.error("Something went wrong when tried to login to discord: " + ex);
      System.exit(1);
    } catch (IllegalArgumentException ex) {
      log.error(
          "Some aspect of the configuration is invalid: "
              + ex
              + "\nConfig Location: "
              + config.getConfigLocation());
      System.exit(1);
    }

    get("/", (req, res) -> "Hello World");
  }

  @Override
  public void onReady(ReadyEvent event) {
    System.out.println(event.getJDA().getSelfUser().getAsTag() + " is ready now!");
  }

  @Override
  public void onShutdown(ShutdownEvent event) {
    afks.shutdown();
    inVcRoles.shutdown();
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getAuthor() == null) return;

    if (afks.get(event.getAuthor().getId()) != null) {
      event
          .getChannel()
          .sendMessage(
              event.getAuthor().getAsMention() + " Welcome back, I have removed your AFK status.")
          .queue();
      afks.remove(event.getAuthor().getId());
    }
    if (event.getChannelType() != ChannelType.PRIVATE && !event.getAuthor().isBot()) {
      String relate =
          "__"
              + event.getGuild().getName()
              + "__ <#"
              + event.getTextChannel().getId()
              + "> **"
              + event.getAuthor().getAsTag()
              + "**:\n"
              + event.getMessage().getContentRaw();
      event.getMessage().getMentionedUsers().stream()
          .filter((u) -> (afks.get(u.getId()) != null))
          .forEach(
              (u) -> {
                u.openPrivateChannel().queue(channel -> channel.sendMessage(relate).queue());
              });
    }
    if (event.getChannelType() != ChannelType.PRIVATE
        && !event.getMessage().getMentionedUsers().isEmpty()
        && !event.getAuthor().isBot()) {
      StringBuilder builder = new StringBuilder("");
      event.getMessage().getMentionedUsers().stream()
          .forEach(
              u -> {
                if (afks.get(u.getId()) != null) {
                  String response = afks.get(u.getId())[AFKs.MESSAGE];
                  if (response != null)
                    builder
                        .append("\n\uD83D\uDCA4 **")
                        .append(u.getName())
                        .append("** is currently AFK:\n")
                        .append(response);
                }
              });
      String afkmessage = builder.toString().trim();
      if (!afkmessage.equals("")) event.getChannel().sendMessage(afkmessage).queue();
    }
  }

  @Override
  public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
    try {
      if (event.getMember().getUser().isBot()) return;
      if (inVcRoles.get(event.getGuild().getId()) != null) {
        event
            .getGuild()
            .addRoleToMember(
                event.getMember(),
                event
                    .getGuild()
                    .getRoleById(inVcRoles.get(event.getGuild().getId())[InVCRoles.ROLEID]))
            .queue();
      }
    } catch (ErrorResponseException | InsufficientPermissionException | HierarchyException ex) {
      System.out.println("Error when giving a member voice role: " + ex.toString());
    }
  }

  @Override
  public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
    try {
      if (event.getMember().getUser().isBot()) return;
      if (inVcRoles.get(event.getGuild().getId()) != null) {
        event
            .getGuild()
            .removeRoleFromMember(
                event.getMember(),
                event
                    .getGuild()
                    .getRoleById(inVcRoles.get(event.getGuild().getId())[InVCRoles.ROLEID]))
            .queue();
      }
    } catch (ErrorResponseException | InsufficientPermissionException | HierarchyException ex) {
      System.out.println("Error when removing a member voice role: " + ex.toString());
    }
  }
}
