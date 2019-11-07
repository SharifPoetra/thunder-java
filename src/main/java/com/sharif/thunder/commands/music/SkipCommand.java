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
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;
import net.dv8tion.jda.api.entities.User;

public class SkipCommand extends MusicCommand {
  public SkipCommand(Thunder thunder) {
    super(thunder);
    this.name = "skip";
    this.help = "votes to skip the current song.";
    this.aliases = new String[] {"voteskip"};
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(CommandEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    if (event.getAuthor().getIdLong() == handler.getRequester()) {
      event.reply(
          event.getClient().getSuccess()
              + " Skipped **"
              + handler.getPlayer().getPlayingTrack().getInfo().title
              + "**");
      handler.stopTrack();
    } else {
      int listeners =
          (int)
              event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                  .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened())
                  .count();
      String msg;
      if (handler.getVotes().contains(event.getAuthor().getId()))
        msg = event.getClient().getWarning() + " You already voted to skip this song `[";
      else {
        msg = event.getClient().getSuccess() + " You voted to skip the song `[";
        handler.getVotes().add(event.getAuthor().getId());
      }
      int skippers =
          (int)
              event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                  .filter(m -> handler.getVotes().contains(m.getUser().getId()))
                  .count();
      int required = (int) Math.ceil(listeners * .55);
      msg += skippers + " votes, " + required + "/" + listeners + " needed]`";
      if (skippers >= required) {
        User u = event.getJDA().getUserById(handler.getRequester());
        msg +=
            "\n"
                + event.getClient().getSuccess()
                + " Skipped **"
                + handler.getPlayer().getPlayingTrack().getInfo().title
                + "**"
                + (handler.getRequester() == 0
                    ? ""
                    : " (requested by "
                        + (u == null ? "someone" : "**" + u.getName() + "**")
                        + ")");
        handler.getPlayer().stopTrack();
      }
      event.reply(msg);
    }
  }
}
