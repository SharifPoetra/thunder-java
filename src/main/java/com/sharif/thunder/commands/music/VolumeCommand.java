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

import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.FormatUtil;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VolumeCommand extends MusicCommand {
  public VolumeCommand(Thunder thunder) {
    super(thunder);
    this.name = "volume";
    this.aliases = new String[] {"vol"};
    this.help = "sets or shows volume.";
    this.arguments = new Argument[] {new Argument("1-150", Argument.Type.SHORTSTRING, false)};
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    String vo = (String) args[0];
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    int volume = handler.getPlayer().getVolume();
    if (vo.isEmpty()) {
      SenderUtil.reply(
          event, FormatUtil.volumeIcon(volume) + " Current volume is `" + volume + "`");
    } else {
      int nvolume;
      try {
        nvolume = Integer.parseUnsignedInt(vo);
      } catch (NumberFormatException e) {
        nvolume = -1;
      }
      if (nvolume < 0 || nvolume > 150)
        SenderUtil.replyError(event, "Volume must be a valid integer between 0 and 150!");
      else {
        handler.getPlayer().setVolume(nvolume);
        SenderUtil.reply(
            event,
            FormatUtil.volumeIcon(nvolume)
                + " Volume changed from `"
                + volume
                + "` to `"
                + nvolume
                + "`");
      }
    }
  }
}
