package com.sharif.thunder.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalCommand extends Command {
  
  public EvalCommand() {
    this.name = "eval";
    this.help = "evaluates nashorn code";
    this.aliases = new String[]{"e"};
    this.ownerCommand = true;
    this.guildOnly = false;
    this.hidden = true;
  }
  
  @Override
  protected void execute(CommandEvent event) {
   event.getChannel().sendTyping().queue();
    event.async(() -> {
      ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
      try {
        engine.eval("var imports = new JavaImporter(" +
                    "java.io," +
                    "java.lang," +
                    "java.util," +
                    "Packages.net.dv8tion.jda.api," +
                    "Packages.net.dv8tion.jda.api.entities," +
                    "Packages.net.dv8tion.jda.api.entities.impl," +
                    "Packages.net.dv8tion.jda.api.managers," +
                    "Packages.net.dv8tion.jda.api.managers.impl," +
                    "Packages.net.dv8tion.jda.api.utils);");
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      
      try {
      engine.put("bot", event.getSelfUser());
      engine.put("event", event);
      engine.put("jda", event.getJDA());
      engine.put("guild", event.getGuild());
      engine.put("channel", event.getChannel());

      // System.out.println(event.getMessage().getContentDisplay().substring(args[0].length()));

        event.replySuccess("Evaluated Successfully:\n```\n"+engine.eval(event.getArgs())+" ```");
      } catch (Exception e) {
        event.replyError("An exception was thrown:\n```\n"+e.getMessage()+" ```");
      } 
    });
  }
  
} 