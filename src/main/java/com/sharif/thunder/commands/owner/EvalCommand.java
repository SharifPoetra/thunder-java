package com.sharif.thunder.commands.owner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class EvalCommand extends Command {
 
  public EvalCommand() {
    this.name = "eval";
    this.help = "evaluates nashorn code";
    this.aliases = new String[]{"e"}
    this.ownerCommand = true;
    this.guildOnly = false;
    this.hidden = true;
  }
  
  @Override
  protected void execute(CommandEvent event) {
   event.getChannel().sendTyping().queue();
    event.async(() -> {
      ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
      se.put("bot", event.getSelfUser());
      se.put("event", event);
      se.put("jda", event.getJDA());
      se.put("guild", event.getGuild());
      se.put("channel", event.getChannel());
      
      String args = event.getArgs().replaceAll("([^(]+?)\\s*->", "function($1)");
      
      try {
        event.replySuccess("Evaluated Successfully:\n```\n"+se.eval(args)+" ```");
      } catch (Exception e) {
        event.replyError("An exception was thrown:\n```\n"+e+" ```");
      } 
    });
  }
  
} 