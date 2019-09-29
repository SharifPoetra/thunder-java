package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class NowplayingCommand extends MusicCommand {
  public NowplayingCommand(Thunder thunder) {
    super(thunder);
    this.name = "nowplaying";
    this.help = "shows the song that is currently playing";
    this.aliases = new String[]{"np","current"};
    this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
  }

  @Override
  public void doCommand(CommandEvent event) {
    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
    Message m = handler.getNowPlaying(event.getJDA());
    if(m==null) {
      event.reply(handler.getNoMusicPlaying(event.getJDA()));
      thunder.getNowplayingHandler().clearLastNPMessage(event.getGuild());
    } else {
      event.reply(m, msg -> thunder.getNowplayingHandler().setLastNPMessage(msg));
    }
  }
}