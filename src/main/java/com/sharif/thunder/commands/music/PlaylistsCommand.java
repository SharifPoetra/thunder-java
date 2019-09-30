package com.sharif.thunder.commands.music;

import java.util.List;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.MusicCommand;

public class PlaylistsCommand extends MusicCommand {
  public PlaylistsCommand(Thunder thunder) {
    super(thunder);       
    this.name = "playlists";
    this.help = "shows the available playlists.";    
    this.aliases = new String[]{"pls"};
    this.guildOnly = true;
    this.beListening = false;
    this.beListening = false;
  } 
  
  @Override
  public void doCommand(CommandEvent event) {
    if(!thunder.getPlaylistLoader().folderExists())
      thunder.getPlaylistLoader().createFolder();
    if(!thunder.getPlaylistLoader().folderExists()) {
      event.reply(event.getClient().getWarning()+" Playlists folder does not exist and could not be created!");
      return;
    }
    List<String> list = thunder.getPlaylistLoader().getPlaylistNames();
    if(list==null)
      event.reply(event.getClient().getError()+" Failed to load available playlists!");
    else if(list.isEmpty())
      event.reply(event.getClient().getWarning()+" There are no playlists in the Playlists folder!");
    else {
      StringBuilder builder = new StringBuilder(event.getClient().getSuccess()+" Available playlists:\n\n");
      list.forEach(str -> builder.append("`").append(str).append("` "));
      builder.append("\n\nType `").append(event.getClient().getTextualPrefix()).append("play playlist <name>` to play a playlist");
      event.reply(builder.toString());
    }
  }
}