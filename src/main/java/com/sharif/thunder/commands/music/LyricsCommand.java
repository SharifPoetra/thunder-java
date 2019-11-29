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

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.SenderUtil;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LyricsCommand extends MusicCommand {
  private Lyrics lyrics;

  public LyricsCommand(Thunder thunder) {
    super(thunder);
    this.name = "lyrics";
    this.help = "shows the lyrics to the currently-playing song.";
    this.arguments = new Argument[] {new Argument("song name", Argument.Type.LONGSTRING, true)};
    this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
    this.guildOnly = true;
    this.beListening = false;
    this.bePlaying = false;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    LyricsClient client = new LyricsClient();

    event.getChannel().sendTyping().queue();
    String title;
    if (args.length == 0
        && ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler())
            .isMusicPlaying(event.getJDA()))
      title =
          ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler())
              .getPlayer()
              .getPlayingTrack()
              .getInfo()
              .title;
    else title = (String) args[0];
    if (title.isEmpty()) {
      SenderUtil.replyError(event, "You must specify what lyrics you want to search!");
      return;
    }
    try {
      lyrics = client.getLyrics(title).get();
    } catch (InterruptedException | ExecutionException e) {
      SenderUtil.replyError(
          event, "Shomething went wrong when trying fetching the lyrics: " + e.getMessage());
    }

    if (lyrics == null) {
      SenderUtil.replyError(event, "Lyrics for `" + title + "` could not be found!");
      return;
    }

    EmbedBuilder eb =
        new EmbedBuilder()
            .setAuthor(lyrics.getAuthor())
            .setColor(event.getGuild().getSelfMember().getColor())
            .setTitle(lyrics.getTitle(), lyrics.getURL());
    if (lyrics.getContent().length() > 15000) {
      SenderUtil.replyWarning(
          event, "Lyrics for `" + title + "` found but likely not correct: " + lyrics.getURL());
    } else if (lyrics.getContent().length() > 2000) {
      String content = lyrics.getContent().trim();
      while (content.length() > 2000) {
        int index = content.lastIndexOf("\n\n", 2000);
        if (index == -1) index = content.lastIndexOf("\n", 2000);
        if (index == -1) index = content.lastIndexOf(" ", 2000);
        if (index == -1) index = 2000;
        event
            .getChannel()
            .sendMessage(eb.setDescription(content.substring(0, index).trim()).build())
            .queue();
        content = content.substring(index).trim();
        eb.setAuthor(null).setTitle(null, null);
      }
      event.getChannel().sendMessage(eb.setDescription(content).build()).queue();
    } else event.getChannel().sendMessage(eb.setDescription(lyrics.getContent()).build()).queue();
  }
}
