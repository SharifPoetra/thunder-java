package com.sharif.thunder.commands;

import com.jagrosh.jdautilities.command.Command;

public abstract class FunCommand extends Command {

    public FunCommand() {
        this.category = new Category("Fun");
        this.guildOnly = true;
    }
}
