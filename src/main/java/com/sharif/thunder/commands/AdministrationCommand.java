package com.sharif.thunder.commands;

import com.jagrosh.jdautilities.command.Command;

public abstract class AdministrationCommand extends Command {

    public AdministrationCommand() {
        this.category = new Category("Administration");
        this.guildOnly = true;
    }
}
