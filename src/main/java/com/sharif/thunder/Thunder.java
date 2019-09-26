package com.sharif.thunder; 

import com.sharif.thunder.commands.fun.*;
import com.sharif.thunder.commands.owner.*;
import com.sharif.thunder.commands.utilities.*;
import com.sharif.thunder.events.*;
import com.sharif.thunder.commands.CommandExceptionListener;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.exceptions.PermissionException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.Executors;

import static spark.Spark.*;

public class Thunder {
  
  public static final int RESTART_EXITCODE = 11;
  
  private final EventWaiter waiter;
  
  public Thunder() throws Exception, IllegalArgumentException, LoginException, RateLimitedException {
    
    String token = "NTgwNjI2OTcyNzQxMzM3MDg4.XYoE-g.Ev7wwjQrxxlKn3d_EIPzGSjpYI8";
    String ownerId = "580618094792146975";   
    String prefix = "~";
    
    waiter = new EventWaiter(Executors.newSingleThreadScheduledExecutor(), false);
      
    CommandClientBuilder client = new CommandClientBuilder()
      .setOwnerId(ownerId)
      .setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26")
      .setPrefix(prefix)
      .setListener(new CommandExceptionListener())
      .setShutdownAutomatically(false)
      .addCommands(
      
      // fun
      new ChooseCommand(),
      //utilities
      new UptimeCommand(),
      // owner
      new ReloadCommand(), 
      new EvalCommand());
      
      new JDABuilder(AccountType.BOT)
        
        .setToken(token)
        .addEventListeners(new Ready())
        .addEventListeners(waiter, client.build())
        .build();
      
      get("/", (req, res) -> "Hello World");
    }
  
  public EventWaiter getEventWaiter() {
    return waiter;
  }
  
  public static void main(String[] args) throws Exception, IllegalArgumentException, LoginException, RateLimitedException {
    new Thunder();
  }
  
}