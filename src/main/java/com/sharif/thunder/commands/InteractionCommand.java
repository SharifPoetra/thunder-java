package com.sharif.thunder.commands;

import com.jagrosh.jdautilities.command.Command;

public abstract class InteractionCommand extends Command {

  public InteractionCommand() {
    this.category = new Category("Interaction");
    this.guildOnly = true;
  }
}
