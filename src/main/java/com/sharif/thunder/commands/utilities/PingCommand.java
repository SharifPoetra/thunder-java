package com.sharif.thunder.commands.utilities;

import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.Thunder;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import java.time.temporal.ChronoUnit;

public class PingCommand extends UtilitiesCommand {
  
  private static final String[] pingMessages = new String[]{
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
    this.arguments = "[fancy]";
  } 
  
  @Override
  protected void execute(CommandEvent event) {
    
    String args = event.getArgs();
    
    if (args.matches("fancy")) {
      event.reply("Checking ping...", message -> {
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
        message.editMessage(String.format("Average ping: %dms (min: %d, max: %d)  | Websocket: " + event.getJDA().getGatewayPing() + "ms", (int) Math.ceil(sum / 5f), min, max)).queue();
      });
    } else {
      event.reply("Ping: ...", m -> {
        long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
        m.editMessage("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
      });
    } 
  }
}