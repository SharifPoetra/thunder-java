package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;

public class VaporwaveCommand extends MusicCommand {
  public VaporwaveCommand(Thunder thunder) {
    super(thunder);
    this.name = "vaporwave";
    this.help = "toggles vaporwave mode.";
    this.guildOnly = true;
  }

  @Override
  protected void execute(CommandEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    handler.setVaporwave(!handler.isVaporwave());
    event.replySuccess(
        "Vaporwave mode is now `" + (!handler.isVaporwave() ? "disabled" : "enabled") + "`.");
  }

  @Override
  public void doCommand(CommandEvent event) {
    /* Intentionally Empty */
  }
}
