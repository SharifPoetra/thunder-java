package com.sharif.thunder.commands.music;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.FormatUtil;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;


public class QueueCommand extends MusicCommand {
  
  private final Paginator.Builder builder;
  
  public QueueCommand(Thunder thunder) {
    super(thunder);
    this.name = "queue";
    this.help = "shows the current queue.";
    this.arguments = "[pagenum]";
    this.aliases = new String[]{"list"};
    this.bePlaying = true;
    this.botPermissions = new Permission[]{Permission.MESSAGE_ADD_REACTION,Permission.MESSAGE_EMBED_LINKS};
    builder = new Paginator.Builder()
      .setColumns(1)
      .setFinalAction(m -> {try{m.clearReactions().queue();}catch(PermissionException ignore){}})
      .setItemsPerPage(10)
      .waitOnSinglePage(false)
      .useNumberedItems(true)
      .showPageNumbers(true)
      .setEventWaiter(thunder.getWaiter())
      .setTimeout(1, TimeUnit.MINUTES);
  }
  
  @Override
  public void doCommand(CommandEvent event) {
    int pagenum = 1;
    try {
      pagenum = Integer.parseInt(event.getArgs());
    } catch(NumberFormatException ignore){}
    AudioHandler ah = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
    List<QueuedTrack> list = ah.getQueue().getList();
    if(list.isEmpty()) {
      Message nowp = ah.getNowPlaying(event.getJDA());
      Message nonowp = ah.getNoMusicPlaying(event.getJDA());
      Message built = new MessageBuilder()
        .setContent(event.getClient().getWarning() + " There is no music in the queue!")
        .setEmbed((nowp==null ? nonowp : nowp).getEmbeds().get(0)).build();
      event.reply(built, m -> {
        if(nowp!=null)
          thunder.getNowplayingHandler().setLastNPMessage(m);
      });
      return;
    }
    String[] songs = new String[list.size()];
    long total = 0;
    for(int i=0; i<list.size(); i++) {
      total += list.get(i).getTrack().getDuration();
      songs[i] = list.get(i).toString();
    }
    long fintotal = total;
    builder.setText((i1,i2) -> getQueueTitle(ah, event.getClient().getSuccess(), songs.length, fintotal))
      .setItems(songs)
      .setUsers(event.getAuthor())
      .setColor(event.getSelfMember().getColor());
    builder.build().paginate(event.getChannel(), pagenum);
  }
  
    private String getQueueTitle(AudioHandler ah, String success, int songslength, long total) {
      StringBuilder sb = new StringBuilder();
      if(ah.getPlayer().getPlayingTrack()!=null) {
        sb.append(ah.getPlayer().isPaused() ? Thunder.PAUSE_EMOJI : Thunder.PLAY_EMOJI).append(" **")
          .append(ah.getPlayer().getPlayingTrack().getInfo().title).append("**\n");
      }
      return FormatUtil.filterEveryone(sb.append(success).append(" Current Queue | ").append(songslength)
             .append(" entries | `").append(FormatUtil.formatTime(total)).append("` ").toString());
    }
}