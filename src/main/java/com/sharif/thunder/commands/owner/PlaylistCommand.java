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
package com.sharif.thunder.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.OwnerCommand;
import com.sharif.thunder.playlist.PlaylistLoader.Playlist;
import java.io.IOException;
import java.util.List;

public class PlaylistCommand extends OwnerCommand {
  private final Thunder thunder;

  public PlaylistCommand(Thunder thunder) {
    this.thunder = thunder;
    this.guildOnly = false;
    this.name = "playlist";
    this.arguments = "<append|delete|make>";
    this.help = "playlist management.";
    this.hidden = true;
    this.children =
        new OwnerCommand[] {
          new ListCommand(), new AppendlistCommand(), new DeletelistCommand(), new MakelistCommand()
        };
  }

  @Override
  public void execute(CommandEvent event) {
    StringBuilder builder =
        new StringBuilder(event.getClient().getWarning() + " Playlist Management Commands:\n");
    for (Command cmd : this.children)
      builder
          .append("\n`")
          .append(event.getClient().getPrefix())
          .append(name)
          .append(" ")
          .append(cmd.getName())
          .append(" ")
          .append(cmd.getArguments() == null ? "" : cmd.getArguments())
          .append("` - ")
          .append(cmd.getHelp());
    event.reply(builder.toString());
  }

  public class MakelistCommand extends OwnerCommand {
    public MakelistCommand() {
      this.name = "make";
      this.aliases = new String[] {"create"};
      this.help = "makes a new playlist";
      this.arguments = "<name>";
      this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
      String pname = event.getArgs().replaceAll("\\s+", "_");
      if (thunder.getPlaylistLoader().getPlaylist(pname) == null) {
        try {
          thunder.getPlaylistLoader().createPlaylist(pname);
          event.reply(
              event.getClient().getSuccess() + " Successfully created playlist `" + pname + "`!");
        } catch (IOException e) {
          event.reply(
              event.getClient().getError()
                  + " I was unable to create the playlist: "
                  + e.getLocalizedMessage());
        }
      } else
        event.reply(event.getClient().getError() + " Playlist `" + pname + "` already exists!");
    }
  }

  public class DeletelistCommand extends OwnerCommand {
    public DeletelistCommand() {
      this.name = "delete";
      this.aliases = new String[] {"remove"};
      this.help = "deletes an existing playlist";
      this.arguments = "<name>";
      this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
      String pname = event.getArgs().replaceAll("\\s+", "_");
      if (thunder.getPlaylistLoader().getPlaylist(pname) == null)
        event.reply(event.getClient().getError() + " Playlist `" + pname + "` doesn't exist!");
      else {
        try {
          thunder.getPlaylistLoader().deletePlaylist(pname);
          event.reply(
              event.getClient().getSuccess() + " Successfully deleted playlist `" + pname + "`!");
        } catch (IOException e) {
          event.reply(
              event.getClient().getError()
                  + " I was unable to delete the playlist: "
                  + e.getLocalizedMessage());
        }
      }
    }
  }

  public class AppendlistCommand extends OwnerCommand {
    public AppendlistCommand() {
      this.name = "append";
      this.aliases = new String[] {"add"};
      this.help = "appends songs to an existing playlist";
      this.arguments = "<name> <URL> | <URL> | ...";
      this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
      String[] parts = event.getArgs().split("\\s+", 2);
      if (parts.length < 2) {
        event.reply(
            event.getClient().getError() + " Please include a playlist name and URLs to add!");
        return;
      }
      String pname = parts[0];
      Playlist playlist = thunder.getPlaylistLoader().getPlaylist(pname);
      if (playlist == null)
        event.reply(event.getClient().getError() + " Playlist `" + pname + "` doesn't exist!");
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
          event.reply(
              event.getClient().getSuccess()
                  + " Successfully added "
                  + urls.length
                  + " items to playlist `"
                  + pname
                  + "`!");
        } catch (IOException e) {
          event.reply(
              event.getClient().getError()
                  + " I was unable to append to the playlist: "
                  + e.getLocalizedMessage());
        }
      }
    }
  }

  public class ListCommand extends OwnerCommand {
    public ListCommand() {
      this.name = "all";
      this.aliases = new String[] {"available", "list"};
      this.help = "lists all available playlists";
      this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
      if (!thunder.getPlaylistLoader().folderExists()) thunder.getPlaylistLoader().createFolder();
      if (!thunder.getPlaylistLoader().folderExists()) {
        event.reply(
            event.getClient().getWarning()
                + " Playlists folder does not exist and could not be created!");
        return;
      }
      List<String> list = thunder.getPlaylistLoader().getPlaylistNames();
      if (list == null)
        event.reply(event.getClient().getError() + " Failed to load available playlists!");
      else if (list.isEmpty())
        event.reply(
            event.getClient().getWarning() + " There are no playlists in the Playlists folder!");
      else {
        StringBuilder builder =
            new StringBuilder(event.getClient().getSuccess() + " Available playlists:\n");
        list.forEach(str -> builder.append("`").append(str).append("` "));
        event.reply(builder.toString());
      }
    }
  }
}
