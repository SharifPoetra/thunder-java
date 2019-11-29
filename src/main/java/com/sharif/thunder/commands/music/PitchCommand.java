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
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PitchCommand extends MusicCommand {

  private int f;

  public PitchCommand(Thunder thunder) {
    super(thunder);
    this.name = "pitch";
    this.help = "changes pitch of the song.";
    this.arguments = new Argument[] {new Argument("-12 - 12", Argument.Type.SHORTSTRING, true)};
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    String input = (String) args[0];
    try {
      f = Integer.parseInt(input);
    } catch (NumberFormatException e) {
      SenderUtil.replyError(event, "The given argument must be a valid integer!");
      return;
    }

    if (f < -12 || f > 12) {
      SenderUtil.replyError(event, "Pitch out of range (-12 - 12)!");
      return;
    }

    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    handler.setPitch(f);

    if (f == 0) {
      SenderUtil.replySuccess(event, "Pitch reset!");
    } else {
      SenderUtil.replySuccess(event, "Pitch set to " + f + " semitones.");
    }
  }
}
