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

public class PitchCommand extends MusicCommand {

  public PitchCommand(Thunder thunder) {
    super(thunder);
    this.name = "pitch";
    this.help = "changes pitch of the song.";
    this.arguments = "<-12 - 12>";
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  protected void execute(CommandEvent event) {
    int f;
    try {
      f = Integer.parseInt(event.getArgs());
    } catch (NumberFormatException e) {
      event.replyError("The given argument must be a valid integer!");
      return;
    }

    if (f < -12 || f > 12) {
      event.replyError("Pitch out of range (-12 - 12)!");
      return;
    }

    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    handler.setPitch(f);

    if (f == 0) {
      event.replySuccess("Pitch reset!");
    } else {
      event.replySuccess("Pitch set to " + f + " semitones.");
    }
  }

  @Override
  public void doCommand(CommandEvent event) {
    /* Intentionally Empty */
  }
}
