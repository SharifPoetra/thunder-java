package com.sharif.thunder.commands.owner;

import java.time.OffsetDateTime;
import com.sharif.thunder.commands.OwnerCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.utils.FormatUtil;
import java.time.temporal.ChronoUnit;
import net.dv8tion.jda.api.JDA;

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
    long totalMb = Runtime.getRuntime().totalMemory()/(1024*1024);
    long usedMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024);
    StringBuilder sb = new StringBuilder("**"+event.getSelfUser().getName()+"** statistics:"
                + "\nLast Startup: "+FormatUtil.secondsToTime(thunder.getReadyAt().until(OffsetDateTime.now(), ChronoUnit.SECONDS))+" ago"
                + "\nGuilds: **"+thunder.getShardManager().getGuildCache().size()+"**"
                + "\nMemory: **"+usedMb+"**Mb / **"+totalMb+"**Mb"
                + "\nAverage Ping: **"+thunder.getShardManager().getAverageGatewayPing()+"**ms"
                + "\nShard Total: **"+thunder.getShardManager().getShardsTotal()+"**"
                + "\nShard Connectivity: ```diff");
    thunder.getShardManager().getShards().forEach(jda -> sb.append("\n").append(jda.getStatus()==JDA.Status.CONNECTED ? "+ " : "- ")
                .append(jda.getShardInfo().getShardId()<10 ? "0" : "").append(jda.getShardInfo().getShardId()).append(": ").append(jda.getStatus())
                .append(" ~ ").append(jda.getGuildCache().size()).append(" guilds"));
    sb.append("\n```");
    event.reply(sb.toString().trim());
    }
}