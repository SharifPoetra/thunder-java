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
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.queue.FairQueue;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MoveTrackCommand extends MusicCommand {

  public MoveTrackCommand(Thunder thunder) {
    super(thunder);
    this.name = "movetrack";
    this.help = "move a track in the current queue to a different position.";
    this.arguments = new Argument[] {new Argument("from> <to", Argument.Type.SHORTSTRING, true)};
    this.aliases = new String[] {"move"};
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    int from;
    int to;

    String aparts = (String) args[0];
    String[] parts = aparts.split("\\s+", 2);
    if (parts.length < 2) {
      SenderUtil.replyError(event, "Please include two valid indexes.");
      return;
    }

    try {
      // Validate the args
      from = Integer.parseInt(parts[0]);
      to = Integer.parseInt(parts[1]);
    } catch (NumberFormatException e) {
      SenderUtil.replyError(event, "Please provide two valid indexes.");
      return;
    }

    if (from == to) {
      SenderUtil.replyError(event, "Can't move a track to the same position.");
      return;
    }

    // Validate that from and to are available
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    FairQueue<QueuedTrack> queue = handler.getQueue();
    if (isUnavailablePosition(queue, from)) {
      String reply = String.format("`%d` is not a valid position in the queue!", from);
      SenderUtil.replyError(event, reply);
      return;
    }
    if (isUnavailablePosition(queue, to)) {
      String reply = String.format("`%d` is not a valid position in the queue!", to);
      SenderUtil.replyError(event, reply);
      return;
    }

    // Move the track
    QueuedTrack track = queue.moveItem(from - 1, to - 1);
    String trackTitle = track.getTrack().getInfo().title;
    String reply = String.format("Moved **%s** from position `%d` to `%d`.", trackTitle, from, to);
    SenderUtil.replySuccess(event, reply);
  }

  private static boolean isUnavailablePosition(FairQueue<QueuedTrack> queue, int position) {
    return (position < 1 || position > queue.size());
  }
}
