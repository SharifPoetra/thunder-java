package com.sharif.thunder.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandExceptionListener implements CommandListener {

  private final Logger log = LoggerFactory.getLogger("Command");

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
