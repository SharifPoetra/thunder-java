package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.FormatUtil;

public class VolumeCommand extends MusicCommand {
  public VolumeCommand(Thunder thunder) {
    super(thunder);
    this.name = "volume";
    this.aliases = new String[]{"vol"};
    this.help = "sets or shows volume.";
    this.arguments = "[0-150]";
  }
  
  @Override
  public void doCommand(CommandEvent event) {
    
    // event.reply("Sorry! the volume command are currently disabled due to still buggy, Thanks for your understanding.");
    // return;
    // TODO: fix filter feature not work after adjusting the volume
    
    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
    int volume = handler.getPlayer().getVolume();
    if(event.getArgs().isEmpty()) {
      event.reply(FormatUtil.volumeIcon(volume)+" Current volume is `"+volume+"`");
    } else {
      int nvolume;
      try {
        nvolume = Integer.parseUnsignedInt(event.getArgs());
      } catch(NumberFormatException e) {
        nvolume = -1;
      }
      if(nvolume<0 || nvolume>150)
        event.reply(event.getClient().getError()+" Volume must be a valid integer between 0 and 150!");
      else {
        handler.getPlayer().setVolume(nvolume);
        event.reply(FormatUtil.volumeIcon(nvolume)+" Volume changed from `"+volume+"` to `"+nvolume+"`");
      }
    }
  }
}