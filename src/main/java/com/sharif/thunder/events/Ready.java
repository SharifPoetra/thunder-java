package com.sharif.thunder.events;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Ready extends ListenerAdapter {
  
  @Override
  public void onReady(ReadyEvent event) {
    System.out.println(event.getJDA().getSelfUser().getAsTag() + " is ready!!");
  } 
} 