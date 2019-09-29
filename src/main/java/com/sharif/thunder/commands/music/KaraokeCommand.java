package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.audio.AudioHandler;

public class KaraokeCommand extends MusicCommand {
  public KaraokeCommand(Thunder thunder) {
    super(thunder);
    this.name = "karaoke";
    this.help = "toggles karaoke mode.";
    this.guildOnly = true;
  }
  
  // override musiccommand's execute because we don't actually care where this is used
  @Override
  protected void execute(CommandEvent event) {
    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
    handler.setKaraoke(!handler.isKaraoke());
    event.replySuccess("Repeat mode is now `"+(!handler.isKaraoke() ? "disabled" : "enabled")+"`.");
  } 
  
  @Override
  public void doCommand(CommandEvent event) { /* Intentionally Empty */ }
}