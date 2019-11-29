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
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class KaraokeCommand extends MusicCommand {
  public KaraokeCommand(Thunder thunder) {
    super(thunder);
    this.name = "karaoke";
    this.help = "toggles karaoke mode.";
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
    // this.children = new Command[] {new KaraokeLevelCommand(thunder), new
    // KaraokeMonoCommand(thunder)};
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    handler.setKaraoke(!handler.isKaraoke());
    SenderUtil.replySuccess(
        event, "Karaoke mode is now `" + (!handler.isKaraoke() ? "disabled" : "enabled") + "`.");
  }

  // public class KaraokeLevelCommand extends MusicCommand {

  //   public KaraokeLevelCommand(Thunder thunder) {
  //     super(thunder);
  //     this.name = "level";
  //   }

  //   @Override
  //   public void doCommand(CommandEvent event) {
  //     float f;
  //     try {
  //       f = Float.parseFloat(event.getArgs());
  //     } catch (NumberFormatException e) {
  //       event.replyError("The given argument must be a valid integer!");
  //       return;
  //     }
  //     AudioHandler handler = (AudioHandler)
  // event.getGuild().getAudioManager().getSendingHandler();
  //     handler.setKaraokeLevel(f);

  //     if (f == 1) {
  //       event.replySuccess("Karaoke level reset!");
  //     } else {
  //       event.replySuccess("Karaoke level set to " + f);
  //     }
  //   }
  // }

  // public class KaraokeMonoCommand extends MusicCommand {

  //   public KaraokeMonoCommand(Thunder thunder) {
  //     super(thunder);
  //     this.name = "mono";
  //   }

  //   @Override
  //   public void doCommand(CommandEvent event) {
  //     float f;
  //     try {
  //       f = Float.parseFloat(event.getArgs());
  //     } catch (NumberFormatException e) {
  //       event.replyError("The given argument must be a valid integer!");
  //       return;
  //     }
  //     AudioHandler handler = (AudioHandler)
  // event.getGuild().getAudioManager().getSendingHandler();
  //     handler.setKaraokeMono(f);

  //     if (f == 1) {
  //       event.replySuccess("Karaoke mono reset!");
  //     } else {
  //       event.replySuccess("Karaoke mono set to " + f);
  //     }
  //   }
  // }
}
