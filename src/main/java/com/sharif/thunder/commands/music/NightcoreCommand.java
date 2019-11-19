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

public class NightcoreCommand extends MusicCommand {

  private float f;

  public NightcoreCommand(Thunder thunder) {
    super(thunder);
    this.name = "nightcore";
    this.help = "toggles nightcore mode and changes it's speed.";
    this.arguments = "<0.1 - 3.0>";
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  protected void execute(CommandEvent event) {

    try {
      f = Float.parseFloat(event.getArgs());
    } catch (NumberFormatException e) {
      event.replyError("The given argument must be a valid integer!");
      return;
    }

    if (f < 0.1f || f > 3.0f) {
      event.replyError("Out of range 0.1 - 3.0");
      return;
    }

    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    handler.setNightcore(f);

    if (f == 1.0f) {
      event.reply("Nightcore mode disabled.");
    } else {
      event.reply("Nightcore mode enabled, speed: " + f + "");
    }
  }

  @Override
  public void doCommand(CommandEvent event) {
    /* Intentionally Empty */
  }
}
