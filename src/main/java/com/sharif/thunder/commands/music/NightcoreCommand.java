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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.sharif.thunder.utils.SenderUtil;
import com.sharif.thunder.commands.Argument;

public class NightcoreCommand extends MusicCommand {

  private float f;

  public NightcoreCommand(Thunder thunder) {
    super(thunder);
    this.name = "nightcore";
    this.help = "toggles nightcore mode and changes it's speed.";
    this.arguments = new Argument[] {new Argument("0.1 - 3.0", Argument.Type.SHORTSTRING, true)};
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    String input = (String) args[0];
    try {
      f = Float.parseFloat(input);
    } catch (NumberFormatException e) {
      SenderUtil.replyError(event, "The given argument must be a valid integer!");
      return;
    }

    if (f < 0.1f || f > 3.0f) {
      SenderUtil.replyError(event, "Out of range 0.1 - 3.0");
      return;
    }

    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    handler.setNightcore(f);

    if (f == 1.0f) {
      SenderUtil.reply(event, "Nightcore mode disabled.");
    } else {
      SenderUtil.reply(event, "Nightcore mode enabled, speed: " + f + "");
    }
  }
}
