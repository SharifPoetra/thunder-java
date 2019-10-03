package com.sharif.thunder.commands.utilities;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.UtilitiesCommand;
import java.lang.management.ManagementFactory;

public class UptimeCommand extends UtilitiesCommand {

    private final Thunder thunder;

    public UptimeCommand(Thunder thunder) {
        this.thunder = thunder;
        this.name = "uptime";
        this.help = "displays the amount of time that the bot has been up.";
    }

    @Override
    protected void execute(CommandEvent event) {
        final long duration = ManagementFactory.getRuntimeMXBean().getUptime();
        final long years = duration / 31104000000L;
        final long months = duration / 2592000000L % 12;
        final long days = duration / 86400000L % 30;
        final long hours = duration / 3600000L % 24;
        final long minutes = duration / 60000L % 60;
        final long seconds = duration / 1000L % 60;
        final long milliseconds = duration % 1000;

        String uptime =
                (years == 0 ? "" : "**" + years + "** Years, ")
                        + (months == 0 ? "" : "**" + months + "** Months, ")
                        + (days == 0 ? "" : "**" + days + "** Days, ")
                        + (hours == 0 ? "" : "**" + hours + "** Hours, ")
                        + (minutes == 0 ? "" : "**" + minutes + "** Minutes, ")
                        + (seconds == 0 ? "" : "**" + seconds + "** Seconds, ")
                        + (milliseconds == 0 ? "" : "**" + milliseconds + "** Milliseconds, ");

        uptime = replaceLast(uptime, ", ", "");
        uptime = replaceLast(uptime, ",", " and");

        event.reply("I've been online for:\n" + uptime);
    }

    private String replaceLast(final String text, final String regex, final String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
