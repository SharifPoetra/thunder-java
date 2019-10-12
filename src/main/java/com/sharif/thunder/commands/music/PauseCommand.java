package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;

public class PauseCommand extends MusicCommand {
  public PauseCommand(Thunder thunder) {
    super(thunder);
    this.name = "pause";
    this.help = "pauses the current song.";
    this.bePlaying = true;
  }

  @Override
  public void doCommand(CommandEvent event) {
    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    if (handler.getPlayer().isPaused()) {
      event.replyWarning(
          "The player is already paused! Use `"
              + event.getClient().getPrefix()
              + "play` to unpause!");
      return;
    }
    handler.getPlayer().setPaused(true);
    event.replySuccess(
        "Paused **"
            + handler.getPlayer().getPlayingTrack().getInfo().title
            + "**. Type `"
            + event.getClient().getPrefix()
            + "play` to unpause!");
  }
}
