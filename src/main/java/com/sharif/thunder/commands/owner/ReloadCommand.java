package com.sharif.thunder.commands.owner;

import com.sharif.thunder.Thunder;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ReloadCommand extends Command {
 
  public ReloadCommand() {
    this.name = "reload";
    this.help = "Kills the current instance and launches a fresh instance of this bot.";
    this.ownerCommand = true;
    this.guildOnly = false;
    this.hidden = true;
  } 
  
  @Override
  protected void execute(CommandEvent event) {
    event.replySuccess("Restarting the bot with code "+Thunder.RESTART_EXITCODE+", one moment...");
    System.exit(Thunder.RESTART_EXITCODE);
  } 
} 