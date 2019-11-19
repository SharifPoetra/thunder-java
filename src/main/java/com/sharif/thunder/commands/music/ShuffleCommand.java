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

public class ShuffleCommand extends MusicCommand {
  public ShuffleCommand(Thunder thunder) {
    super(thunder);
    this.name = "shuffle";
    this.help = "shuffles songs you have added.";
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(CommandEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    int s = handler.getQueue().shuffle(event.getAuthor().getIdLong());
    switch (s) {
      case 0:
        event.replyError("You don't have any music in the queue to shuffle!");
        break;
      case 1:
        event.replyWarning("You only have one song in the queue!");
        break;
      default:
        event.replySuccess("You successfully shuffled your " + s + " entries.");
        break;
    }
  }
}
