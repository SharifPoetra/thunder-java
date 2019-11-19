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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.FormatUtil;
import com.sharif.thunder.utils.OtherUtil;
import net.dv8tion.jda.api.entities.Message;

public class PlaynextCommand extends MusicCommand {
  private final String loadingEmoji;

  public PlaynextCommand(Thunder thunder, String loadingEmoji) {
    super(thunder);
    this.loadingEmoji = loadingEmoji;
    this.name = "playnext";
    this.arguments = "<title|URL>";
    this.help = "plays a single song next.";
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(CommandEvent event) {
    if (event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty()) {
      event.replyWarning("Please include a song title or URL!");
      return;
    }
    String args =
        event.getArgs().startsWith("<") && event.getArgs().endsWith(">")
            ? event.getArgs().substring(1, event.getArgs().length() - 1)
            : event.getArgs().isEmpty()
                ? event.getMessage().getAttachments().get(0).getUrl()
                : event.getArgs();
    event.reply(
        loadingEmoji + " Loading... `[" + args + "]`",
        m ->
            thunder
                .getPlayerManager()
                .loadItemOrdered(event.getGuild(), args, new ResultHandler(m, event, false)));
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

    private void loadSingle(AudioTrack track) {
      if (thunder.getConfig().isTooLong(track)) {
        m.editMessage(
                FormatUtil.filterEveryone(
                    event.getClient().getWarning()
                        + " This track (**"
                        + track.getInfo().title
                        + "**) is longer than the allowed maximum: `"
                        + FormatUtil.formatTime(track.getDuration())
                        + "` > `"
                        + FormatUtil.formatTime(thunder.getConfig().getMaxSeconds() * 1000)
                        + "`"))
            .queue();
        return;
      }
      AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
      handler.setAnnouncingChannel(event.getChannel().getIdLong());
      int pos = handler.addTrackToFront(new QueuedTrack(track, event.getAuthor())) + 1;
      String addMsg =
          FormatUtil.filterEveryone(
              event.getClient().getSuccess()
                  + " Added **"
                  + track.getInfo().title
                  + "** (`"
                  + FormatUtil.formatTime(track.getDuration())
                  + "`) "
                  + (pos == 0 ? "" : " to the queue at position " + pos));
      m.editMessage(addMsg)
          .queue(
              (m) -> {
                OtherUtil.deleteMessageAfter(m, track.getDuration());
              });
    }

    @Override
    public void trackLoaded(AudioTrack track) {
      loadSingle(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
      AudioTrack single;
      if (playlist.getTracks().size() == 1 || playlist.isSearchResult())
        single =
            playlist.getSelectedTrack() == null
                ? playlist.getTracks().get(0)
                : playlist.getSelectedTrack();
      else if (playlist.getSelectedTrack() != null) single = playlist.getSelectedTrack();
      else single = playlist.getTracks().get(0);
      loadSingle(single);
    }

    @Override
    public void noMatches() {
      if (ytsearch)
        m.editMessage(
                FormatUtil.filterEveryone(
                    event.getClient().getWarning()
                        + " No results found for `"
                        + event.getArgs()
                        + "`."))
            .queue();
      else
        thunder
            .getPlayerManager()
            .loadItemOrdered(
                event.getGuild(), "ytsearch:" + event.getArgs(), new ResultHandler(m, event, true));
    }

    @Override
    public void loadFailed(FriendlyException throwable) {
      if (throwable.severity == FriendlyException.Severity.COMMON)
        m.editMessage(event.getClient().getError() + " Error loading: " + throwable.getMessage())
            .queue();
      else m.editMessage(event.getClient().getError() + " Error loading track.").queue();
    }
  }
}
