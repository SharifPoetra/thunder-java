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
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BassboostCommand extends MusicCommand {
  public BassboostCommand(Thunder thunder) {
    super(thunder);
    this.name = "bassboost";
    this.help = "toggles bassboost mode.";
    this.aliases = new String[] {"bb"};
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    handler.setBassboost(!handler.isBassboost());
    SenderUtil.replySuccess(event, "Bassboost mode is now `" + (!handler.isBassboost() ? "disabled" : "enabled") + "`.");
  }
}
