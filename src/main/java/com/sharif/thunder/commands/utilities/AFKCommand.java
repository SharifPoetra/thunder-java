package com.sharif.thunder.commands.utilities;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.datasources.AFKs;

public class AFKCommand extends UtilitiesCommand {
    private final AFKs afks;

    public AFKCommand(AFKs afks) {
        this.afks = afks;
        this.name = "afk";
        this.help = "relays mentions via DM; can autoreply message.";
        this.arguments = "[message]";
    }

    @Override
    protected void execute(CommandEvent event) {
        String message = event.getArgs() == null ? null : event.getArgs();
        afks.set(new String[] {event.getAuthor().getId(), message});
        event.reply("⌨️ | " + event.getAuthor().getAsMention() + ", I've set you to AFK mode.");
    }
}
