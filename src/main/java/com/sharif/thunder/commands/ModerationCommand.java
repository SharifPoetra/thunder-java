package com.sharif.thunder.commands;

import com.jagrosh.jdautilities.command.Command;

public abstract class UtilitiesCommand extends Command {

    public UtilitiesCommand() {
        this.category = new Category("Moderation");
        this.guildOnly = true;
    }
}
