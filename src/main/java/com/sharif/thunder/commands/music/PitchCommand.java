package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;

public class PitchCommand extends MusicCommand {

  public PitchCommand(Thunder thunder) {
    super(thunder);
    this.name = "pitch";
    this.help = "changes pitch of the song.";
    this.arguments = "<-12 - 12>";
  }

  @Override
  protected void execute(CommandEvent event) {
    int f;
    try {
      f = Integer.parseInt(event.getArgs());
    } catch (NumberFormatException e) {
      event.replyError("The given argument must be a number.");
      return;
    }

    if (f < -12 || f > 12) {
      event.replyError("Pitch out of range (-12 - 12)!");
      return;
    }

    AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
    handler.setPitch(f);

    if (f == 0) {
      event.reply("Pitch reset!");
    } else {
      event.reply("Pitch set to " + f + " semitones.");
    }
  }

  @Override
  public void doCommand(CommandEvent event) {
    /* Intentionally Empty */
  }
}
