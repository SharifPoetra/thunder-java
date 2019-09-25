package com.sharif.thunder;

import com.sharif.thunder.commands.*;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

public class Main {
  
    public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException, RateLimitedException {
      
      String token = "NTgwNjI2OTcyNzQxMzM3MDg4.XYoE-g.Ev7wwjQrxxlKn3d_EIPzGSjpYI8";
      
      String ownerId = "580618094792146975";
      
      String prefix = "~";
      
      EventWaiter waiter = new EventWaiter();
      
      CommandClientBuilder client = new CommandClientBuilder();
      
      client.setOwnerId(ownerId);
      
      client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
      
      client.setPrefix(prefix);
      
      client.addCommands(
        new ChooseCommand()
      );
      
      new JDABuilder(AccountType.BOT)
        
        .setToken(token)
        .addEventListeners(waiter, client.build())
        .build();
       
    }
}