package com.sharif.thunder.commands.owner;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.OwnerCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.Command.Category;

public class RestartCommand extends OwnerCommand {
  
  private final Thunder thunder;
  
  public RestartCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "reload";
    this.help = "Kills the current instance and launches a fresh instance of this bot.";
    this.hidden = true;
  } 
  
  @Override
  protected void execute(CommandEvent event) {
    event.replySuccess("Restarting the bot with code "+Thunder.RESTART_EXITCODE+", one moment...");
    System.exit(Thunder.RESTART_EXITCODE);
  } 
} 