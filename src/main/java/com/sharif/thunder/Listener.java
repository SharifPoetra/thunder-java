package com.sharif.thunder;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.JDA.ShardInfo;

public class Listener implements EventListener {
  
  private final Thunder thunder;
  
  public Listener(Thunder thunder) {
    this.thunder = thunder;
  }
  
  @Override 
  public void onEvent(GenericEvent event) {
    
    if (event instanceof GuildMessageReceivedEvent) {
      Message m = ((GuildMessageReceivedEvent)event).getMessage();
      if (m.getAuthor().isBot()) {
        return;
      }
    } else if (event instanceof ReadyEvent) {
      ShardInfo si = event.getJDA().getShardInfo();
      String shardinfo = si == null ? "1/1" : (si.getShardId()+1)+"/"+si.getShardTotal();
      System.out.println("Shard "+ shardinfo + " is ready!");
      System.out.println("Shard `"+shardinfo+"` has connected. Guilds: "
                    +event.getJDA().getGuildCache().size()+" Users: "+event.getJDA().getUserCache().size()+".");
    } 
  }
}