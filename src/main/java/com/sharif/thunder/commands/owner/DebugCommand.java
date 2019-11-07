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
