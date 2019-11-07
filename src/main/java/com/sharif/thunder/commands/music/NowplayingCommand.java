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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class NowplayingCommand extends MusicCommand {
  public NowplayingCommand(Thunder thunder) {
    super(thunder);
    this.name = "nowplaying";
    this.help = "shows the song that is currently playing.";
    this.aliases = new String[] {"np", "current"};
    this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
  }

  @Override
  public void doCommand(CommandEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    Message m = handler.getNowPlaying(event.getJDA());
    if (m == null) {
      event.reply(handler.getNoMusicPlaying(event.getJDA()));
      thunder.getNowplayingHandler().clearLastNPMessage(event.getGuild());
    } else {
      event.reply(m, msg -> thunder.getNowplayingHandler().setLastNPMessage(msg));
    }
  }
}
