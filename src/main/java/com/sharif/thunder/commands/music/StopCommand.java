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

public class StopCommand extends MusicCommand {
  public StopCommand(Thunder thunder) {
    super(thunder);
    this.name = "stop";
    this.help = "stops the current song and clears the queue.";
    this.aliases = new String[] {"leave"};
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = false;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    handler.stopAndClear();
    event.getGuild().getAudioManager().closeAudioConnection();
    SenderUtil.replySuccess(event, "The player has stopped and the queue has been cleared.");
  }
}
