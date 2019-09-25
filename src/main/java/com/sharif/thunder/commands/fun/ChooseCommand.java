package com.sharif.thunder.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ChooseCommand extends Command {
  
  public ChooseCommand() {
    this.name = "choose";
    this.help = "make a decision";
    this.arguments = "<item> <item> ...";
    this.guildOnly = true;
  } 
  
  @Override
  protected void execute(CommandEvent event) {
    
    if (event.getArgs().isEmpty()) {
      event.replyWarning("You didn't give me a choices!");
    } else {
      String[] items = event.getArgs().split("\\s+");
      if (items.length == 1) {
        event.replyWarning("You only gave me one option, `"+items[0]+"`");
      } else {
        event.replySuccess("I choose `"+items[(int)(Math.random() * items.length)]+"`");
      } 
    } 
  
  } 
  
} 