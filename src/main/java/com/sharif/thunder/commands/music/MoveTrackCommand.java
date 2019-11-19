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
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.queue.FairQueue;

public class MoveTrackCommand extends MusicCommand {

  public MoveTrackCommand(Thunder thunder) {
    super(thunder);
    this.name = "movetrack";
    this.help = "move a track in the current queue to a different position.";
    this.arguments = "<from> <to>";
    this.aliases = new String[] {"move"};
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(CommandEvent event) {
    int from;
    int to;

    String[] parts = event.getArgs().split("\\s+", 2);
    if (parts.length < 2) {
      event.replyError("Please include two valid indexes.");
      return;
    }

    try {
      // Validate the args
      from = Integer.parseInt(parts[0]);
      to = Integer.parseInt(parts[1]);
    } catch (NumberFormatException e) {
      event.replyError("Please provide two valid indexes.");
      return;
    }

    if (from == to) {
      event.replyError("Can't move a track to the same position.");
      return;
    }

    // Validate that from and to are available
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    FairQueue<QueuedTrack> queue = handler.getQueue();
    if (isUnavailablePosition(queue, from)) {
      String reply = String.format("`%d` is not a valid position in the queue!", from);
      event.replyError(reply);
      return;
    }
    if (isUnavailablePosition(queue, to)) {
      String reply = String.format("`%d` is not a valid position in the queue!", to);
      event.replyError(reply);
      return;
    }

    // Move the track
    QueuedTrack track = queue.moveItem(from - 1, to - 1);
    String trackTitle = track.getTrack().getInfo().title;
    String reply = String.format("Moved **%s** from position `%d` to `%d`.", trackTitle, from, to);
    event.replySuccess(reply);
  }

  private static boolean isUnavailablePosition(FairQueue<QueuedTrack> queue, int position) {
    return (position < 1 || position > queue.size());
  }
}
