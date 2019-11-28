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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.UtilitiesCommand;
import java.time.temporal.ChronoUnit;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.sharif.thunder.commands.Argument;

public class PingCommand extends UtilitiesCommand {

  private static final String[] pingMessages =
      new String[] {
        ":ping_pong::white_small_square::black_small_square::black_small_square::ping_pong:",
        ":ping_pong::black_small_square::white_small_square::black_small_square::ping_pong:",
        ":ping_pong::black_small_square::black_small_square::white_small_square::ping_pong:",
        ":ping_pong::black_small_square::white_small_square::black_small_square::ping_pong:",
      };

  private final Thunder thunder;

  public PingCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "ping";
    this.help = "cheks the latency of the bot.";
    this.arguments = new Argument[] {new Argument("fancy", Argument.Type.SHORTSTRING, false)};
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {

    String fancy = (String)args[0];

    if (fancy != null && fancy.matches("fancy")) {
      event.getChannel().sendMessage("Checking ping...").queue(
          message -> {
            int pings = 5;
            int lastResult;
            int sum = 0, min = 999, max = 0;
            long start = System.currentTimeMillis();
            for (int j = 0; j < pings; j++) {
              message.editMessage(pingMessages[j % pingMessages.length]).queue();
              lastResult = (int) (System.currentTimeMillis() - start);
              sum += lastResult;
              min = Math.min(min, lastResult);
              max = Math.max(max, lastResult);
              try {
                Thread.sleep(1_500L);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              start = System.currentTimeMillis();
            }
            message
                .editMessage(
                    String.format(
                        "Average ping: %dms (min: %d, max: %d)  | Websocket: "
                            + event.getJDA().getGatewayPing()
                            + "ms",
                        (int) Math.ceil(sum / 5f),
                        min,
                        max))
                .queue();
          });
    } else {
      event.getChannel().sendMessage("Ping: ...").queue(
            m -> {
            long ping =
                event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage(
                    "Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms")
                .queue();
          });
    }
  }
}
