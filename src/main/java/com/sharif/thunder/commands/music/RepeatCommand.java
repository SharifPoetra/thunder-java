package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.audio.AudioHandler;

public class RepeatCommand extends MusicCommand {
  public RepeatCommand(Thunder thunder) {
    super(thunder);
    this.name = "repeat";
    this.help = "re-adds music to the queue when finished";
    this.guildOnly = true;
  }
  
  // override musiccommand's execute because we don't actually care where this is used
  @Override
  protected void execute(CommandEvent event) {
    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
    handler.setRepeating(!handler.isRepeating());
    event.replySuccess("Repeat mode is now `"+(!handler.isRepeating() ? "disabled" : "enabled")+"`.");
  } 
  
  @Override
  public void doCommand(CommandEvent event) { /* Intentionally Empty */ }
}