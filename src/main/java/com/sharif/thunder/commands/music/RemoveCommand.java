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
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveCommand extends MusicCommand {
  public RemoveCommand(Thunder thunder) {
    super(thunder);
    this.name = "remove";
    this.help = "removes a song from the queue.";
    this.arguments = new Argument[] {new Argument("position|ALL", Argument.Type.SHORTSTRING, false)};
    this.aliases = new String[] {"delete"};
    this.guildOnly = true;
    this.beListening = true;
    this.bePlaying = true;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    String position = (String) args[0];
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    if (handler.getQueue().isEmpty()) {
      SenderUtil.replyError(event, "There is nothing in the queue!");
      return;
    }
    if (position.equalsIgnoreCase("all")) {
      int count = handler.getQueue().removeAll(event.getAuthor().getIdLong());
      if (count == 0) SenderUtil.replyWarning(event, "You don't have any songs in the queue!");
      else SenderUtil.replySuccess(event, "Successfully removed your " + count + " entries.");
      return;
    }
    int pos;
    try {
      pos = Integer.parseInt(position);
    } catch (NumberFormatException e) {
      pos = 0;
    }
    if (pos < 1 || pos > handler.getQueue().size()) {
      SenderUtil.replyError(event, "Position must be a valid integer between 1 and " + handler.getQueue().size() + "!");
      return;
    }
    boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
    QueuedTrack qt = handler.getQueue().get(pos - 1);
    if (qt.getIdentifier() == event.getAuthor().getIdLong()) {
      handler.getQueue().remove(pos - 1);
      SenderUtil.replySuccess(event, "Removed **" + qt.getTrack().getInfo().title + "** from the queue");
    } else if (isDJ) {
      handler.getQueue().remove(pos - 1);
      User u;
      try {
        u = event.getJDA().getUserById(qt.getIdentifier());
      } catch (Exception e) {
        u = null;
      }
      SenderUtil.replySuccess(event, "Removed **" + qt.getTrack().getInfo().title + "** from the queue (requested by " + (u == null ? "someone" : "**" + u.getName() + "**") + ")");
    } else {
      SenderUtil.replyError(event, "You cannot remove **" + qt.getTrack().getInfo().title + "** because you don't have `Manage Server` permission and you're not the requester of that song!");
    }
  }
}
