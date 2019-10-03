package com.sharif.thunder.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.OwnerCommand;
import com.sharif.thunder.utils.FormatUtil;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class DebugCommand extends OwnerCommand {

  private final Thunder thunder;

  public DebugCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "debug";
    this.help = "shows some debug stats";
    this.hidden = true;
  }

  @Override
  protected void execute(CommandEvent event) {
    long totalMb = Runtime.getRuntime().totalMemory() / (1024 * 1024);
    long usedMb =
        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
    StringBuilder sb =
        new StringBuilder(
            "**"
                + event.getSelfUser().getName()
                + "** statistics:"
                + "\nLast Startup: "
                + FormatUtil.secondsToTime(
                    thunder.getReadyAt().until(OffsetDateTime.now(), ChronoUnit.SECONDS))
                + " ago"
                + "\nGuilds: **"
                + event.getClient().getTotalGuilds()
                + "**"
                + "\nMemory: **"
                + usedMb
                + "**Mb / **"
                + totalMb
                + "**Mb"
                + "\nAverage Ping: **"
                + event.getJDA().getGatewayPing()
                + "**ms");
    event.reply(sb.toString().trim());
  }
}
