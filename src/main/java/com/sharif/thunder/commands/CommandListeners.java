package com.sharif.thunder.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandListeners implements CommandListener {

  private final Logger log = LoggerFactory.getLogger("CommandListeners");

  @Override
  public void onCommand(CommandEvent event, Command command) {
    log.info(
        event.getAuthor().getAsTag()
            + " is using a command: "
            + command.getName()
            + " "
            + event.getArgs()
            + " in "
            + event.getGuild());
  }

  @Override
  public void onCommandException(CommandEvent event, Command command, Throwable throwable) {
    if (throwable instanceof CommandErrorException) event.replyError(throwable.getMessage());
    else if (throwable instanceof CommandWarningException)
      event.replyWarning(throwable.getMessage());
    else log.error("An exception occurred in a command: " + command, throwable);
  }

  public static class CommandErrorException extends RuntimeException {
    public CommandErrorException(String message) {
      super(message);
    }
  }

  public static class CommandWarningException extends RuntimeException {
    public CommandWarningException(String message) {
      super(message);
    }
  }
}
