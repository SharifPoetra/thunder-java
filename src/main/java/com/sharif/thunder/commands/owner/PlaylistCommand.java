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
package com.sharif.thunder.commands.owner;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.Command;
import com.sharif.thunder.commands.OwnerCommand;
import com.sharif.thunder.playlist.PlaylistLoader.Playlist;
import com.sharif.thunder.utils.SenderUtil;
import java.io.IOException;
import java.util.List;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlaylistCommand extends OwnerCommand {
  private final Thunder thunder;

  public PlaylistCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "playlist";
    this.help = "playlist management.";
    this.hidden = true;
    this.children = new Command[] {new ListCommand(), new AppendlistCommand(), new DeletelistCommand(), new MakelistCommand()};
  }

  @Override
  public void execute(Object[] args, MessageReceivedEvent event) {
    StringBuilder builder = new StringBuilder(thunder.getConfig().getWarning() + " Playlist Management Commands:\n");
    for (Command cmd : this.children)
      builder.append("\n`").append(thunder.getConfig().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(Argument.arrayToString(cmd.getArguments())).append("` - ").append(cmd.getHelp());
    SenderUtil.reply(event, builder.toString());
  }

  private class MakelistCommand extends OwnerCommand {
    private MakelistCommand() {
      this.name = "make";
      this.aliases = new String[] {"create"};
      this.help = "makes a new playlist";
      this.arguments = new Argument[] {new Argument("name", Argument.Type.SHORTSTRING, true, 3, 20)};
    }

    @Override
    protected void execute(Object[] args, MessageReceivedEvent event) {
      String pname = (String) args[0];
      pname.replaceAll("\\s+", "_");
      if (thunder.getPlaylistLoader().getPlaylist(pname) == null) {
        try {
          thunder.getPlaylistLoader().createPlaylist(pname);
          SenderUtil.replySuccess(event, "Successfully created playlist `" + pname + "`!");
        } catch (IOException e) {
          SenderUtil.replyError(event, "I was unable to create the playlist: " + e.getLocalizedMessage());
        }
      } else SenderUtil.replyError(event, "Playlist `" + pname + "` already exists!");
    }
  }

  private class DeletelistCommand extends OwnerCommand {
    private DeletelistCommand() {
      this.name = "delete";
      this.aliases = new String[] {"remove"};
      this.help = "deletes an existing playlist";
      this.arguments = new Argument[] {new Argument("name", Argument.Type.SHORTSTRING, true, 3, 20)};
    }

    @Override
    protected void execute(Object[] args, MessageReceivedEvent event) {
      String pname = (String) args[0];
      pname.replaceAll("\\s+", "_");
      if (thunder.getPlaylistLoader().getPlaylist(pname) == null)
        SenderUtil.replyError(event, "Playlist `" + pname + "` doesn't exist!");
      else {
        try {
          thunder.getPlaylistLoader().deletePlaylist(pname);
          SenderUtil.replySuccess(event, "Successfully deleted playlist `" + pname + "`!");
        } catch (IOException e) {
          SenderUtil.replyError(
              event, "I was unable to delete the playlist: " + e.getLocalizedMessage());
        }
      }
    }
  }

  private class AppendlistCommand extends OwnerCommand {
    private AppendlistCommand() {
      this.name = "append";
      this.aliases = new String[] {"add"};
      this.help = "appends songs to an existing playlist";
      this.arguments = new Argument[] {new Argument("name> <URL> | <URL> | <...", Argument.Type.LONGSTRING, true)};
    }

    @Override
    protected void execute(Object[] args, MessageReceivedEvent event) {
      String aparts = (String) args[0];
      String[] parts = aparts.split("\\s+", 2);
      if (parts.length < 2) {
        SenderUtil.replyError(event, "Please include a playlist name and URLs to add!");
        return;
      }
      String pname = parts[0];
      Playlist playlist = thunder.getPlaylistLoader().getPlaylist(pname);
      if (playlist == null) SenderUtil.replyError(event, "Playlist `" + pname + "` doesn't exist!");
      else {
        StringBuilder builder = new StringBuilder();
        playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
        String[] urls = parts[1].split("\\|");
        for (String url : urls) {
          String u = url.trim();
          if (u.startsWith("<") && u.endsWith(">")) u = u.substring(1, u.length() - 1);
          builder.append("\r\n").append(u);
        }
        try {
          thunder.getPlaylistLoader().writePlaylist(pname, builder.toString());
          SenderUtil.replySuccess(event, "Successfully added " + urls.length + " items to playlist `" + pname + "`!");
        } catch (IOException e) {
          SenderUtil.replyError(event, "I was unable to append to the playlist: " + e.getLocalizedMessage());
        }
      }
    }
  }

  private class ListCommand extends OwnerCommand {
    private ListCommand() {
      this.name = "all";
      this.aliases = new String[] {"available", "list"};
      this.help = "lists all available playlists";
    }

    @Override
    protected void execute(Object[] args, MessageReceivedEvent event) {
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
        StringBuilder builder = new StringBuilder(thunder.getConfig().getSuccess() + " Available playlists:\n");
        list.forEach(str -> builder.append("`").append(str).append("` "));
        SenderUtil.reply(event, builder.toString());
      }
    }
  }
}
