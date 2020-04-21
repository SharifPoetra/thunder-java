/*
 *   Copyright 2019-2020 SharifPoetra
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

import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NowplayingCommand extends MusicCommand {
  public NowplayingCommand(Thunder thunder) {
    super(thunder);
    this.name = "nowplaying";
    this.help = "shows the song that is currently playing.";
    this.aliases = new String[] {"np", "current"};
    this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
    this.guildOnly = true;
    this.beListening = false;
    this.bePlaying = false;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    Message m = handler.getNowPlaying(event.getJDA());
    if (m == null) {
      event.getChannel().sendMessage(handler.getNoMusicPlaying(event.getJDA())).queue();
      thunder.getNowplayingHandler().clearLastNPMessage(event.getGuild());
    } else {
      event.getChannel().sendMessage(m).queue(msg -> thunder.getNowplayingHandler().setLastNPMessage(msg));
    }
  }
}
