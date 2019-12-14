/*
 *   Copyright 2019 SharifPoetra
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sharif.thunder.commands.utilities;

import static com.sharif.thunder.AsyncInfoMonitor.*;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Command;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.utils.FormatUtil;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class StatsCommand extends UtilitiesCommand {
  private final Thunder thunder;

  public StatsCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "stats";
    this.help = "See the bot, usage or vps statistics.";
    this.guildOnly = true;
    this.aliases = new String[] {"stats", "botinfo"};
    this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
    this.children = new Command[] {new UsageStatsCommand(), new ServerStatsCommand()};
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {
    String author =
        event.getJDA().getUserById(thunder.getConfig().getOwnerId()) == null
            ? "<@" + thunder.getConfig().getOwnerId() + ">"
            : event.getJDA().getUserById(thunder.getConfig().getOwnerId()).getName();

    EmbedBuilder eb =
        new EmbedBuilder()
            .setColor(event.getGuild().getSelfMember().getColor())
            .setAuthor(
                event.getJDA().getSelfUser().getName() + " Statistics",
                null,
                event.getJDA().getSelfUser().getAvatarUrl())
            .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
            .addField(
                "Users",
                event.getJDA().getUsers().size()
                    + " unique\n"
                    + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers().size()).sum()
                    + " total",
                true)
            .addField(
                "Channels",
                event.getJDA().getTextChannels().size()
                    + " Text\n"
                    + event.getJDA().getVoiceChannels().size()
                    + " Voice",
                true)
            .addField(
                "Bot Stats",
                event.getJDA().getGuilds().size()
                    + " servers\n"
                    + event.getJDA().getShardInfo()
                    + "\n"
                    + event
                        .getJDA()
                        .getAudioManagers()
                        .stream()
                        .filter(AudioManager::isConnected)
                        .count()
                    + " voice connections",
                true)
            .addField(
                "Uptime",
                FormatUtil.secondsToTime(
                    thunder.getReadyAt().until(OffsetDateTime.now(), ChronoUnit.SECONDS)),
                true)
            .setFooter("Last restart", null)
            .setTimestamp(thunder.getReadyAt());
    event.getChannel().sendMessage(eb.build()).queue();
  }

  private class UsageStatsCommand extends UtilitiesCommand {
    private UsageStatsCommand() {
      this.name = "usage";
      this.help = "The bot's (and JVM) hardware usage";
      this.guildOnly = true;
      this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    public void execute(Object[] args, MessageReceivedEvent event) {
      event
          .getChannel()
          .sendMessage(
              new EmbedBuilder()
                  .setAuthor(
                      event.getJDA().getSelfUser().getName() + "'s usage information",
                      null,
                      event.getJDA().getSelfUser().getAvatarUrl())
                  .setColor(event.getGuild().getSelfMember().getColor())
                  .setDescription("Hardware and usage information.")
                  .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                  .addField("Threads", getThreadCount() + " Threads", true)
                  .addField(
                      "Memory Usage",
                      getTotalMemory() - getFreeMemory() + "MB/" + getMaxMemory() + "MB",
                      true)
                  .addField("CPU Cores", getAvailableProcessors() + " Cores", true)
                  .addField("CPU Usage", String.format("%.2f", getInstanceCPUUsage()) + "%", true)
                  .addField("Assigned Memory", getTotalMemory() + "MB", true)
                  .addField("Remaining from assigned", getFreeMemory() + "MB", true)
                  .build())
          .queue();
    }
  }

  private class ServerStatsCommand extends UtilitiesCommand {
    private ServerStatsCommand() {
      this.name = "server";
      this.help = "The bot's hardware usage";
      this.guildOnly = true;
      this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    public void execute(Object[] args, MessageReceivedEvent event) {
      EmbedBuilder embedBuilder =
          new EmbedBuilder()
              .setColor(event.getGuild().getSelfMember().getColor())
              .setAuthor(
                  event.getJDA().getSelfUser().getName() + "'s server usage information",
                  null,
                  event.getJDA().getSelfUser().getAvatarUrl())
              .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
              .addField("CPU Usage", String.format("%.2f", getInstanceCPUUsage()) + "%", true)
              .addField(
                  "RAM (TOTAL/FREE/USED)",
                  String.format("%.2f", getVpsMaxMemory())
                      + "GB/"
                      + String.format("%.2f", getVpsFreeMemory())
                      + "GB/"
                      + String.format("%.2f", getVpsUsedMemory())
                      + "GB",
                  false);

      event.getChannel().sendMessage(embedBuilder.build()).queue();
    }
  }
}
