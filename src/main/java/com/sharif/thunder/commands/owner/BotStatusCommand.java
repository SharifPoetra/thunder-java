package com.sharif.thunder.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.commands.OwnerCommand;
import net.dv8tion.jda.api.entities.Activity;

public class BotStatusCommand extends OwnerCommand {

  public BotStatusCommand() {
    this.name = "botstatus";
    this.help = "Set the game I'm currently playing";
    this.arguments = "<status>";
    this.hidden = true;
  }

  @Override
  public void execute(CommandEvent event) {
    String args = event.getArgs();
    if (args.isEmpty()) {
      event.replyError("You must specify what my status to be!");
      return;
    }
    event.getJDA().getPresence().setActivity(Activity.playing(args));
    event.replySuccess("Successfully set my current playing status to " + args);
  }
}
