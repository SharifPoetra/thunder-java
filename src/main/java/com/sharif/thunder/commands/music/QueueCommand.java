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
package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.menu.Paginator;
import com.sharif.thunder.Main;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.FormatUtil;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

public class QueueCommand extends MusicCommand {

  private final Paginator.Builder builder;

  public QueueCommand(Thunder thunder) {
    super(thunder);
    this.name = "queue";
    this.help = "shows the current queue.";
    this.arguments = new Argument[] {new Argument("pagenum", Argument.Type.SHORTSTRING, false)};
    this.aliases = new String[] {"q", "list"};
    this.guildOnly = true;
    this.beListening = false;
    this.bePlaying = true;
    this.botPermissions = new Permission[] {Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS};
    builder = new Paginator.Builder().setColumns(1).setFinalAction(m -> {
      try {
        m.clearReactions().queue();
      } catch (PermissionException ignore) {
      }
    })
    .setItemsPerPage(10)
    .setBulkSkipNumber(5)
    .waitOnSinglePage(false)
    .useNumberedItems(true)
    .showPageNumbers(true)
    .setEventWaiter(thunder.getWaiter())
    .setTimeout(1, TimeUnit.MINUTES);
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    String page = (String) args[0];
    int pagenum = 1;
    try {
      pagenum = Integer.parseInt(page);
    } catch (NumberFormatException ignore) {
    }
    AudioHandler ah = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    List<QueuedTrack> list = ah.getQueue().getList();
    if (list.isEmpty()) {
      Message nowp = ah.getNowPlaying(event.getJDA());
      Message nonowp = ah.getNoMusicPlaying(event.getJDA());
      Message built = new MessageBuilder()
      .setContent(thunder.getConfig().getWarning() + " There is no music in the queue!")
      .setEmbed((nowp == null ? nonowp : nowp).getEmbeds().get(0)).build();
      event.getChannel().sendMessage(built).queue(m -> {
        if (nowp != null) thunder.getNowplayingHandler().setLastNPMessage(m);
      });
      return;
    }
    String[] songs = new String[list.size()];
    long total = 0;
    for (int i = 0; i < list.size(); i++) {
      total += list.get(i).getTrack().getDuration();
      songs[i] = list.get(i).toString();
    }
    long fintotal = total;
    builder.setText((i1, i2) -> getQueueTitle(ah, thunder.getConfig().getSuccess(), songs.length, fintotal)).setItems(songs).setUsers(event.getAuthor()).setColor(event.getGuild().getSelfMember().getColor());
    builder.build().paginate(event.getChannel(), pagenum);
  }

  private String getQueueTitle(AudioHandler ah, String success, int songslength, long total) {
    StringBuilder sb = new StringBuilder();
    if (ah.getPlayer().getPlayingTrack() != null) {
      sb.append(ah.getPlayer().isPaused() ? Main.PAUSE_EMOJI : Main.PLAY_EMOJI).append(" **").append(ah.getPlayer().getPlayingTrack().getInfo().title).append("**\n");
    }
    return FormatUtil.filterEveryone(sb.append(success).append(" Current Queue | ").append(songslength).append(" entries | `").append(FormatUtil.formatTime(total)).append("` ").toString());
  }
}
