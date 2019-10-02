package com.sharif.thunder.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.playlist.PlaylistLoader.Playlist;
import com.sharif.thunder.utils.FormatUtil;
import com.sharif.thunder.utils.OtherUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.PermissionException;

public class PlayCommand extends MusicCommand {
  private final static String LOAD = "\uD83D\uDCE5"; // 📥
  private final static String CANCEL = "\uD83D\uDEAB"; // 🚫
    
  private final String loadingEmoji;
    
  public PlayCommand(Thunder thunder, String loadingEmoji) {
    super(thunder);
    this.loadingEmoji = loadingEmoji;
    this.name = "play";
    this.arguments = "<title|URL|subcommand>";
    this.help = "plays the provided song.";
    this.aliases = new String[]{"p"};
    this.beListening = true;
    this.bePlaying = false;
    this.children = new Command[]{new PlaylistCommand(thunder)};
  }

  @Override
  public void doCommand(CommandEvent event) {
    if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty()) {
      AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
      handler.setAnnouncingChannel(event.getChannel().getIdLong());
      if(handler.getPlayer().getPlayingTrack()!=null && handler.getPlayer().isPaused()) {
        handler.getPlayer().setPaused(false);
        event.reply("" + thunder.getConfig().getMusic()+ " Resumed **"+handler.getPlayer().getPlayingTrack().getInfo().title+"**.");
      return;
    }
      StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" Play Commands:\n");
      builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <song title>` - plays the first result from Youtube");
      builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <URL>` - plays the provided song, playlist, or stream");
      for(Command cmd: children)
        builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
      event.reply(builder.toString());
      return;
    }
    String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">") 
      ? event.getArgs().substring(1,event.getArgs().length()-1) 
      : event.getArgs().isEmpty() ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();
    event.reply(loadingEmoji+" Loading... `["+args+"]`", m -> thunder.getPlayerManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m,event,false)));
  }
    
  private class ResultHandler implements AudioLoadResultHandler {
    private final Message m;
    private final CommandEvent event;
    private final boolean ytsearch;
    
    private ResultHandler(Message m, CommandEvent event, boolean ytsearch) {
      this.m = m;
      this.event = event;
      this.ytsearch = ytsearch;
    }
    
    private void loadSingle(AudioTrack track, AudioPlaylist playlist) {
      if(thunder.getConfig().isTooLong(track)) {
        m.editMessage(FormatUtil.filterEveryone(event.getClient().getWarning()+" This track (**"+track.getInfo().title+"**) is longer than the allowed maximum: `"
                      +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(thunder.getConfig().getMaxSeconds()*1000)+"`")).queue();
        return;
      }
      AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
      handler.setAnnouncingChannel(event.getChannel().getIdLong());
      int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
      String addMsg = FormatUtil.filterEveryone(thunder.getConfig().getMusic()+" Added **"+track.getInfo().title
                      +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos == 0 ? "":" to the queue at position "+pos));
      if(playlist==null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
        m.editMessage(addMsg).queue((m) -> {
        OtherUtil.deleteMessageAfter(m, track.getDuration());
      });
      else {
        new ButtonMenu.Builder()
          .setText(addMsg+"\n"+event.getClient().getWarning()+" This track has a playlist of **"+playlist.getTracks().size()+"** tracks attached. Select "+LOAD+" to load playlist.")
          .setChoices(LOAD, CANCEL)
          .setEventWaiter(thunder.getWaiter())
          .setTimeout(30, TimeUnit.SECONDS)
          .setAction(re -> {
            if(re.getName().equals(LOAD))
              m.editMessage(addMsg+"\n"+event.getClient().getSuccess()+" Loaded **"+loadPlaylist(playlist, track)+"** additional tracks!").queue();
            else
              m.editMessage(addMsg).queue();
          }).setFinalAction(m -> {
          try {
            m.clearReactions().queue();
          } catch(PermissionException ignore) {}
        }).build().display(m);
      }
    }
        
    private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude) {
      int[] count = {0};
      playlist.getTracks().stream().forEach((track) -> {
        if(!thunder.getConfig().isTooLong(track) && !track.equals(exclude)) {
          AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
          handler.setAnnouncingChannel(event.getChannel().getIdLong());
          handler.addTrack(new QueuedTrack(track, event.getAuthor()));
          count[0]++;
        }
      });
      return count[0];
    }
    
    @Override
    public void trackLoaded(AudioTrack track) {
      loadSingle(track, null);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
      if(playlist.getTracks().size()==1 || playlist.isSearchResult()) {
        AudioTrack single = playlist.getSelectedTrack()==null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
        loadSingle(single, null);
      } else if (playlist.getSelectedTrack()!=null) {
        AudioTrack single = playlist.getSelectedTrack();
        loadSingle(single, playlist);
      } else {
        int count = loadPlaylist(playlist, null);
        if(count==0) {
          m.editMessage(FormatUtil.filterEveryone(event.getClient().getWarning()+" All entries in this playlist "+(playlist.getName()==null ? "" : "(**"+playlist.getName()
                        +"**) ")+"were longer than the allowed maximum (`"+thunder.getConfig().getMaxTime()+"`)")).queue();
        } else {
          m.editMessage(FormatUtil.filterEveryone(event.getClient().getSuccess()+" Found "
                            +(playlist.getName()==null?"a playlist":"playlist **"+playlist.getName()+"**")+" with `"
                            + playlist.getTracks().size()+"` entries; added to the queue!"
                            + (count<playlist.getTracks().size() ? "\n"+event.getClient().getWarning()+" Tracks longer than the allowed maximum (`"
                            + thunder.getConfig().getMaxTime()+"`) have been omitted." : ""))).queue();
        }
      }
    }

    @Override
    public void noMatches() {
      if(ytsearch)
        m.editMessage(FormatUtil.filterEveryone(event.getClient().getWarning()+" No results found for `"+event.getArgs()+"`.")).queue();
      else
        thunder.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+event.getArgs(), new ResultHandler(m,event,true));
    }

    @Override
    public void loadFailed(FriendlyException throwable) {
      if(throwable.severity==Severity.COMMON)
        m.editMessage(event.getClient().getError()+" Error loading: "+throwable.getMessage()).queue();
      else
        m.editMessage(event.getClient().getError()+" Error loading track.").queue();
    }
  }
   
  public class PlaylistCommand extends MusicCommand {
    public PlaylistCommand(Thunder thunder) {
      super(thunder);
      this.name = "playlist";
      this.aliases = new String[]{"pl"};
      this.arguments = "<name>";
      this.help = "plays the provided playlist";
      this.beListening = true;
      this.bePlaying = false;
    }
    
    @Override
    public void doCommand(CommandEvent event) {
      if(event.getArgs().isEmpty()) {
        event.reply(event.getClient().getError()+" Please include a playlist name.");
        return;
      }
      Playlist playlist = thunder.getPlaylistLoader().getPlaylist(event.getArgs());
      if(playlist==null) {
        event.replyError("I could not find `"+event.getArgs()+".txt` in the Playlists folder.");
        return;
      }
      event.getChannel().sendMessage(loadingEmoji+" Loading playlist **"+event.getArgs()+"**... ("+playlist.getItems().size()+" items)").queue(m -> {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        handler.setAnnouncingChannel(event.getChannel().getIdLong());
        playlist.loadTracks(thunder.getPlayerManager(), (at)->handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
          StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty() 
                                                    ? event.getClient().getWarning()+" No tracks were loaded!" 
                                                    : event.getClient().getSuccess()+" Loaded **"+playlist.getTracks().size()+"** tracks!");
          if(!playlist.getErrors().isEmpty())
            builder.append("\nThe following tracks failed to load:");
          playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex()+1).append("]` **").append(err.getItem()).append("**: ").append(err.getReason()));
          String str = builder.toString();
          if(str.length()>2000)
            str = str.substring(0,1994)+" (...)";
          m.editMessage(FormatUtil.filterEveryone(str)).queue();
        });
      });
    }
  }
}