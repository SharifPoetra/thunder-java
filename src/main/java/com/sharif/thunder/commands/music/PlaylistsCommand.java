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
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.SenderUtil;
import java.util.List;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlaylistsCommand extends MusicCommand {
  public PlaylistsCommand(Thunder thunder) {
    super(thunder);
    this.name = "playlists";
    this.help = "shows the available playlists.";
    this.aliases = new String[] {"pls"};
    this.guildOnly = true;
    this.beListening = false;
    this.beListening = false;
  }

  @Override
  public void doCommand(Object[] args, MessageReceivedEvent event) {
    if (!thunder.getPlaylistLoader().folderExists()) thunder.getPlaylistLoader().createFolder();
    if (!thunder.getPlaylistLoader().folderExists()) {
      SenderUtil.replyWarning(event, "Playlists folder does not exist and could not be created!");
      return;
    }
    List<String> list = thunder.getPlaylistLoader().getPlaylistNames();
    if (list == null) SenderUtil.replyError(event, "Failed to load available playlists!");
    else if (list.isEmpty())
      SenderUtil.replyWarning(event, "There are no playlists in the Playlists folder!");
    else {
      StringBuilder builder = new StringBuilder("Available playlists:\n\n");
      list.forEach(str -> builder.append("`").append(str).append("` "));
      builder.append("\n\nType `").append(thunder.getConfig().getPrefix()).append("play playlist <name>` to play a playlist");
      SenderUtil.replySuccess(event, builder.toString());
    }
  }
}
