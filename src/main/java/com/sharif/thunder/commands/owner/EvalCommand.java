package com.sharif.thunder.commands.owner;

import com.sharif.thunder.commands.OwnerCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.Command;
import com.sharif.thunder.Thunder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalCommand extends OwnerCommand {
  
  private final Thunder thunder;
  
  public EvalCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "eval";
    this.help = "evaluates nashorn code";
    this.aliases = new String[]{"e"};
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
        engine.put("bot", thunder);
        engine.put("event", event);
        engine.put("jda", event.getJDA());
        engine.put("guild", event.getGuild());
        engine.put("channel", event.getChannel());
        event.replySuccess("Evaluated Successfully:\n```\n"+engine.eval(event.getArgs())+" ```");
      } catch (Exception e) {
        event.replyError("An exception was thrown:\n```\n"+e.getMessage()+" ```");
      } 
    });
  }
  
} 